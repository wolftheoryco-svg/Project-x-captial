package com.example.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Gemini REST request/response definitions using Moshi ---

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null,
    @Json(name = "tools") val tools: List<Tool>? = null,
    @Json(name = "systemInstruction") val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null,
    @Json(name = "inlineData") val inlineData: InlineData? = null
)

@JsonClass(generateAdapter = true)
data class InlineData(
    @Json(name = "mimeType") val mimeType: String,
    @Json(name = "data") val data: String // base64 encoded
)

@JsonClass(generateAdapter = true)
data class Tool(
    @Json(name = "googleSearch") val googleSearch: GoogleSearch? = null,
    @Json(name = "functionDeclarations") val functionDeclarations: List<FunctionDeclaration>? = null
)

@JsonClass(generateAdapter = true)
class GoogleSearch // Empty class for grounding representation

@JsonClass(generateAdapter = true)
data class FunctionDeclaration(
    val name: String,
    val description: String,
    val parameters: FunctionParameters
)

@JsonClass(generateAdapter = true)
data class FunctionParameters(
    val type: String = "OBJECT",
    val properties: Map<String, FunctionProperty>,
    val required: List<String>? = null
)

@JsonClass(generateAdapter = true)
data class FunctionProperty(
    val type: String,
    val description: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "responseMimeType") val responseMimeType: String? = null,
    @Json(name = "temperature") val temperature: Double? = null,
    @Json(name = "responseModalities") val responseModalities: List<String>? = null,
    @Json(name = "imageConfig") val imageConfig: ImageConfig? = null,
    @Json(name = "thinkingConfig") val thinkingConfig: ThinkingConfig? = null
)

@JsonClass(generateAdapter = true)
data class ImageConfig(
    @Json(name = "aspectRatio") val aspectRatio: String = "1:1",
    @Json(name = "imageSize") val imageSize: String = "1K"
)

@JsonClass(generateAdapter = true)
data class ThinkingConfig(
    @Json(name = "thinkingLevel") val thinkingLevel: String // "low", "medium", "high"
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>?,
    @Json(name = "promptFeedback") val promptFeedback: PromptFeedback? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content?,
    @Json(name = "finishReason") val finishReason: String? = null,
    @Json(name = "groundingMetadata") val groundingMetadata: GroundingMetadata? = null
)

@JsonClass(generateAdapter = true)
data class GroundingMetadata(
    @Json(name = "groundingChunks") val groundingChunks: List<GroundingChunk>? = null,
    @Json(name = "searchEntryPoint") val searchEntryPoint: SearchEntryPoint? = null
)

@JsonClass(generateAdapter = true)
data class GroundingChunk(
    @Json(name = "web") val web: WebSource? = null
)

@JsonClass(generateAdapter = true)
data class WebSource(
    val uri: String?,
    val title: String?
)

@JsonClass(generateAdapter = true)
data class SearchEntryPoint(
    val renderedContent: String?
)

@JsonClass(generateAdapter = true)
data class PromptFeedback(
    val blockReason: String? = null
)

// --- Veo Video Generator REST definitions ---

@JsonClass(generateAdapter = true)
data class GenerateVideosRequest(
    @Json(name = "prompt") val prompt: String,
    @Json(name = "config") val config: VeoConfig? = null
)

@JsonClass(generateAdapter = true)
data class VeoConfig(
    @Json(name = "numberOfVideos") val numberOfVideos: Int = 1,
    @Json(name = "resolution") val resolution: String = "720p",
    @Json(name = "aspectRatio") val aspectRatio: String = "16:9"
)

// --- Retrofit interface definition ---

interface GeminiApiService {
    @POST("v1beta/models/{model}:generateContent")
    suspend fun generateContent(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse

    @POST("v1beta/models/{model}:generateVideos")
    suspend fun generateVideos(
        @Path("model") model: String,
        @Query("key") apiKey: String,
        @Body request: GenerateVideosRequest
    ): ResponseBody // Returns operation info, or mockup video metadata
}

// --- Retrofit builder ---

object NetworkModule {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()

    val geminiService: GeminiApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
        retrofit.create(GeminiApiService::class.java)
    }
}
