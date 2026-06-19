package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val email: String,
    val displayName: String?,
    val balance: Double = 100000.0, // Start with $100k demo capital
    val isKillSwitchOn: Boolean = false,
    val riskMarginPct: Float = 1.0f, // Max 1% risk per trade default
    val dailyLossLimit: Double = 5000.0, // Max $5k daily loss
    val weeklyLossLimit: Double = 15000.0,
    val isAuthenticated: Boolean = false // Track sign-in state
)

@Entity(tableName = "trade_logs")
data class TradeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val symbol: String, // e.g., "EUR/USD", "XAU/USD", "BTC/USD"
    val orderType: String, // "BUY" or "SELL"
    val entryPrice: Double,
    val exitPrice: Double? = null,
    val currentPrice: Double,
    val volume: Double, // Lot size
    val pnl: Double = 0.0,
    val status: String, // "OPEN" or "CLOSED"
    val openTimestamp: Long = System.currentTimeMillis(),
    val closeTimestamp: Long? = null,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val triggerReason: String? = null // Explanation of trade trigger
)

@Entity(tableName = "agent_status")
data class AgentStatusEntity(
    @PrimaryKey val agentName: String, // "Eyes", "Brain", "Filter", "Judge", "Shield", "Hands", "Broker", "Monitor", "Teacher", "Library"
    val role: String,
    val isActive: Boolean = true,
    val currentMetric: String = "Idle",
    val factorWeight: Float = 0.25f, // Tuned by TeacherAgent
    val lastThought: String = "Standing by for market analysis...",
    val lastUpdateTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "market_data")
data class MarketDataEntity(
    @PrimaryKey val symbol: String,
    val price: Double,
    val changePct: Float,
    val trend: String, // "BULLISH", "BEARISH", "NEUTRAL"
    val timestamp: Long = System.currentTimeMillis()
)
