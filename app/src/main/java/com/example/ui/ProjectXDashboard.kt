package com.example.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import com.example.data.local.AgentStatusEntity
import com.example.data.local.MarketDataEntity
import com.example.data.local.TradeEntity
import com.example.data.local.UserEntity
import kotlinx.coroutines.delay

// --- Modern Immersive Slate/Cyan Theme Colors ---
val CyberDark = Color(0xFF020617)    // Deep Slate background
val CyberSurface = Color(0xFF0F172A) // Slate-900 container base
val CyberCard = Color(0xFF1E293B)    // Slate-800 border and card fill
val CyberPrimary = Color(0xFF22D3EE) // Luminous high-tech Cyan-400
val CyberGreen = Color(0xFF10B981)   // Vibrant Emerald-500 for active trading / profits
val CyberRed = Color(0xFFEF4444)     // Intense Red-500 security Kill-switch
val CyberGray = Color(0xFF94A3B8)    // Neutral Slate-400 placeholder and structural borders
val CyberBlue = Color(0xFF06B6D4)    // Active Cyan-500 accent color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectXDashboardScreen(
    viewModel: ProjectXViewModel,
    modifier: Modifier = Modifier
) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val isSimulationRunning by viewModel.isSimulationRunning.collectAsStateWithLifecycle()
    val firebaseStatus by viewModel.firebaseStatus.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CyberDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (userProfile?.isKillSwitchOn == true) CyberRed else if (isSimulationRunning) CyberGreen else CyberGray)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Project X Capital",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        )
                    }
                },
                actions = {
                    // Firebase state badge
                    Surface(
                        color = if (userProfile?.isAuthenticated == true) CyberGreen.copy(alpha = 0.15f) else CyberSurface,
                        border = BorderStroke(1.dp, if (userProfile?.isAuthenticated == true) CyberGreen else CyberGray.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(
                            text = if (userProfile?.isAuthenticated == true) "SIGNED IN" else "SANDBOX",
                            color = if (userProfile?.isAuthenticated == true) CyberGreen else Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = CyberDark,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = CyberSurface,
                tonalElevation = 8.dp,
                modifier = Modifier.navigationBarsPadding()
            ) {
                NavigationBarItem(
                    selected = activeTab == "dashboard",
                    onClick = { viewModel.selectTab("dashboard") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard Tracker") },
                    label = { Text("Dashboard", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberPrimary,
                        selectedTextColor = CyberPrimary,
                        unselectedIconColor = CyberGray,
                        unselectedTextColor = CyberGray,
                        indicatorColor = CyberCard
                    ),
                    modifier = Modifier.testTag("nav_btn_dashboard")
                )
                NavigationBarItem(
                    selected = activeTab == "chat",
                    onClick = { viewModel.selectTab("chat") },
                    icon = { Icon(Icons.Default.Search, contentDescription = "AI Chat Consensus") },
                    label = { Text("Agents", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberPrimary,
                        selectedTextColor = CyberPrimary,
                        unselectedIconColor = CyberGray,
                        unselectedTextColor = CyberGray,
                        indicatorColor = CyberCard
                    ),
                    modifier = Modifier.testTag("nav_btn_chat")
                )
                NavigationBarItem(
                    selected = activeTab == "media",
                    onClick = { viewModel.selectTab("media") },
                    icon = { Icon(Icons.Default.PlayArrow, contentDescription = "AI Generation Labs") },
                    label = { Text("AI Lab", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberPrimary,
                        selectedTextColor = CyberPrimary,
                        unselectedIconColor = CyberGray,
                        unselectedTextColor = CyberGray,
                        indicatorColor = CyberCard
                    ),
                    modifier = Modifier.testTag("nav_btn_media")
                )
                NavigationBarItem(
                    selected = activeTab == "settings",
                    onClick = { viewModel.selectTab("settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Broker Risk Controls") },
                    label = { Text("Settings", fontSize = 11.sp, fontFamily = FontFamily.Monospace) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = CyberPrimary,
                        selectedTextColor = CyberPrimary,
                        unselectedIconColor = CyberGray,
                        unselectedTextColor = CyberGray,
                        indicatorColor = CyberCard
                    ),
                    modifier = Modifier.testTag("nav_btn_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(CyberDark)
        ) {
            when (activeTab) {
                "dashboard" -> DashboardView(viewModel)
                "chat" -> AgentsChatView(viewModel)
                "media" -> AiMediaLabView(viewModel)
                "settings" -> SettingsAndBrokerControlsView(viewModel)
            }
        }
    }
}

// --- SUB-VIEW 1: MASTER QUANTUM DASHBOARD ---
@Composable
fun DashboardView(viewModel: ProjectXViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val marketData by viewModel.marketData.collectAsStateWithLifecycle()
    val allTrades by viewModel.allTrades.collectAsStateWithLifecycle()
    val agentStatuses by viewModel.agentStatuses.collectAsStateWithLifecycle()
    val isSimulationRunning by viewModel.isSimulationRunning.collectAsStateWithLifecycle()
    val terminalLogs by viewModel.terminalLogs.collectAsStateWithLifecycle()

    var selectedDetailAgent by remember { mutableStateOf<AgentStatusEntity?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // PORTFOLIO METRICS CARD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CAPITAL DEPLOYED STATUS",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "\$${String.format("%,.2f", userProfile?.balance ?: 100000.0)}",
                            color = Color.White,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace
                        )
                        IconButton(
                            onClick = { viewModel.toggleSimulation() },
                            modifier = Modifier.testTag("play_pause_sim")
                        ) {
                            Icon(
                                imageVector = if (isSimulationRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                                contentDescription = "Toggle Simulation",
                                tint = if (isSimulationRunning) CyberRed else CyberGreen,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Open Float PnL", fontSize = 10.sp, color = CyberGray, fontFamily = FontFamily.Monospace)
                            val floatingPnl = allTrades.filter { it.status == "OPEN" }.sumOf { it.pnl }
                            Text(
                                text = (if (floatingPnl >= 0) "+\$" else "-\$") + String.format("%.2f", Math.abs(floatingPnl)),
                                color = if (floatingPnl >= 0) CyberGreen else CyberRed,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Risk Parameter", fontSize = 10.sp, color = CyberGray, fontFamily = FontFamily.Monospace)
                            Text(
                                text = "Max ${userProfile?.riskMarginPct ?: 1.0}% per trade",
                                color = CyberPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // REAL-TIME CANVAS TICK CHART
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "QUANT MARKET CONCURRENCY",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Synchronized Gold (XAU) & BTC/USD synthetic live telemetry feeds.",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Sparkline/Line Chart drawn in custom responsive canvas
                    val goldPrice = marketData.firstOrNull { it.symbol == "XAU/USD" }?.price ?: 2320.0
                    val btcPrice = marketData.firstOrNull { it.symbol == "BTC/USD" }?.price ?: 67000.0

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .background(CyberDark, shape = RoundedCornerShape(8.dp))
                            .border(1.dp, CyberCard, shape = RoundedCornerShape(8.dp))
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw baseline grid
                            drawLine(Color.White.copy(alpha = 0.05f), start = androidx.compose.ui.geometry.Offset(0f, h/2), end = androidx.compose.ui.geometry.Offset(w, h/2), strokeWidth = 1f)
                            drawLine(Color.White.copy(alpha = 0.05f), start = androidx.compose.ui.geometry.Offset(w/4, 0f), end = androidx.compose.ui.geometry.Offset(w/4, h), strokeWidth = 1f)
                            drawLine(Color.White.copy(alpha = 0.05f), start = androidx.compose.ui.geometry.Offset(w/2, 0f), end = androidx.compose.ui.geometry.Offset(w/2, h), strokeWidth = 1f)
                            drawLine(Color.White.copy(alpha = 0.05f), start = androidx.compose.ui.geometry.Offset(w*3/4, 0f), end = androidx.compose.ui.geometry.Offset(w*3/4, h), strokeWidth = 1f)

                            // Generate synthetic path representing algorithm consensus
                            val pathGold = Path()
                            val pathBtc = Path()
                            val steps = 8
                            val stepX = w / steps

                            // Gold path coordinates (green)
                            pathGold.moveTo(0f, h * 0.7f)
                            for (i in 1..steps) {
                                val factor = Math.sin((i + goldPrice * 0.01)) * 12 + h * 0.5f
                                pathGold.lineTo(i * stepX, factor.toFloat())
                            }
                            drawPath(pathGold, CyberGreen, style = Stroke(width = 3f))

                            // BTC path coordinates (cyan)
                            pathBtc.moveTo(0f, h * 0.4f)
                            for (i in 1..steps) {
                                val factor = Math.cos((i + btcPrice * 0.0001)) * 25 + h * 0.5f
                                pathBtc.lineTo(i * stepX, factor.toFloat())
                            }
                            drawPath(pathBtc, CyberBlue, style = Stroke(width = 3f))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        marketData.forEach { asset ->
                            Column {
                                Text(asset.symbol, fontSize = 10.sp, color = CyberGray, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text(
                                    text = String.format(if (asset.symbol.contains("BTC")) "%.1f" else "%.4f", asset.price),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = String.format("%+.2f%%", asset.changePct),
                                    color = if (asset.changePct >= 0) CyberGreen else CyberRed,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // THE 10 AGENT CONSENSUS TEAM GRID
        item {
            Column {
                Text(
                    text = "SPEC-AGENTS CLUSTER CONSENSUS MATRIX",
                    color = CyberPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Text(
                    text = "High-fidelity grid displaying connection health, execution logs, and network status for all 10 nodes.",
                    color = CyberGray,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        }

        // Display agents in a robust card-based grid layout chunked in pairs
        val agentPairs = agentStatuses.chunked(2)
        items(agentPairs) { pair ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { agent ->
                    AgentGridCard(
                        agent = agent,
                        terminalLogs = terminalLogs,
                        onClick = { selectedDetailAgent = agent },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (pair.size < 2) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        // ACTIVE POSITIONS
        item {
            Column {
                Text(
                    text = "ACTIVE FLOATING TRADES",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val activeTrades = allTrades.filter { it.status == "OPEN" }
                if (activeTrades.isEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Standing by. Active strategy consensus looking for market traps.",
                                color = CyberGray,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    activeTrades.forEach { trade ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            color = if (trade.orderType == "BUY") CyberGreen.copy(alpha = 0.15f) else CyberRed.copy(alpha = 0.15f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = trade.orderType,
                                                color = if (trade.orderType == "BUY") CyberGreen else CyberRed,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = trade.symbol,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Volume: ${trade.volume} Lots | Entry: ${String.format("%.4f", trade.entryPrice)}",
                                        fontSize = 10.sp,
                                        color = CyberGray,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    val profit = trade.pnl
                                    Text(
                                        text = (if (profit >= 0) "+\$" else "-\$") + String.format("%.2f", Math.abs(profit)),
                                        color = if (profit >= 0) CyberGreen else CyberRed,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = "Now: ${String.format("%.4f", trade.currentPrice)}",
                                        color = CyberGray,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // CLOSED HISTORIC TRADES
        item {
            Column {
                Text(
                    text = "HISTORIC STRATEY REALIZATIONS",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val closedTrades = allTrades.filter { it.status == "CLOSED" }.take(5)
                if (closedTrades.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No closed trade records registered yet.", color = CyberGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                } else {
                    closedTrades.forEach { trade ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "${trade.orderType} ${trade.symbol}",
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = trade.triggerReason ?: "Archived Consensus",
                                            color = CyberGray,
                                            fontSize = 9.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                                Text(
                                    text = (if (trade.pnl >= 0) "+\$" else "-\$") + String.format("%.2f", Math.abs(trade.pnl)),
                                    color = if (trade.pnl >= 0) CyberGreen.copy(alpha = 0.8f) else CyberRed.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // LIVE AGENT LOG TERMINAL STREAM
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LIVE QUANT TRADING TELEMETRY",
                            color = CyberPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CyberBlue)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(CyberDark, shape = RoundedCornerShape(6.dp))
                            .padding(8.dp)
                    ) {
                        LazyColumn(reverseLayout = true, modifier = Modifier.fillMaxSize()) {
                            items(terminalLogs.reversed()) { logLine ->
                                Text(
                                    text = logLine,
                                    color = if (logLine.contains("System:")) CyberBlue
                                    else if (logLine.contains("Shield:")) CyberRed
                                    else if (logLine.contains("Eyes:") || logLine.contains("Hands:")) CyberGreen
                                    else if (logLine.contains("Brain:") || logLine.contains("Teacher:")) CyberPrimary
                                    else Color.White,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Agent Details popup
    selectedDetailAgent?.let { agent ->
        Dialog(onDismissRequest = { selectedDetailAgent = null }) {
            Surface(
                color = CyberSurface,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, CyberPrimary),
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "AGENT NODE PROFILE",
                        color = CyberPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Agent [${agent.agentName}]",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Responsibility: ${agent.role}",
                        color = CyberBlue,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    HorizontalDivider(color = CyberCard, modifier = Modifier.padding(bottom = 12.dp))

                    Text("Tuned Weight Factor", color = CyberGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text("${agent.factorWeight * 100}% Influence Weight", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Current Telemetry Node Thought", color = CyberGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Text(
                        text = agent.lastThought,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { selectedDetailAgent = null },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCard),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("CLOSE OVERVIEW", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }
    }
}

// --- SUB-VIEW 2: MULTI-AGENT CHAT CONSENSUS ROOM ---
data class AgentRoleInfo(
    val index: Int,
    val name: String,
    val icon: String,
    val role: String,
    val mainJob: String,
    val mostImportantFor: String,
    val duties: List<String>,
    val relevance: String,
    val analogy: String,
    val accentColor: Color,
    val activeState: String
)

@Composable
fun AgentsChatView(viewModel: ProjectXViewModel) {
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val selectedChatAgent by viewModel.selectedChatAgent.collectAsStateWithLifecycle()
    val selectedChatModel by viewModel.selectedChatModel.collectAsStateWithLifecycle()
    val groundingEnabled by viewModel.groundingEnabled.collectAsStateWithLifecycle()
    val isDiagnosing by viewModel.isDiagnosing.collectAsStateWithLifecycle()
    val diagnosticStep by viewModel.diagnosticStep.collectAsStateWithLifecycle()
    val diagnosticMessage by viewModel.diagnosticMessage.collectAsStateWithLifecycle()

    var textInput by remember { mutableStateOf("") }
    val keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)

    // Sub-tab selection: "matrix" for grid view, "directory" for 10 agents team profiles, "dialog" for chat console
    var activeAgentsSubTab by remember { mutableStateOf("matrix") }

    val agentsAvailableForChat = listOf(
        "General Partner (Hedge Fund Lead)",
        "Brain Agent (Intelligence Lead)",
        "Shield Agent (Risk Lead)",
        "Teacher Agent (RL Learner)"
    )

    val specialistsDirectory = remember {
        listOf(
            AgentRoleInfo(
                index = 1,
                name = "Eyes",
                icon = "👁️",
                role = "Data Collection",
                mainJob = "Gather market data & news",
                mostImportantFor = "Information",
                duties = listOf(
                    "Collects real-time market data (prices, volume)",
                    "Fetches news and economic calendar events",
                    "Monitors active social sentiment indices"
                ),
                relevance = "Without good data, no good decisions can be made.",
                analogy = "Like a news reporter who gathers information from the market.",
                accentColor = CyberBlue,
                activeState = "TELEMETRY SCANNING"
            ),
            AgentRoleInfo(
                index = 2,
                name = "Brain",
                icon = "🧠",
                role = "Market Intelligence",
                mainJob = "Calculate factors",
                mostImportantFor = "Analysis",
                duties = listOf(
                    "Calculates Trend Momentum vectors (strength)",
                    "Computes Hold Interest Carry coefficients",
                    "Tracks Intrinsic mean reversion Values",
                    "Determines dynamic underlying Volatility ranges"
                ),
                relevance = "This is the core intelligence that decides if a trade has potential.",
                analogy = "Like a senior analyst who studies the market deeply.",
                accentColor = CyberPrimary,
                activeState = "COMPUTING MODEL EDGES"
            ),
            AgentRoleInfo(
                index = 3,
                name = "Filter",
                icon = "🔍",
                role = "Signal Confirmation",
                mainJob = "Remove weak signals",
                mostImportantFor = "Quality control",
                duties = listOf(
                    "Checks if the signal from Brain is strong enough",
                    "Removes volatile and low-probability factor noises",
                    "Confirms clean alignment across different factors"
                ),
                relevance = "Prevents trading on weak, fake, or momentary market noise.",
                analogy = "Like a strict editor who removes bad articles before publishing.",
                accentColor = Color(0xFFC084FC), // Purple
                activeState = "SIGNAL PURIFICATION ON"
            ),
            AgentRoleInfo(
                index = 4,
                name = "Judge",
                icon = "⚖️",
                role = "Scoring Engine",
                mainJob = "Score trades 1–10",
                mostImportantFor = "Decision making",
                duties = listOf(
                    "Gives a final consensus score (1 to 10) to every potential trade",
                    "Decides whether to execute or instantly reject proposed lots",
                    "Integrates real-time feeds from both Filter & Brain Agent"
                ),
                relevance = "Only high-scoring, multi-aligned trades are allowed to bypass the consensus gate.",
                analogy = "Like a federal judge who delivers a final, absolute verdict on a case.",
                accentColor = Color(0xFFFBBF24), // Amber/Orange
                activeState = "CONSENSUS ALIGNED"
            ),
            AgentRoleInfo(
                index = 5,
                name = "Shield",
                icon = "🛡️",
                role = "Risk + Kill Switch",
                mainJob = "Protect account",
                mostImportantFor = "Safety",
                duties = listOf(
                    "Calculates precise entry size allocation limits (max 1% margin)",
                    "Coordinates daily and weekly loss drawdown limits",
                    "Triggers system-wide emergency Kill Switches in high drawdown"
                ),
                relevance = "Ensures absolute asset preservation, shielding capital from sudden tail risks.",
                analogy = "Like an elite personal bodyguard who protects the money.",
                accentColor = CyberRed,
                activeState = "ACTIVE DRAWDOWN NORMAL"
            ),
            AgentRoleInfo(
                index = 6,
                name = "Hands",
                icon = "🧤",
                role = "Trade Execution",
                mainJob = "Execute trades",
                mostImportantFor = "Action",
                duties = listOf(
                    "Receives fully certified consensus orders from Shield",
                    "Translates signals into mechanical transactions lots",
                    "Dispatches structural buy and sell tickets"
                ),
                relevance = "Turns abstract analytical determinations into real, active, executed trades.",
                analogy = "Like the hands that physically place the trade in the market.",
                accentColor = CyberGreen,
                activeState = "LOT STAGING SECURED"
            ),
            AgentRoleInfo(
                index = 7,
                name = "Broker",
                icon = "🏦",
                role = "Order Routing",
                mainJob = "Get best execution",
                mostImportantFor = "Efficiency",
                duties = listOf(
                    "Selects optimal liquid venues to clear the trade lot",
                    "Minimizes slippage ratios and technical access fees",
                    "Dynamically toggles routes across multi-broker networks"
                ),
                relevance = "Ensures clean fills and locks in the best possible execution parameters.",
                analogy = "Like a smart taxi driver who knows the absolute fastest city shortcut.",
                accentColor = CyberBlue,
                activeState = "BROKER CLEARANCE CONNECTED"
            ),
            AgentRoleInfo(
                index = 8,
                name = "Monitor",
                icon = "🖥️",
                role = "Post-Trade Tracking",
                mainJob = "Track open positions & P&L",
                mostImportantFor = "Control",
                duties = listOf(
                    "Monitors active open floating exposures continuously",
                    "Calculates floating unrealized profits and losses margins",
                    "Surfaces early exit flags if factor strength trends break"
                ),
                relevance = "Tracks how trades perform in the wild and keeps the system updated on net exposure.",
                analogy = "Like an alert accountant auditing every single penny as it moves.",
                accentColor = CyberPrimary,
                activeState = "REAL-TIME LOG STREAMING"
            ),
            AgentRoleInfo(
                index = 9,
                name = "Teacher",
                icon = "🎓",
                role = "Reinforcement Learning",
                mainJob = "Learn and improve strategy",
                mostImportantFor = "Improvement",
                duties = listOf(
                    "Audits historic win and loss log event parameters",
                    "Runs reinforcement feedback iterations to maximize strategy rewards",
                    "Increments or decrements factor weight settings recursively"
                ),
                relevance = "Ensures the capital pool grows continuously smarter instead of remaining static.",
                analogy = "Like an elite head coach studying game tapes to optimize next week's tactics.",
                accentColor = Color(0xFFFB7185), // Rose
                activeState = "NEURAL OPTIMIZER READY"
            ),
            AgentRoleInfo(
                index = 10,
                name = "Library",
                icon = "📚",
                role = "Knowledge + Event Store",
                mainJob = "Store everything",
                mostImportantFor = "Memory & Learning",
                duties = listOf(
                    "Commits events, logs, chat transcripts, and profile values to disk",
                    "Yields historical event records to TeacherAgent for backtests",
                    "Maintains the strict distributed event sourcing transaction ledgers"
                ),
                relevance = "Provides a transparent memory core enabling auditability and model refinement.",
                analogy = "Like a majestic library or secure database retaining records of every historic transaction.",
                accentColor = CyberGray,
                activeState = "EVENT STORE PERSISTENCE OK"
            )
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        
        // Horizontal Switcher for details, matrix and dialogue
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val tabs = listOf(
                Triple("matrix", "TEAM MATRIX 📊", "agent_matrix_tab"),
                Triple("directory", "ROLE DETAILS 🤖", "agent_details_tab"),
                Triple("dialog", "AI CONSOLE 💬", "agent_chat_tab")
            )
            tabs.forEach { (tabId, label, tag) ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (activeAgentsSubTab == tabId) CyberPrimary.copy(alpha = 0.15f) else CyberSurface)
                        .border(
                            width = 1.dp,
                            color = if (activeAgentsSubTab == tabId) CyberPrimary else CyberCard,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { activeAgentsSubTab = tabId }
                        .padding(vertical = 11.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (activeAgentsSubTab == tabId) CyberPrimary else Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        if (activeAgentsSubTab == "matrix") {
            // --- TAB SELECTOR 1: TEAM MATRIX VIEW (Requested Table / Grid) ---
            Column(modifier = Modifier.fillMaxSize()) {
                // Dynamic active Diagnostics & hand-checking system
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberSurface),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isDiagnosing) CyberPrimary else CyberCard
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isDiagnosing) CyberPrimary.copy(alpha = 0.15f)
                                            else CyberGreen.copy(alpha = 0.15f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isDiagnosing) {
                                        CircularProgressIndicator(
                                            progress = diagnosticStep / 10f,
                                            modifier = Modifier.size(22.dp),
                                            color = CyberPrimary,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = CyberGreen,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "SWARM SYSTEM DIAGNOSTICS",
                                        color = CyberPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = if (isDiagnosing) "Diagnosing 10 swarm agents..." else "All 10 Agent ports verified.",
                                        color = Color.White.copy(alpha = 0.82f),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.runAgentDiagnostics() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isDiagnosing) CyberPrimary.copy(alpha = 0.12f) else CyberPrimary,
                                    contentColor = if (isDiagnosing) CyberPrimary else CyberDark
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = if (isDiagnosing) BorderStroke(1.dp, CyberPrimary) else null,
                                enabled = !isDiagnosing,
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("run_diagnostics_button")
                            ) {
                                Text(
                                    text = if (isDiagnosing) "RUNNING..." else "CONNECT & CHECK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress dynamic log text
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberDark)
                                .border(0.5.dp, CyberCard, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "> $diagnosticMessage",
                                    color = if (isDiagnosing) CyberPrimary else CyberGreen,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (isDiagnosing) {
                                    Text(
                                        text = "${diagnosticStep * 10}%",
                                        color = CyberPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                } else {
                                    Text(
                                        text = "PASS",
                                        color = CyberGreen,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                var searchQuery by remember { mutableStateOf("") }
                val filteredAgents = remember(searchQuery, specialistsDirectory) {
                    specialistsDirectory.filter {
                        it.name.contains(searchQuery, ignoreCase = true) ||
                                it.role.contains(searchQuery, ignoreCase = true) ||
                                it.mainJob.contains(searchQuery, ignoreCase = true) ||
                                it.mostImportantFor.contains(searchQuery, ignoreCase = true)
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Filter matrix (e.g. 'Safety', 'Brain')...", fontSize = 11.sp, color = CyberGray) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = CyberGray, modifier = Modifier.size(16.dp)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberCard
                    ),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                        .testTag("matrix_filter_input")
                )

                // Tabular Scroll Container
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberDark),
                    border = BorderStroke(1.dp, CyberCard),
                    modifier = Modifier.weight(1f).fillMaxWidth()
                ) {
                    Column {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(CyberSurface)
                                .border(width = (0.5).dp, color = CyberCard)
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ID", color = CyberPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.width(22.dp))
                            Text("AGENT", color = CyberPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1f))
                            Text("ROLE", color = CyberPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1.3f))
                            Text("MAIN JOB", color = CyberPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1.4f))
                            Text("MOST IMPORTANT", color = CyberPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1.1f))
                        }

                        // Rows
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            if (filteredAgents.isEmpty()) {
                                item {
                                    Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                                        Text("No matching agents found in matrix", color = CyberGray, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            } else {
                                items(filteredAgents) { agent ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { activeAgentsSubTab = "directory" } // Redirect to directory deep profiles
                                            .border(width = (0.5).dp, color = CyberCard)
                                            .padding(horizontal = 8.dp, vertical = 12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        // ID
                                        Text(
                                            text = "${agent.index}",
                                            color = agent.accentColor,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.width(22.dp)
                                        )

                                        // AGENT NAME
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = agent.icon, fontSize = 11.sp, modifier = Modifier.padding(end = 4.dp))
                                            Text(
                                                text = agent.name,
                                                color = Color.White,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }

                                        // ROLE
                                        Text(
                                            text = agent.role,
                                            color = Color.White.copy(alpha = 0.9f),
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.weight(1.3f),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        // MAIN JOB
                                        Text(
                                            text = agent.mainJob,
                                            color = Color.White.copy(alpha = 0.82f),
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace,
                                            modifier = Modifier.weight(1.4f),
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        // MAIN VALUE BADGE
                                        Box(
                                            modifier = Modifier
                                                .weight(1.1f)
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(agent.accentColor.copy(alpha = 0.12f))
                                                .border(width = 0.5.dp, color = agent.accentColor.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = agent.mostImportantFor.uppercase(),
                                                color = agent.accentColor,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "💡 Click any row of the matrix above to pivot to detailed parameters, checklists, and visual analogies.",
                    color = CyberGray,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else if (activeAgentsSubTab == "directory") {
            // SPECIALIST DIRECTORY WALL VIEW (Detailed cards)
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "10 SPECIALIZED AI FUNNEL INSTANCES",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "These specialized agents function in perfect consensus like an elite, high-frequency quant trading team. Each executes real-time pipeline roles synchronously.",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                items(specialistsDirectory) { agent ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberSurface),
                        border = BorderStroke(1.dp, agent.accentColor.copy(alpha = 0.35f)),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("agent_card_${agent.index}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            // Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(agent.accentColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = agent.icon, fontSize = 14.sp)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Agent ${String.format("%02d", agent.index)}",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                // Status Indicator
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(agent.accentColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = agent.activeState,
                                        color = agent.accentColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Name & Title Role
                            Text(
                                text = agent.name.uppercase() + " AGENT",
                                color = agent.accentColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "ROLE: ${agent.role}",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                            )

                            // Strategic Main Job & Focus row
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(CyberDark)
                                        .border(width = 0.5.dp, color = CyberCard, shape = RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "MAIN JOB: " + agent.mainJob,
                                        color = CyberGray,
                                        fontSize = 8.5.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(agent.accentColor.copy(alpha = 0.12f))
                                        .border(width = 0.5.dp, color = agent.accentColor.copy(alpha = 0.3f), shape = RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = agent.mostImportantFor.uppercase(),
                                        color = agent.accentColor,
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            // Duties List
                            Surface(
                                color = CyberDark,
                                border = BorderStroke(1.dp, CyberCard),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "CORE DUTIES & FUNCTIONS:",
                                        color = CyberGray,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                    agent.duties.forEach { duty ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(bottom = 4.dp),
                                            verticalAlignment = Alignment.Top
                                        ) {
                                            Text(
                                                text = "•",
                                                color = agent.accentColor,
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Monospace,
                                                modifier = Modifier.padding(end = 6.dp)
                                            )
                                            Text(
                                                text = duty,
                                                color = Color.White.copy(alpha = 0.85f),
                                                fontSize = 10.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }

                            // Relevance Info Box
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(agent.accentColor.copy(alpha = 0.08f))
                                    .border(1.dp, agent.accentColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Text(
                                        text = "WHY IT MATTERS:",
                                        color = agent.accentColor,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = agent.relevance,
                                        color = Color.White.copy(alpha = 0.9f),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Analogy Footer Box
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TEAM ANALOGY:",
                                    color = CyberGray,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(end = 6.dp)
                                )
                                Text(
                                    text = agent.analogy,
                                    color = CyberBlue,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }
        } else {
            // --- TAB SELECTOR 2: CHAT CONSOLE VIEW ---
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "QUANT AI DIALOG CONSOLE",
                        color = CyberPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Select Agent target dropdown
                    Text("Dialogue Target:", color = CyberGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        items(agentsAvailableForChat) { agent ->
                            val isSelected = agent == selectedChatAgent
                            Surface(
                                color = if (isSelected) CyberCard else CyberDark,
                                border = BorderStroke(1.dp, if (isSelected) CyberPrimary else CyberCard),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.clickable { viewModel.setChatAgent(agent) }
                            ) {
                                Text(
                                    text = agent.split(" ")[0] + " " + (agent.split(" ").getOrNull(1) ?: ""),
                                    color = if (isSelected) CyberPrimary else Color.White,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Model / Grounding row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Model: ", color = CyberGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberCard)
                                    .clickable {
                                        val nextModel = when (selectedChatModel) {
                                            GptModelType.FlashGeneric -> GptModelType.ProDeepThinking
                                            GptModelType.ProDeepThinking -> GptModelType.LiteLowLatency
                                            GptModelType.LiteLowLatency -> GptModelType.FlashGeneric
                                        }
                                        viewModel.setChatModel(nextModel)
                                    }
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = when (selectedChatModel) {
                                        GptModelType.FlashGeneric -> "Gemini 3.5-Flash"
                                        GptModelType.ProDeepThinking -> "Gemini 3.1-Pro (Thinking)"
                                        GptModelType.LiteLowLatency -> "Gemini LITE (Fast)"
                                    },
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Search Grounding: ", color = CyberGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Switch(
                                checked = groundingEnabled,
                                onCheckedChange = { viewModel.setGroundingEnabled(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = CyberPrimary,
                                    checkedTrackColor = CyberPrimary.copy(alpha = 0.4f),
                                    uncheckedThumbColor = CyberGray,
                                    uncheckedTrackColor = CyberSurface
                                ),
                                modifier = Modifier.scale(0.6f)
                            )
                        }
                    }
                }
            }

            // Chat logs viewport
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDark),
                border = BorderStroke(1.dp, CyberCard),
                modifier = Modifier.weight(1f).fillMaxWidth()
            ) {
                if (chatHistory.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Search, contentDescription = "Query Agent", tint = CyberGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Query the $selectedChatAgent directly regarding market conditions or factor calculations.",
                                color = CyberGray,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(chatHistory) { msg ->
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = if (msg.sender == "Operator") Alignment.End else Alignment.Start
                            ) {
                                Surface(
                                    color = if (msg.sender == "Operator") CyberCard else CyberSurface,
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, if (msg.sender == "Operator") CyberPrimary.copy(alpha = 0.5f) else CyberCard),
                                    modifier = Modifier.widthIn(max = 280.dp)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(
                                            text = msg.sender,
                                            color = if (msg.sender == "Operator") CyberPrimary else CyberBlue,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = msg.text,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace
                                        )

                                        if (msg.isSearchGroundingUsed && msg.groundedSources.isNotEmpty()) {
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Grounded Search Sources:",
                                                color = CyberPrimary,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 8.sp,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            msg.groundedSources.forEach { source ->
                                                Text(
                                                    text = "• ${source.first}",
                                                    color = CyberBlue,
                                                    fontSize = 8.sp,
                                                    fontFamily = FontFamily.Monospace,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (isChatLoading) {
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    CircularProgressIndicator(color = CyberPrimary, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Agent computing neural network nodes...", color = CyberGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Input bottom bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Compile queries for Agent consensus...", fontSize = 11.sp, color = CyberGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = CyberSurface,
                        unfocusedContainerColor = CyberSurface,
                        focusedBorderColor = CyberPrimary,
                        unfocusedBorderColor = CyberCard
                    ),
                    singleLine = true,
                    keyboardOptions = keyboardOptions,
                    textStyle = LocalTextStyle.current.copy(fontFamily = FontFamily.Monospace, fontSize = 12.sp),
                    modifier = Modifier.weight(1f).testTag("chat_input_field")
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            viewModel.submitChatMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberPrimary)
                        .size(52.dp)
                        .testTag("send_chat_btn")
                ) {
                    Icon(Icons.Default.Search, contentDescription = "Send Command", tint = CyberDark)
                }
            }
        }
    }
}

// --- SUB-VIEW 3: AI MEDIA GENERATION LAB ---
@Composable
fun AiMediaLabView(viewModel: ProjectXViewModel) {
    val generatedImageState by viewModel.generatedImageState.collectAsStateWithLifecycle()
    val isImageGenerating by viewModel.isImageGenerating.collectAsStateWithLifecycle()
    val imageError by viewModel.imageError.collectAsStateWithLifecycle()

    val veoVideoState by viewModel.veoVideoState.collectAsStateWithLifecycle()
    val isVeoGenerating by viewModel.isVeoGenerating.collectAsStateWithLifecycle()
    val veoError by viewModel.veoError.collectAsStateWithLifecycle()

    var imagePrompt by remember { mutableStateOf("Futuristic trading desk with multi-screen holographic quantum financial analysis") }
    var scaleOption by remember { mutableStateOf("1K") } // "1K", "2K", "4K"
    var selectedAspectRatio by remember { mutableStateOf("16:9") } // "1:1", "16:9", "9:16"

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "HIGH-DEFINITION IMAGE GENERATION MODEL",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Conforming to gemini-3-pro-image-preview with custom spatial configurations.",
                        color = CyberGray,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    OutlinedTextField(
                        value = imagePrompt,
                        onValueChange = { imagePrompt = it },
                        label = { Text("Asset Generation Prompt", color = CyberGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberCard
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Sizing / resolution selectors
                        Column {
                            Text("Resolution Size:", color = CyberGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("1K", "2K", "4K").forEach { size ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (scaleOption == size) CyberCard else CyberDark)
                                            .border(1.dp, if (scaleOption == size) CyberPrimary else CyberCard)
                                            .clickable { scaleOption = size }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(size, color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }

                        // Aspect Ratio selectors
                        Column {
                            Text("Aspect Ratio:", color = CyberGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("1:1", "16:9", "9:16").forEach { ratio ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(if (selectedAspectRatio == ratio) CyberCard else CyberDark)
                                            .border(1.dp, if (selectedAspectRatio == ratio) CyberPrimary else CyberCard)
                                            .clickable { selectedAspectRatio = ratio }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Text(ratio, color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.generateHighQualityImage(imagePrompt, scaleOption, selectedAspectRatio) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        enabled = !isImageGenerating
                    ) {
                        if (isImageGenerating) {
                            CircularProgressIndicator(color = CyberDark, modifier = Modifier.size(20.dp))
                        } else {
                            Text("COMPILE & GENERATE IMAGE", color = CyberDark, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    imageError?.let { err ->
                        Text(err, color = CyberRed, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(top = 8.dp))
                    }
                }
            }
        }

        // Display Generated Image output
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("GENERATED IMAGE VIEWPORT", color = CyberGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (generatedImageState != null) {
                        val bitmap = remember(generatedImageState) {
                            try {
                                val decodedBytes = Base64.decode(generatedImageState, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Generated Artwork Output",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(150.dp).background(CyberDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("Error rendering Decoded PNG.", color = CyberRed, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(CyberDark, shape = RoundedCornerShape(8.dp))
                                .border(1.dp, CyberCard, shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No compiled imagery loaded. Click Compile above.", color = CyberGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // VEO VIDEO ANIMATION GENERATOR (veo-3.1-fast-generate-preview)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "VEO FAST VIDEO ANIMATION ENGINE",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Animate custom quant scenes or images to cinematic standard video loop.",
                        color = CyberGray,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Button(
                        onClick = {
                            viewModel.animateImageToVideo(
                                "Animate the quantum financial desk displaying glowing charts, camera rotating in 3d",
                                selectedAspectRatio
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberBlue),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        enabled = !isVeoGenerating
                    ) {
                        if (isVeoGenerating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("ANIMATE TO VIDEO (16:9/9:16)", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }

                    veoError?.let { err ->
                        Text(err, color = CyberRed, fontSize = 10.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("ANIMATED RESULT TIMELINE", color = CyberGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(4.dp))

                    if (veoVideoState != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .background(CyberBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp))
                                .border(1.dp, CyberBlue, shape = RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Rendering OK", tint = CyberBlue, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Veo Video Loop Generated Successfully!", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                Text("Job ID: ${veoVideoState}", color = CyberGray, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .background(CyberDark, shape = RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No video looping state detected.", color = CyberGray, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }
    }
}

// --- SUB-VIEW 4: SETTINGS & RISK BROKER CONTROLS ---
data class TopologyNode(
    val id: String,
    val label: String,
    val icon: String,
    val category: String,
    val description: String,
    val connections: String,
    val activeStateLabel: String
)

@Composable
fun SettingsAndBrokerControlsView(viewModel: ProjectXViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val agentStatuses by viewModel.agentStatuses.collectAsStateWithLifecycle()
    val firebaseStatus by viewModel.firebaseStatus.collectAsStateWithLifecycle()
    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()

    var apiKeyInput by remember { mutableStateOf(customApiKey) }
    var currentRiskMargin by remember { mutableStateOf(userProfile?.riskMarginPct ?: 1.0f) }

    // Hard Reinforcement Learning factor weight fields
    var momWeight by remember { mutableStateOf(0.25f) }
    var carryWeight by remember { mutableStateOf(0.25f) }
    var valWeight by remember { mutableStateOf(0.25f) }
    var volWeight by remember { mutableStateOf(0.25f) }

    // Selected node state for systemic topology overview
    var selectedNodeId by remember { mutableStateOf("eyes_agent") }

    // Walkthrough steps state for interactive simulated trade flow
    var activeFlowStep by remember { mutableStateOf(1) }
    var flowPlaying by remember { mutableStateOf(false) }

    // External App Connection states
    var selectedConnectionTab by remember { mutableStateOf("dashboard") }
    var selectedRegistryLayer by remember { mutableStateOf("frontend") }

    // Core API Console, Database, & Folder states
    var selectedApiEndpoint by remember { mutableStateOf("GET /api/status") }
    var selectedDbTable by remember { mutableStateOf("trades") }
    var selectedFolderId by remember { mutableStateOf("root") }

    // Control automatic step sequence playing
    LaunchedEffect(flowPlaying) {
        if (flowPlaying) {
            while (flowPlaying) {
                delay(1800)
                activeFlowStep = if (activeFlowStep < 10) activeFlowStep + 1 else 1
            }
        }
    }

    val topologyNodes = remember {
        listOf(
            TopologyNode(
                id = "user_operator",
                label = "User / App",
                icon = "👤",
                category = "OPERATOR PORTAL",
                description = "The client console initiating secure authenticated trading simulations, parameter overrides, and system control payloads.",
                connections = "Routes strategy commands directly into Quantum Dashboard.",
                activeStateLabel = "ONLINE OPERATOR"
            ),
            TopologyNode(
                id = "dashboard_app",
                label = "Quantum Dashboard",
                icon = "📊",
                category = "CONTROL PLATFORM",
                description = "The centralized visual telemetry hub running native micro-interactions, responsive charts, and real-time execution outputs.",
                connections = "Orchestrates state rendering from the 10 Agent decision funnel, feeding telemetry stream logs to operator screens.",
                activeStateLabel = "SYNCHRONIZED"
            ),
            TopologyNode(
                id = "eyes_agent",
                label = "Eyes Agent",
                icon = "👁️",
                category = "10 AGENTS FUNNEL",
                description = "Ingests multi-source data feeds including Gold (XAU) prices and BTC/USD synthetic live price indicators to detect price action anomalies.",
                connections = "Feeds parsed indicators down the chain into Brain Agent for deep strategic evaluation.",
                activeStateLabel = "TELEMETRY SCANNING"
            ),
            TopologyNode(
                id = "brain_agent",
                label = "Brain Agent",
                icon = "🧠",
                category = "10 AGENTS FUNNEL",
                description = "The cognitive intelligence core processing quantitative factors (Mom, Carry, Val, Vol) to determine mathematical market edges.",
                connections = "Forwards raw strategic directives to Filter Agent for noise threshold evaluation.",
                activeStateLabel = "NEURAL INTERPOLATION ON"
            ),
            TopologyNode(
                id = "filter_agent",
                label = "Filter Agent",
                icon = "🔍",
                category = "10 AGENTS FUNNEL",
                description = "Applies strict statistical statistical confidence intervals to block low-probability noise, ensuring premium execution targets.",
                connections = "Routes purified trade signals to the Judge Agent for collective alignment checks.",
                activeStateLabel = "SIGNAL CONVERGENCE OK"
            ),
            TopologyNode(
                id = "judge_agent",
                label = "Judge Agent",
                icon = "⚖️",
                category = "10 AGENTS FUNNEL",
                description = "Iterates over and weights consensus inputs across multiple agents to confirm bullish or bearish trend alignment over 3 distinct timeframes.",
                connections = "Passes verified order signals to Shield Agent to trigger comprehensive safety verification checks.",
                activeStateLabel = "CONSENSUS ALIGNED"
            ),
            TopologyNode(
                id = "shield_agent",
                label = "Shield Agent",
                icon = "🛡️",
                category = "10 AGENTS FUNNEL",
                description = "Enforces risk mitigation limits, analyzing margin exposure thresholds (max 1% trade limit) and tracking the System Kill Switch state.",
                connections = "Validates trade safety parameters before certifying consensus packets to Hands Agent.",
                activeStateLabel = "RISK ENGINE PASS"
            ),
            TopologyNode(
                id = "hands_agent",
                label = "Hands Agent",
                icon = "🧤",
                category = "10 AGENTS FUNNEL",
                description = "Transforms certified consensus signals into transaction mechanics, packing trade payload lots and size allocations.",
                connections = "Dispatches secure order payloads to Broker Agent for platform order routing.",
                activeStateLabel = "LOT STAGING SECURED"
            ),
            TopologyNode(
                id = "broker_agent",
                label = "Broker Agent",
                icon = "🏦",
                category = "10 AGENTS FUNNEL",
                description = "Performs low-latency communication with simulated execution accounts, routing trades, updating fill states, and managing floating balances.",
                connections = "Directly triggers order logging events via the Library and updates live dashboard metrics.",
                activeStateLabel = "PORTFOLIO CONCURRENCY OK"
            ),
            TopologyNode(
                id = "monitor_agent",
                label = "Monitor Agent",
                icon = "🖥️",
                category = "FEEDBACK CORES",
                description = "Watches overall multi-agent health status, parsing runtime metrics and pushing direct diagnostic trace signals to logs.",
                connections = "Monitors runtime status and informs of system performance metrics.",
                activeStateLabel = "LOG STREAM STREAMING"
            ),
            TopologyNode(
                id = "teacher_agent",
                label = "Teacher Agent",
                icon = "🎓",
                category = "FEEDBACK CORES",
                description = "Runs background Reinforcement Learning simulations, comparing realized alpha metrics against expected rewards to re-tune weights.",
                connections = "Computes system feedback to optimize Brain Agent model weight configurations.",
                activeStateLabel = "RL MODEL TUNE READY"
            ),
            TopologyNode(
                id = "library_agent",
                label = "Library Agent",
                icon = "📚",
                category = "FEEDBACK CORES",
                description = "Acts as the unified system event store and long-term semantic knowledge memory, recording historical transaction logs.",
                connections = "Stores long-term records used by Teacher Agent for continuous backtests.",
                activeStateLabel = "KNOWLEDGE ENGINE OK"
            ),
            TopologyNode(
                id = "market_data",
                label = "Market Data",
                icon = "📈",
                category = "SYSTEMS LAYER",
                description = "External endpoints (e.g. yfinance, WebSocket telemetry streams) serving continuous high-precision currency quotes.",
                connections = "Directly ingested by Eyes class instances to drive evaluation loops.",
                activeStateLabel = "SYNTHETIC FEED SECURED"
            ),
            TopologyNode(
                id = "risk_engine",
                label = "Risk Engine",
                icon = "⚠️",
                category = "SYSTEMS LAYER",
                description = "Rigid hardware-level threshold manager holding active margin bounds, automatically implementing safety pullouts.",
                connections = "Directly controlled by Shield Agent, instantly closing open floats when breaching limit.",
                activeStateLabel = "SAFETY ON"
            ),
            TopologyNode(
                id = "event_store",
                label = "Event Store",
                icon = "🗄️",
                category = "SYSTEMS LAYER",
                description = "Sub-system database executing localized queries to persist historic trades, chat archives, and user profiles safely.",
                connections = "Connected to persistent Room SQLite storage layer.",
                activeStateLabel = "PERSISTED OK"
            )
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // SYSTEM OVERVIEW: HYPER-TOPOLOGY DIAGRAM
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberPrimary.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SYSTEM INTERACTIVE HYPER-TOPOLOGY",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap on any pipeline segment below to analyze real-time signal flow, data connection mappings, and agent dependencies within Project X Capital.",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Column layout demonstrating the vertical flow from the ASCII diagram
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // LAYER 1: USER OPERATOR CONSOLE
                        val isUserSelected = selectedNodeId == "user_operator"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isUserSelected) CyberCard else CyberDark)
                                .border(
                                    1.dp,
                                    if (isUserSelected) CyberPrimary else CyberCard,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedNodeId = "user_operator" }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("👤", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "User / Operator Console",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CyberGreen)
                                )
                            }
                        }

                        Text("▼", color = CyberPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        // LAYER 2: QUANTUM DASHBOARD
                        val isDashSelected = selectedNodeId == "dashboard_app"
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDashSelected) CyberCard else CyberDark)
                                .border(
                                    1.dp,
                                    if (isDashSelected) CyberPrimary else CyberCard,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { selectedNodeId = "dashboard_app" }
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("📊", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Quantum Terminal Dashboard",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CyberGreen)
                                )
                            }
                        }

                        Text("▼", color = CyberPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        // LAYER 3: 10 AI AGENTS CONSPIRACY GRID (Laid out in beautifully interactive rows representing funnel flow)
                        Text(
                            text = "10 AI AGENTS CONGESTION TUNNEL",
                            color = CyberGray,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        // ROW A of Funnel: Eyes ➔ Brain ➔ Filter ➔ Judge
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val rowANodes = listOf(
                                Pair("eyes_agent", "👁️"),
                                Pair("brain_agent", "🧠"),
                                Pair("filter_agent", "🔍"),
                                Pair("judge_agent", "⚖️")
                            )

                            rowANodes.forEachIndexed { index, pair ->
                                val (nid, icon) = pair
                                val isNSelected = selectedNodeId == nid
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isNSelected) CyberCard else CyberDark)
                                        .border(
                                            1.dp,
                                            if (isNSelected) CyberPrimary else CyberCard,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedNodeId = nid },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(icon, fontSize = 18.sp)
                                        Text(
                                            nid.split("_")[0].uppercase(),
                                            fontSize = 7.sp,
                                            color = if (isNSelected) CyberPrimary else CyberGray,
                                            maxLines = 1,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                if (index < rowANodes.size - 1) {
                                    Text("➔", color = CyberPrimary.copy(alpha = 0.5f), fontSize = 10.sp)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(0.85f),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("▼ (downward evaluation route)", color = CyberPrimary.copy(alpha = 0.5f), fontSize = 8.sp, fontFamily = FontFamily.Monospace)
                        }

                        // ROW B of Funnel: Shield ➔ Hands ➔ Broker
                        Row(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val rowBNodes = listOf(
                                Pair("shield_agent", "🛡️"),
                                Pair("hands_agent", "🧤"),
                                Pair("broker_agent", "🏦")
                            )

                            rowBNodes.forEachIndexed { index, pair ->
                                val (nid, icon) = pair
                                val isNSelected = selectedNodeId == nid
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isNSelected) CyberCard else CyberDark)
                                        .border(
                                            1.dp,
                                            if (isNSelected) CyberPrimary else CyberCard,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedNodeId = nid },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(icon, fontSize = 18.sp)
                                        Text(
                                            nid.split("_")[0].uppercase(),
                                            fontSize = 7.sp,
                                            color = if (isNSelected) CyberPrimary else CyberGray,
                                            maxLines = 1,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                if (index < rowBNodes.size - 1) {
                                    Text("➔", color = CyberPrimary.copy(alpha = 0.5f), fontSize = 10.sp)
                                }
                            }
                        }

                        // ROW C: Feedback loop loop: Monitor ◄ Teacher ◄ Library
                        Row(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val rowCNodes = listOf(
                                Pair("monitor_agent", "🖥️"),
                                Pair("teacher_agent", "🎓"),
                                Pair("library_agent", "📚")
                            )

                            rowCNodes.forEachIndexed { index, pair ->
                                val (nid, icon) = pair
                                val isNSelected = selectedNodeId == nid
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isNSelected) CyberCard else CyberDark)
                                        .border(
                                            1.dp,
                                            if (isNSelected) CyberPrimary else CyberCard,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedNodeId = nid },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(icon, fontSize = 18.sp)
                                        Text(
                                            nid.split("_")[0].uppercase(),
                                            fontSize = 7.sp,
                                            color = if (isNSelected) CyberPrimary else CyberGray,
                                            maxLines = 1,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                if (index < rowCNodes.size - 1) {
                                    Text("◄", color = CyberPrimary.copy(alpha = 0.4f), fontSize = 10.sp)
                                }
                            }
                        }

                        Text("▼", color = CyberPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)

                        // LAYER 4: DETACHED SYSTEM FUNCTION CORES
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val sysNodes = listOf(
                                Pair("market_data", "📈"),
                                Pair("risk_engine", "⚠️"),
                                Pair("event_store", "🗄️")
                            )

                            sysNodes.forEach { pair ->
                                val (nid, icon) = pair
                                val isNSelected = selectedNodeId == nid
                                Box(
                                    modifier = Modifier
                                        .width(90.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isNSelected) CyberCard else CyberDark)
                                        .border(
                                            1.dp,
                                            if (isNSelected) CyberPrimary else CyberCard,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { selectedNodeId = nid }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(icon, fontSize = 16.sp)
                                        Text(
                                            nid.replace("_", " ").uppercase(),
                                            fontSize = 7.sp,
                                            color = if (isNSelected) CyberPrimary else Color.White,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // DETAIL DRAWER PANEL FOR SELECTED TOPOLOGY NODE
                    val selectedNode = topologyNodes.firstOrNull { it.id == selectedNodeId } ?: topologyNodes[2]
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberDark),
                        border = BorderStroke(1.dp, CyberCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(selectedNode.icon, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = selectedNode.label,
                                            color = Color.White,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = selectedNode.category,
                                            color = CyberPrimary,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }

                                Surface(
                                    color = CyberPrimary.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, CyberPrimary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = selectedNode.activeStateLabel,
                                        color = CyberPrimary,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = selectedNode.description,
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = CyberCard)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "CONNECTIONS & CHANNELS:",
                                color = CyberGray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedNode.connections,
                                color = CyberPrimary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // --- NEW COMPONENT 1: SYSTEM LAYER ARCHITECTURE & COMPONENT REGISTRY ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SYSTEM LAYER ARCHITECTURE & COMPONENT REGISTRY",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Detailed functional breakdown of the enterprise high-frequency trading topology layers.",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Horizontal tab selector for system layers
                    val layers = listOf(
                        Pair("frontend", "Frontend / App 📱"),
                        Pair("orchestration", "Orchestration 🤖"),
                        Pair("intelligence", "Intelligence 🧠"),
                        Pair("risk", "Risk & Safety 🛡️"),
                        Pair("data", "Data & Event Core 🗄️"),
                        Pair("execution", "Execution / Broker 🏦"),
                        Pair("infra", "Infrastructure 🐳")
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(layers) { layer ->
                            val isSelected = selectedRegistryLayer == layer.first
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyberPrimary.copy(alpha = 0.15f) else CyberDark)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) CyberPrimary else CyberCard,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedRegistryLayer = layer.first }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = layer.second,
                                    color = if (isSelected) CyberPrimary else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Layer info rendering
                    val registryData = when (selectedRegistryLayer) {
                        "frontend" -> Triple(
                            "Quantum Dashboard, Mobile App Console",
                            "Handles real-time user interface rendering, responsive data visualization, system parameter overrides, and live execution auditing.",
                            "Android Jetpack Compose, Streamlit (Python desktop console), Flutter / React Native"
                        )
                        "orchestration" -> Triple(
                            "10 AI Agents Conspiracy Layer",
                            "Manages microsecond pipeline execution, funnel-orchestration, multi-agent evaluation rounds (Eyes ➔ Brain ➔ Filter ➔ Judge), and consensus voting.",
                            "Python (asyncio / FastAPI), Celery Distributed Tasks, RabbitMQ"
                        )
                        "intelligence" -> Triple(
                            "Brain (Quant Factors) + Teacher (RL Learning Core)",
                            "Calculates quantitative market edge coefficients (Momentum, Carry, Intrinsic Value, Volatility bands) & runs backing reinforcement feedback models.",
                            "Pandas / NumPy, scikit-learn, Ray RLlib / TensorFlow, PyTorch"
                        )
                        "risk" -> Triple(
                            "Shield Agent (Risk Engine + Kill Switch Overrides)",
                            "Enforces risk caps, position-sizing leverage thresholds (maximum 1% margin allocation limit per trade block) and systemic hardware kill-switches.",
                            "Custom high-performance Rust rules algorithm engine, in-memory transactional interceptors"
                        )
                        "data" -> Triple(
                            "Library Agent + Event Store (Persistent Memory)",
                            "Executes distributed event sourcing, aggregates historical order ledgers, chat streams, and provides semantic long-term prompt knowledge repositories.",
                            "Apache Kafka message streaming, Redis operational cache, SQLite Room database persistence"
                        )
                        "execution" -> Triple(
                            "Hands Agent + Broker Integration Layer",
                            "Secures transactional package allocations, stages specific trade lots, and relays direct APIs orders down to connected low-latency simulated brokers.",
                            "OMD Broker REST API / WebSockets protocol, MetaTrader MT5 gateway"
                        )
                        else -> Triple(
                            "Docker Engine + Stack Deployments",
                            "Handles container virtualization, scalable orchestration stacks, local developer setups, and cloud environment deployments.",
                            "Docker, docker-compose orchestration, Kubernetes, Google Cloud Run microservices"
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberDark),
                        border = BorderStroke(1.dp, CyberCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "CORE MODULE: ${registryData.first.uppercase()}",
                                color = CyberPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "PRIMARY RESPONSIBILITY:",
                                color = CyberGray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = registryData.second,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(bottom = 10.dp)
                            )

                            Text(
                                text = "RECOMMENDED TECH STACK:",
                                color = CyberGray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = registryData.third,
                                color = CyberBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // --- NEW COMPONENT 2: INTERACTIVE TRADE DATA FLOW PATHWAY SIMULATOR ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TRADE PIPELINE PATHWAY SIMULATOR",
                                color = CyberPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Trace how a transaction flows step-by-step from Ingestion to Persistence.",
                                color = CyberGray,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Simulation Play/Stop Button
                        IconButton(
                            onClick = { flowPlaying = !flowPlaying },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (flowPlaying) CyberRed.copy(alpha = 0.2f) else CyberGreen.copy(alpha = 0.2f))
                                .border(1.dp, if (flowPlaying) CyberRed else CyberGreen, CircleShape)
                        ) {
                            Text(
                                text = if (flowPlaying) "⏸" else "▶",
                                color = if (flowPlaying) CyberRed else CyberGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1 to 10 Horizontal Step Progress bar
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        items((1..10).toList()) { step ->
                            val isActive = activeFlowStep == step
                            val isCompleted = step < activeFlowStep
                            val stepColor = when {
                                isActive -> CyberPrimary
                                isCompleted -> CyberGreen
                                else -> CyberCard
                            }

                            Box(
                                modifier = Modifier
                                    .width(32.dp)
                                    .height(28.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(stepColor.copy(alpha = if (isActive) 0.25f else 0.1f))
                                    .border(
                                        width = 1.dp,
                                        color = if (isActive) CyberPrimary else stepColor,
                                        shape = RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        flowPlaying = false
                                        activeFlowStep = step
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$step",
                                    color = if (isActive) CyberPrimary else if (isCompleted) CyberGreen else CyberGray,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            if (step < 10) {
                                Text("➔", color = CyberGray.copy(alpha = 0.4f), fontSize = 8.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Step data translation
                    val stepDescription = when (activeFlowStep) {
                        1 -> Pair(
                            "Step 1 - Market Ingestion 👁️",
                            "Eyes collects multi-source market pricing, order volumes, and real-time news indicator telemetries."
                        )
                        2 -> Pair(
                            "Step 2 - Factor Formulation 🧠",
                            "Brain Agent processes numerical data calculating Momentum, Carry Yield, Intrinsic Value, and Volatility factors representational weights."
                        )
                        3 -> Pair(
                            "Step 3 - Signal Confidence Filter 🔍",
                            "Filter Agent implements Bayesian logic gates and confidence brackets to eliminate volatile low-probability market noise."
                        )
                        4 -> Pair(
                            "Step 4 - Consensus Alignment ⚖️",
                            "Judge Agent cross-references metrics across the entire collective to deliver a safe trend verdict (Score 1-10)."
                        )
                        5 -> Pair(
                            "Step 5 - Risk Engine Certification 🛡️",
                            "Shield Agent validates safety rules: verifying active margin allocation (max 1% trade limits) and global System Kill-switch state."
                        )
                        6 -> Pair(
                            "Step 6 - Transaction Sizing 🧤",
                            "Hands Agent transforms certified consensus packages into mechanical orders, calculating entry sizes and position lots allocations."
                        )
                        7 -> Pair(
                            "Step 7 - Broker Routing APIs 🏦",
                            "Broker Agent executes low-latency secure connectivity protocols with simulated brokers, dispatching trade tickets to capture floating balances."
                        )
                        8 -> Pair(
                            "Step 8 - Real-Time Dashboard Telemetry 🖥️",
                            "Monitor core reads open margin exposures in real-time, feeding direct log streams and visual dashboard charts."
                        )
                        9 -> Pair(
                            "Step 9 - Reinforcement Tuning 🎓",
                            "Teacher Agent runs automatic RL rewards simulations on historical logs to recalibrate the Brain model's factor weights."
                        )
                        else -> Pair(
                            "Step 10 - event store persistence 📚",
                            "Library Agent commits finalized transaction streams, messages, and metric profiles to persistent Rooms SQLite storage core databases."
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberDark),
                        border = BorderStroke(1.dp, CyberCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stepDescription.first,
                                    color = CyberPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = if (flowPlaying) "SIMULATING..." else "PAUSED STATE",
                                    color = if (flowPlaying) CyberGreen else CyberGray,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stepDescription.second,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // --- NEW COMPONENT 3: EXTERNAL CONNECTIVITY MODULE (How your app connects) ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "EXTERNAL CONNECTIVITY COMPATIBILITY ENGINE",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Connect custom platforms directly to Project X's high-frequency decision funnel.",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val connectTabs = listOf(
                        Pair("dashboard", "Telemetry Data"),
                        Pair("trading", "Start/Stop"),
                        Pair("risk", "Risk Control"),
                        Pair("decisions", "Agent Decisions"),
                        Pair("alerts", "Real-time Alerts"),
                        Pair("tuning", "Strategy Tuning")
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(connectTabs) { tab ->
                            val isSelected = selectedConnectionTab == tab.first
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyberPrimary.copy(alpha = 0.15f) else CyberDark)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) CyberPrimary else CyberCard,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedConnectionTab = tab.first }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = tab.second,
                                    color = if (isSelected) CyberPrimary else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val connectInfo = when (selectedConnectionTab) {
                        "dashboard" -> Triple(
                            "WebSocket Telemetry Stream logs (Monitor + Library Core)",
                            "ws://projectx.capital/api/v1/telemetry",
                            """
                            // Connect to Live Telemetry Feed
                            socket = new WebSocket("ws://projectx.capital/v1/telemetry");
                            socket.onmessage = (event) => {
                              const packet = JSON.parse(event.data);
                              console.log("Monitor Live Metric:", packet.profits);
                            };
                            """.trimIndent()
                        )
                        "trading" -> Triple(
                            "POST Command Override (Shield / Kill Switch State)",
                            "POST https://projectx.capital/api/v1/shield/killswitch",
                            """
                            // Initiate Hardware Emergency System Halt
                            fetch("/api/v1/shield/killswitch", {
                              method: "POST",
                              headers: { "Content-Type": "application/json" },
                              body: JSON.stringify({ active: true, payload: "FORCE_PULLOUT" })
                            });
                            """.trimIndent()
                        )
                        "risk" -> Triple(
                            "PATCH Active Drawdown Margin Allocation Thresholds",
                            "PATCH https://projectx.capital/api/v1/shield/config",
                            """
                            // Set maximum risk allocation limits
                            fetch("/api/v1/shield/config", {
                              method: "PATCH",
                              headers: { "Content-Type": "application/json" },
                              body: JSON.stringify({ max_allocation_pct: 1.0 })
                            });
                            """.trimIndent()
                        )
                        "decisions" -> Triple(
                            "GET Historic Agent Decision & Score Streams",
                            "GET https://projectx.capital/api/v1/library/events",
                            """
                            // Fetch multi-agent historic decision matrix
                            fetch("/api/v1/library/events?target=judge")
                              .then(res => res.json())
                              .then(data => console.log("Verdicts:", data));
                            """.trimIndent()
                        )
                        "alerts" -> Triple(
                            "Server-Sent Events (SSE) Real-time Alert Node",
                            "GET https://projectx.capital/api/v1/alerts/stream",
                            """
                            // Subscribe to Event alert stream
                            const source = new EventSource("/v1/alerts/stream");
                            source.addEventListener("risk_alert", (e) => {
                              alert("SHIELD TRIGGERED: " + JSON.parse(e.data).type);
                            });
                            """.trimIndent()
                        )
                        else -> Triple(
                            "POST Calibrate Alpha Weights Model (Teacher Agent)",
                            "POST https://projectx.capital/api/v1/teacher/manualtune",
                            """
                            // Manually override Brain factors tuning
                            fetch("/api/v1/teacher/manualtune", {
                              method: "POST",
                              headers: { "Content-Type": "application/json" },
                              body: JSON.stringify({ mom: 0.25, carry: 0.25 })
                            });
                            """.trimIndent()
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberDark),
                        border = BorderStroke(1.dp, CyberCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "CONNECTION INTERFACE:",
                                color = CyberGray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = connectInfo.first,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "ENDPOINT/URI: ${connectInfo.second}",
                                color = CyberPrimary,
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "BOILERPLATE EXAMPLE:",
                                color = CyberGray,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )

                            // Monospace code block styling
                            Surface(
                                color = CyberSurface.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, CyberCard),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Text(
                                    text = connectInfo.third,
                                    color = CyberBlue,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(8.dp),
                                    maxLines = 10,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- NEW COMPONENT 4: SIMPLE API DESIGN CONSOLE & SIMULATED CLIENT TERMINAL ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SIMPLE API DESIGN CONSOLE",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Interact with the core REST server routes and WebSocket interfaces proposed for client application bindings.",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // API Endpoints list selection
                    val apiEndpoints = listOf(
                        "GET /api/status",
                        "GET /api/agents",
                        "GET /api/trades",
                        "GET /api/performance",
                        "POST /api/command",
                        "POST /api/risk/update",
                        "GET /api/factors",
                        "ws://your-server/ws/dashboard"
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(apiEndpoints) { ep ->
                            val isSelected = selectedApiEndpoint == ep
                            val isPost = ep.startsWith("POST")
                            val isWs = ep.startsWith("ws")
                            
                            val epColor = when {
                                isPost -> CyberRed
                                isWs -> CyberBlue
                                else -> CyberGreen
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) epColor.copy(alpha = 0.15f) else CyberDark)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) epColor else CyberCard,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedApiEndpoint = ep }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = ep,
                                    color = if (isSelected) epColor else Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Match Endpoint and get mock details
                    val (epPurpose, epPayload) = when (selectedApiEndpoint) {
                        "GET /api/status" -> Pair(
                            "Returns active server uptime, global simulated connection parameters, active balances coordinates and status overrides flags.",
                            """{
  "status": "running",
  "uptime_seconds": 158402,
  "active_agents": 10,
  "last_sync_timestamp": "2026-06-19T13:48:00Z",
  "balance": 1084.50,
  "kill_switch_active": false
}"""
                        )
                        "GET /api/agents" -> Pair(
                            "Compiles real-time processing pulses, logs, actions, and telemetry properties across all 10 conspiracy loop micro-agents.",
                            """[
  { "id": "eyes", "name": "Agent_01_Eyes", "state": "ACTIVE", "last_pulse": "1s ago", "metric": "Ingesting values..." },
  { "id": "brain", "name": "Agent_02_Brain", "state": "ACTIVE", "last_pulse": "1s ago", "metric": "Mom=0.25, Carry=0.25" },
  { "id": "filter", "name": "Agent_03_Filter", "state": "ACTIVE", "last_pulse": "3s ago", "metric": "98.4% Confidence Limit" },
  { "id": "judge", "name": "Agent_04_Judge", "state": "ACTIVE", "last_pulse": "2s ago", "metric": "Consensus score: 8/10" }
]"""
                        )
                        "GET /api/trades" -> Pair(
                          "Pulls standardized trade lists populated with symbolic margins, execution directions, exact entry prices coordinates, and relative P&L.",
                          """[
  { "id": 1045, "symbol": "XAU/USD", "direction": "LONG", "entry": 2354.20, "exit": 2362.80, "pnl": 430.00, "win": true, "score": 8 },
  { "id": 1044, "symbol": "BTC/USD", "direction": "SHORT", "entry": 67340.00, "exit": 67120.00, "pnl": 220.00, "win": true, "score": 9 }
]"""
                        )
                        "GET /api/performance" -> Pair(
                          "Aggregates system efficiency ratios, Win margins rates, Sharpe Coefficient factors, Max historical drawdowns for model validation checks.",
                          """{
  "total_trades": 412,
  "win_rate": 68.45,
  "net_pnl": 98450.25,
  "sharpe_ratio": 2.48,
  "max_drawdown": -4.25,
  "recovery_factor": 5.4,
  "profit_factor": 2.15
}"""
                        )
                        "POST /api/command" -> Pair(
                          "Dispatches active execution override commands (e.g., system startup parameters, manual pauses, state reinitializations).",
                          """{
  "status": "acknowledged",
  "command": "start_trading",
  "caller_identity": "admin_user_prinson",
  "dispatched_at": "2026-06-19T13:48:15Z",
  "agent_funnel_response": "All 10 agents set to active trading profiles."
}"""
                        )
                        "POST /api/risk/update" -> Pair(
                          "Alters operational risk tolerances. Interceptors evaluate allocation variables dynamically and store constraints via ShieldAgent.",
                          """{
  "status": "success",
  "updated_fields": {
    "max_risk_per_trade": 0.01,
    "enforced_by": "ShieldAgent"
  },
  "effect_timestamp": "2026-06-19T13:48:15Z"
}"""
                        )
                        "GET /api/factors" -> Pair(
                          "Exposes real-time quant factor metrics computed across standard assets (Momentum indicators, Carry Yield scores, Volatility bounds).",
                          """{
  "timestamp": "2026-06-19T13:48:15Z",
  "symbol": "XAU/USD",
  "factors": {
    "momentum": 0.72,
    "carry": 0.15,
    "value": -0.42,
    "volatility": 0.28
  },
  "recommended_bias": "BULLISH_STAGING"
}"""
                        )
                        else -> Pair(
                          "Simulates full-duplex WebSocket logs tracking real-time asset pricing, emergency triggers, consensus outputs, and floating balances.",
                          """[CONNECTING] ws://projectx.capital/ws/dashboard ...
[CONNECTED] Handshake OK, protocols aligned.
[RECV] { "pnl": 12840.45, "win_rate_pct": 69.2, "active_positions": 2 }
[RECV] { "alert_event": "ShieldAgent approved standard exposure: Gold size 0.5 lots." }
[RECV] { "agent_consensus_pulse": "Judge consensus aligned bullish, score=9." }"""
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberDark),
                        border = BorderStroke(1.dp, CyberCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "ROUTE SPECIFICATION:",
                                color = CyberPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = selectedApiEndpoint,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = epPurpose,
                                color = CyberGray,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "MOCK SIMULATED SERVER OUTPUT:",
                                color = CyberPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )

                            Surface(
                                color = CyberSurface.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, CyberCard),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Text(
                                    text = epPayload,
                                    color = CyberBlue,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- NEW COMPONENT 5: RELATIONAL DATABASE SCHEMA INSIGHTS ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DATABASE SCHEMA MAPPER",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Select proposed relational entities to analyze structure layouts, constraint properties, and mock data logs.",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Database tables selection tabs
                    val dbTables = listOf("trades", "agent_logs", "performance_metrics", "risk_settings", "events")

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(dbTables) { table ->
                            val isSelected = selectedDbTable == table
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyberPrimary.copy(alpha = 0.15f) else CyberDark)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) CyberPrimary else CyberCard,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedDbTable = table }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = table,
                                    color = if (isSelected) CyberPrimary else Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Table details definition
                    val tableSpec = when (selectedDbTable) {
                        "trades" -> Triple(
                            "Relational system ledger recording historical completed trades coordinates and consensus scoring.",
                            listOf(
                                Pair("id", "SERIAL PRIMARY KEY"),
                                Pair("timestamp", "TIMESTAMP WITH TIME ZONE"),
                                Pair("symbol", "VARCHAR(20)"),
                                Pair("direction", "VARCHAR(10) (LONG / SHORT)"),
                                Pair("entry_price", "DECIMAL(12,6)"),
                                Pair("exit_price", "DECIMAL(12,6)"),
                                Pair("quantity", "DECIMAL(12,4)"),
                                Pair("pnl", "DECIMAL(12,4)"),
                                Pair("win", "BOOLEAN"),
                                Pair("score", "INTEGER (Score 1-10 from Judge)"),
                                Pair("agent_decision", "JSONB (Complete agent parameters record)"),
                                Pair("created_at", "TIMESTAMP DEFAULT NOW()")
                            ),
                            """{
  "id": 1024,
  "timestamp": "2026-06-19T13:45:00Z",
  "symbol": "XAU/USD",
  "direction": "LONG",
  "entry_price": 2350.50,
  "exit_price": 2362.40,
  "quantity": 10.00,
  "pnl": 119.00,
  "win": true,
  "score": 8,
  "agent_decision": {
    "eyes_indicators": "Gold breaks 1% Standard threshold",
    "brain_raw_factors": { "mom": 0.45, "carry": 0.10 }
  }
}"""
                        )
                        "agent_logs" -> Triple(
                            "Event-logger system documenting specific pipeline acts completed across dynamic agent loops.",
                            listOf(
                                Pair("id", "SERIAL PRIMARY KEY"),
                                Pair("timestamp", "TIMESTAMP WITH TIME ZONE"),
                                Pair("agent_name", "VARCHAR(50) (Brain, Shield, etc.)"),
                                Pair("action", "TEXT"),
                                Pair("details", "JSONB"),
                                Pair("created_at", "TIMESTAMP DEFAULT NOW()")
                            ),
                            """{
  "id": 85042,
  "timestamp": "2026-06-19T13:45:01Z",
  "agent_name": "ShieldAgent",
  "action": "EXPOSURE_CERTIFICATION",
  "details": { "max_risk_limit_pct": 1.0, "position_size_allocated": 0.5 },
  "created_at": "2026-06-19T13:45:01Z"
}"""
                        )
                        "performance_metrics" -> Triple(
                            "Historical performance metrics aggregated daily to feed evaluation engines.",
                            listOf(
                                Pair("id", "SERIAL PRIMARY KEY"),
                                Pair("date", "DATE"),
                                Pair("total_trades", "INTEGER"),
                                Pair("win_rate", "DECIMAL(5,2)"),
                                Pair("net_pnl", "DECIMAL(12,4)"),
                                Pair("sharpe_ratio", "DECIMAL(6,3)"),
                                Pair("max_drawdown", "DECIMAL(6,3)"),
                                Pair("created_at", "TIMESTAMP DEFAULT NOW()")
                            ),
                            """{
  "id": 182,
  "date": "2026-06-18",
  "total_trades": 24,
  "win_rate": 70.83,
  "net_pnl": 5840.50,
  "sharpe_ratio": 2.52,
  "max_drawdown": -1.25,
  "created_at": "2026-06-18T23:59:00Z"
}"""
                        )
                        "risk_settings" -> Triple(
                            "Operational state schema configuration enforcing strict margin drawdowns.",
                            listOf(
                                Pair("id", "SERIAL PRIMARY KEY"),
                                Pair("max_risk_per_trade", "DECIMAL(5,4)"),
                                Pair("daily_loss_limit", "DECIMAL(12,4)"),
                                Pair("kill_switch_active", "BOOLEAN DEFAULT true"),
                                Pair("updated_at", "TIMESTAMP")
                            ),
                            """{
  "id": 1,
  "max_risk_per_trade": 0.0100,
  "daily_loss_limit": 5000.00,
  "kill_switch_active": false,
  "updated_at": "2026-06-19T13:40:00Z"
}"""
                        )
                        else -> Triple(
                            "Event Store table logging critical asynchronous message events for event sourcing.",
                            listOf(
                                Pair("id", "SERIAL PRIMARY KEY"),
                                Pair("event_type", "VARCHAR(100)"),
                                Pair("payload", "JSONB"),
                                Pair("timestamp", "TIMESTAMP DEFAULT NOW()")
                            ),
                            """{
  "id": 90425,
  "event_type": "TRADE_ORDER_STAGED",
  "payload": { "symbol": "BTC/USD", "order_lots_limit": 0.15 },
  "timestamp": "2026-06-19T13:46:12Z"
}"""
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberDark),
                        border = BorderStroke(1.dp, CyberCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "TABLE SCHEMA: ${selectedDbTable.uppercase()}",
                                color = CyberPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = tableSpec.first,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "SCHEMA DICTIONARY STRUCT:",
                                color = CyberPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            // Draw table schema definitions list
                            tableSpec.second.forEach { (colName, colType) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = colName,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = colType,
                                        color = CyberGray,
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "SAMPLE RECORD PAYLOAD:",
                                color = CyberPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Surface(
                                color = CyberSurface.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, CyberCard),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp)
                            ) {
                                Text(
                                    text = tableSpec.third,
                                    color = CyberBlue,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- NEW COMPONENT 6: INTERACTIVE HIERARCHICAL FOLDER STRUCTURE EXPLORER ---
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface),
                border = BorderStroke(1.dp, CyberCard),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PROJECT ARCHITECTURE FOLDER TREE",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Examine the modular Python setup proposed for backend deployment. Click node links to view module duties.",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Simplified interactive directory hierarchy rendering
                    val folderTree = listOf(
                        Pair("root", "📂 project-x-capital (Project Root)"),
                        Pair("app/agents", "  📁 app/agents/ (10 AI funnel modules)"),
                        Pair("app/core", "  📁 app/core/ (Orchestrators & Safety)"),
                        Pair("app/api", "  📁 app/api/ (Endpoints & WebSockets)"),
                        Pair("app/dashboard", "  📁 app/dashboard/ (Streamlit Console)"),
                        Pair("app/models", "  📁 app/models/ (Validation schemas)"),
                        Pair("app/services", "  📁 app/services/ (Backend service files)"),
                        Pair("config", "  📁 config/ (YAML profiles)"),
                        Pair("database", "  📁 database/ (Postgres Models)"),
                        Pair("docker", "  📁 docker/ (Virtualization specs)"),
                        Pair("scripts", "  📁 scripts/ (RL Training & Backtests)")
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        folderTree.forEach { (fid, label) ->
                            val isSelected = selectedFolderId == fid
                            Surface(
                                color = if (isSelected) CyberPrimary.copy(alpha = 0.12f) else Color.Transparent,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedFolderId = fid }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) CyberPrimary else Color.White,
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(CyberPrimary)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Folder context specs
                    val folderDoc = when (selectedFolderId) {
                        "root" -> Pair(
                            "project-x-capital/",
                            "Central project deployment folder. Holds core modules, setting presets, docker-compose configuration files, dependencies lists, and continuous integration flows."
                        )
                        "app/agents" -> Pair(
                            "app/agents/",
                            "Houses the 10 micro-agent engines (Eyes, Brain, Filter, Judge, Shield, Hands, Broker, Monitor, Teacher, Library). They carry isolated asynchronous processing logic."
                        )
                        "app/core" -> Pair(
                            "app/core/",
                            "Orchestrator center. Keeps pipelines synchronized. Invokes sequential evaluation rounds and manages real-time risk checks."
                        )
                        "app/api" -> Pair(
                            "app/api/",
                            "FastAPI backend binding points. Defines web REST paths and handles low-latency live WebSocket loops for operational client consoles."
                        )
                        "app/dashboard" -> Pair(
                            "app/dashboard/",
                            "Interactive diagnostic page code template (Streamlit core). Houses plots, status gauges, and manual weights overrides toggles."
                        )
                        "app/models" -> Pair(
                            "app/models/",
                            "Static database object models and dynamic API body structures encoded in typed Pydantic structures for extreme request safety checking."
                        )
                        "app/services" -> Pair(
                            "app/services/",
                            "Holds custom logical bridges and utility functions executing heavy backend calculations and database writing controls."
                        )
                        "config" -> Pair(
                            "config/",
                            "Settings directories. Keeps YAML configuration properties defining execution speed thresholds, maximum drawdowns constraints, and connected services coordinates."
                        )
                        "database" -> Pair(
                            "database/",
                            "Core DB definitions holding model definitions (SQLAlchemy entities) linking PostgreSQL schemas seamlessly into code."
                        )
                        "docker" -> Pair(
                            "docker/",
                            "Stores multi-container templates combining backend service containers, PostgreSQL containers, RabbitMQ queues, and web servers."
                        )
                        else -> Pair(
                            "scripts/",
                            "Diagnostic utilities. Keeps python modules executing Reinforcement Learning factor weight training or running historical file-based backtests."
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberDark),
                        border = BorderStroke(1.dp, CyberCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "DIRECTORY CONTEXT DOCUMENTATION:",
                                color = CyberPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = folderDoc.first,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = folderDoc.second,
                                color = CyberGray,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // SYSTEM MASTER KILL SWITCH
        item {
            val killSwitchOn = userProfile?.isKillSwitchOn == true
            Card(
                colors = CardDefaults.cardColors(containerColor = if (killSwitchOn) CyberRed.copy(alpha = 0.15f) else CyberSurface),
                border = BorderStroke(1.dp, if (killSwitchOn) CyberRed else CyberCard),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "MASTER SYSTEM KILL SWITCH (SHIELD)",
                                color = if (killSwitchOn) CyberRed else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Engages ShieldAgent hard limits: exits all active quant balances, closes floating exposure, blocks execute order modules.",
                                fontSize = 10.sp,
                                color = CyberGray,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Switch(
                            checked = killSwitchOn,
                            onCheckedChange = { viewModel.toggleKillSwitch(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = CyberRed,
                                checkedTrackColor = CyberRed.copy(alpha = 0.4f),
                                uncheckedThumbColor = CyberGray,
                                uncheckedTrackColor = CyberDark
                            ),
                            modifier = Modifier.testTag("kill_switch")
                        )
                    }
                }
            }
        }

        // RISK PER-TRADE LEVEL SELECTOR: 1% Enforced limit
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SHIELD AGENT ENFORCED ALLOCATION PER TRADE",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Specifies client account equity maximum risk parameters per routed position block (Default 1.0%).",
                        color = CyberGray,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf(0.5f, 1.0f, 1.5f, 2.0f).forEach { pct ->
                            val isSelected = currentRiskMargin == pct
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) CyberCard else CyberDark)
                                    .border(1.dp, if (isSelected) CyberPrimary else CyberCard, RoundedCornerShape(8.dp))
                                    .clickable {
                                        currentRiskMargin = pct
                                        viewModel.setRiskMargin(pct)
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "$pct%",
                                    color = if (isSelected) CyberPrimary else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        // FIREBASE CONNECT / GOOGLE SIGN-IN BLOCK
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SECURE GOOGLE AUTH IDENTIFICATION",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Connecting secure user authentication credentials and database state storage with Firebase.",
                        fontSize = 10.sp,
                        color = CyberGray,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Google Account: ${userProfile?.email ?: "Not active"}", color = Color.White, fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                    Text("Connection State: $firebaseStatus", color = CyberBlue, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(bottom = 12.dp))

                    if (userProfile?.isAuthenticated == true) {
                        Button(
                            onClick = { viewModel.handleSignOut() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberRed),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SIGN OUT FROM SECURE GOOGLE SESSION", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.handleGoogleSignIn("Alvin Prinson", "alvinprinson20@gmail.com")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("SIGN IN SECURELY WITH GOOGLE IN CLOUD", color = Color.White, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }
        }

        // API KEY OVERRIDE SELECTOR
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SECURE API OVERRIDE PROTOCOLS",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = apiKeyInput,
                        onValueChange = {
                            apiKeyInput = it
                            viewModel.setCustomApiKey(it)
                        },
                        label = { Text("Gemini Client Key Override", color = CyberGray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = CyberPrimary,
                            unfocusedBorderColor = CyberCard
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Leave empty to use AI Studio system environment default keys.",
                        fontSize = 9.sp,
                        color = CyberGray,
                        fontFamily = FontFamily.Monospace,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )
                }
            }
        }

        // HARD REINFORCEMENT LEARNING FACTOR TUNE overrides (Teacher agent)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TEACHERAGENT MANUAL RL WEIGHT OVERRIDE",
                        color = CyberPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Force re-tuning factor weights directly in the neural calculation layer of BrainAgent.",
                        fontSize = 10.sp,
                        color = CyberGray,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Momentum (Mom): ${String.format("%.2f", momWeight)}", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.width(140.dp))
                            Slider(
                                value = momWeight,
                                onValueChange = { momWeight = it },
                                valueRange = 0.05f..0.50f,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Carry Yield (Car): ${String.format("%.2f", carryWeight)}", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.width(140.dp))
                            Slider(
                                value = carryWeight,
                                onValueChange = { carryWeight = it },
                                valueRange = 0.05f..0.50f,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Intrinsic Val (Val): ${String.format("%.2f", valWeight)}", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.width(140.dp))
                            Slider(
                                value = valWeight,
                                onValueChange = { valWeight = it },
                                valueRange = 0.05f..0.50f,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Volatility Band (Vol): ${String.format("%.2f", volWeight)}", color = Color.White, fontSize = 11.sp, fontFamily = FontFamily.Monospace, modifier = Modifier.width(140.dp))
                            Slider(
                                value = volWeight,
                                onValueChange = { volWeight = it },
                                valueRange = 0.05f..0.50f,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.manualTuneWeights(momWeight, carryWeight, valWeight, volWeight) },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("FORCE MANUALLY INTERPOLATE WEIGHTS", color = CyberDark, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // RESET DATABASES
        item {
            Button(
                onClick = { viewModel.clearTradeLogs() },
                colors = ButtonDefaults.buttonColors(containerColor = CyberRed.copy(alpha = 0.2f)),
                border = BorderStroke(1.dp, CyberRed),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("RESET ALL STRATEGY DATABASES", color = CyberRed, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
        }
    }
}

// --- HELPER extension for scaling inside Jetpack Compose ---
private fun Modifier.scale(scale: Float): Modifier = this.then(
    Modifier.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        layout((placeable.width * scale).toInt(), (placeable.height * scale).toInt()) {
            placeable.placeRelative(0, 0)
        }
    }
)

@Composable
fun AgentGridCard(
    agent: AgentStatusEntity,
    terminalLogs: List<String>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val emoji = when {
        agent.agentName.contains("Eye", true) || agent.role.contains("Eye", true) || agent.agentName.contains("Sight", true) -> "👁️"
        agent.agentName.contains("Brain", true) || agent.role.contains("Brain", true) || agent.agentName.contains("Intel", true) -> "🧠"
        agent.agentName.contains("Filter", true) || agent.role.contains("Filter", true) || agent.agentName.contains("Sieve", true) -> "🔍"
        agent.agentName.contains("Judge", true) || agent.role.contains("Judge", true) || agent.agentName.contains("Arbit", true) -> "⚖️"
        agent.agentName.contains("Shield", true) || agent.role.contains("Shield", true) || agent.agentName.contains("Risk", true) -> "🛡️"
        agent.agentName.contains("Hand", true) || agent.role.contains("Execution", true) || agent.agentName.contains("Trade", true) -> "🧤"
        agent.agentName.contains("Broker", true) || agent.role.contains("Broker", true) -> "🏦"
        agent.agentName.contains("Monitor", true) || agent.role.contains("Telemetry", true) -> "🖥️"
        agent.agentName.contains("Teacher", true) || agent.role.contains("Learn", true) -> "🎓"
        agent.agentName.contains("Library", true) || agent.role.contains("Database", true) || agent.agentName.contains("Book", true) -> "📚"
        else -> "🤖"
    }

    val agentColor = when (agent.agentName) {
        "Eyes" -> CyberPrimary
        "Brain" -> CyberBlue
        "Filter" -> CyberPrimary
        "Judge" -> CyberGreen
        "Shield" -> CyberRed
        "Hands" -> CyberGreen
        "Broker" -> CyberBlue
        "Monitor" -> CyberPrimary
        "Teacher" -> CyberGreen
        "Library" -> CyberGray
        else -> CyberPrimary
    }

    // Dynamic metrics like health score, latency
    val latencyMs = remember(agent.agentName) {
        when (agent.agentName) {
            "Eyes" -> 12
            "Brain" -> 45
            "Filter" -> 18
            "Judge" -> 22
            "Shield" -> 5
            "Hands" -> 8
            "Broker" -> 15
            "Monitor" -> 30
            "Teacher" -> 120
            "Library" -> 25
            else -> 10
        }
    }

    val baseHealth = when (agent.agentName) {
        "Eyes" -> 99.8f
        "Brain" -> 100.0f
        "Filter" -> 99.5f
        "Judge" -> 100.0f
        "Shield" -> 100.0f
        "Hands" -> 99.9f
        "Broker" -> 99.7f
        "Monitor" -> 100.0f
        "Teacher" -> 98.4f
        "Library" -> 100.0f
        else -> 100.0f
    }
    
    val healthScore = if (agent.isActive) baseHealth else 0.0f

    // Find the latest custom log matching this agent's name
    val matchingLog = remember(terminalLogs, agent.agentName) {
        terminalLogs.lastOrNull { log ->
            log.contains(agent.agentName, ignoreCase = true) || log.contains(agent.role, ignoreCase = true)
        }
    }

    // Interactive Pulsing for Active State
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Smooth status & health transitions (equivalent to Framer Motion / CSS transitions)
    val animatedBorderColor by animateColorAsState(
        targetValue = if (agent.isActive) agentColor.copy(alpha = 0.6f) else CyberCard,
        animationSpec = tween(500),
        label = "borderColorAnim"
    )

    val animatedStatusBg by animateColorAsState(
        targetValue = if (agent.isActive) CyberGreen.copy(alpha = 0.08f) else CyberRed.copy(alpha = 0.08f),
        animationSpec = tween(500),
        label = "statusBgAnim"
    )

    val animatedStatusBorder by animateColorAsState(
        targetValue = if (agent.isActive) CyberGreen.copy(alpha = 0.3f) else CyberRed.copy(alpha = 0.3f),
        animationSpec = tween(500),
        label = "statusBorderAnim"
    )

    val animatedIndicatorColor by animateColorAsState(
        targetValue = if (agent.isActive) CyberGreen else CyberRed,
        animationSpec = tween(500),
        label = "indicatorColorAnim"
    )

    val animatedWeight by animateFloatAsState(
        targetValue = agent.factorWeight.coerceIn(0f, 1f),
        animationSpec = tween(800, easing = LinearEasing),
        label = "weightAnim"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = CyberSurface.copy(alpha = 0.85f)),
        border = BorderStroke(
            width = 1.1.dp,
            color = animatedBorderColor
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("agent_grid_card_${agent.agentName}")
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Badge & Name & Health Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(agentColor.copy(alpha = 0.12f))
                            .border(width = 0.5.dp, color = agentColor.copy(alpha = 0.3f), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = emoji, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = agent.agentName,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = agent.role,
                            color = CyberGray,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Dynamic Health Beacon Badge with Slide-fade transition on content state updates
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(animatedStatusBg)
                        .border(
                            0.5.dp, 
                            animatedStatusBorder,
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .graphicsLayer {
                                if (agent.isActive) {
                                    scaleX = pulseScale
                                    scaleY = pulseScale
                                }
                            }
                            .clip(CircleShape)
                            .background(animatedIndicatorColor)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    AnimatedContent(
                        targetState = if (agent.isActive) "${String.format("%.1f", healthScore)}%" else "DOWN",
                        transitionSpec = {
                            (scaleIn(animationSpec = tween(200)) + fadeIn(animationSpec = tween(200)))
                                .togetherWith(scaleOut(animationSpec = tween(200)) + fadeOut(animationSpec = tween(200)))
                        },
                        label = "healthAnim"
                    ) { targetText ->
                        Text(
                            text = targetText,
                            color = animatedIndicatorColor,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Information Grid: Weight & Latency & Active Process
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tuned Weight display
                Column {
                    Text(
                        text = "INFLUENCE",
                        color = CyberGray,
                        fontSize = 7.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    AnimatedContent(
                        targetState = "${String.format("%.1f", agent.factorWeight * 100)}%",
                        transitionSpec = {
                            fadeIn(tween(250)) togetherWith fadeOut(tween(250))
                        },
                        label = "weightTextAnim"
                    ) { targetWeightText ->
                        Text(
                            text = targetWeightText,
                            color = agentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Latency status
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "LATENCY",
                        color = CyberGray,
                        fontSize = 7.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    AnimatedContent(
                        targetState = if (agent.isActive) "${latencyMs}ms" else "N/A",
                        transitionSpec = {
                            fadeIn(tween(250)) togetherWith fadeOut(tween(250))
                        },
                        label = "latencyAnim"
                    ) { targetLatency ->
                        Text(
                            text = targetLatency,
                            color = if (agent.isActive) CyberPrimary else CyberGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            // Current state tag with small progress line representing agent weight
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CYCLE STATE:",
                        color = CyberGray,
                        fontSize = 7.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    AnimatedContent(
                        targetState = agent.currentMetric.uppercase(),
                        transitionSpec = {
                            (slideInVertically { height -> height } + fadeIn(tween(180)))
                                .togetherWith(slideOutVertically { height -> -height } + fadeOut(tween(180)))
                        },
                        label = "cycleStateAnim"
                    ) { targetMetric ->
                        Text(
                            text = targetMetric,
                            color = Color.White,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                // Linear Progress bar with smooth width updates
                LinearProgressIndicator(
                    progress = { animatedWeight },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = agentColor,
                    trackColor = CyberCard
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Latest Action Log Box (Agent Specific Thought) with smooth slides (Framer-motion style)
            Text(
                text = "LATEST THOUGHT / NODE LOG:",
                color = CyberGray,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(bottom = 3.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberDark, shape = RoundedCornerShape(6.dp))
                    .border(width = 0.5.dp, color = CyberCard, shape = RoundedCornerShape(6.dp))
                    .padding(6.dp)
            ) {
                Column {
                    AnimatedContent(
                        targetState = agent.lastThought,
                        transitionSpec = {
                            (slideInVertically { height -> height / 2 } + fadeIn(tween(220)))
                                .togetherWith(slideOutVertically { height -> -height / 2 } + fadeOut(tween(220)))
                        },
                        label = "thoughtTextAnim"
                    ) { targetThought ->
                        Text(
                            text = targetThought,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 8.5.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 2,
                            minLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    
                    // Matching live stream log if available (displays context-aware telemetry line)
                    matchingLog?.let { log ->
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.03f), shape = RoundedCornerShape(3.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "LIVE",
                                    color = CyberBlue,
                                    fontSize = 6.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                AnimatedContent(
                                    targetState = log,
                                    transitionSpec = {
                                        (slideInVertically { height -> height / 2 } + fadeIn(tween(180)))
                                            .togetherWith(slideOutVertically { height -> -height / 2 } + fadeOut(tween(180)))
                                    },
                                    label = "liveLogAnim"
                                ) { targetLog ->
                                    val cleanedLog = targetLog
                                        .replaceFirst(Regex("^System:"), "")
                                        .replaceFirst(Regex("^${agent.agentName}:"), "")
                                        .trim()
                                    Text(
                                        text = cleanedLog,
                                        color = CyberGray,
                                        fontSize = 7.5.sp,
                                        fontFamily = FontFamily.Monospace,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
