package com.example.data

import com.example.data.local.AgentStatusEntity
import com.example.data.local.MarketDataEntity
import com.example.data.local.ProjectXDao
import com.example.data.local.TradeEntity
import com.example.data.local.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID
import kotlin.random.Random

class ProjectXRepository(private val dao: ProjectXDao) {

    val userProfile: Flow<UserEntity?> = dao.getUserProfile()
    val allTrades: Flow<List<TradeEntity>> = dao.getAllTrades()
    val agentStatuses: Flow<List<AgentStatusEntity>> = dao.getAgentStatuses()
    val marketData: Flow<List<MarketDataEntity>> = dao.getMarketData()

    // Initialize Default Seed Data
    suspend fun initializeSeedData() {
        // Initialize User
        val existingUser = dao.getUserProfileSynchronous()
        if (existingUser == null) {
            dao.insertUser(
                UserEntity(
                    email = "alvinprinson20@gmail.com",
                    displayName = "Alvin Prinson",
                    balance = 100000.0,
                    isKillSwitchOn = false,
                    riskMarginPct = 1.0f,
                    isAuthenticated = false
                )
            )
        }

        // Initialize 10 Agents with their default roles and factor weights
        val existingAgents = dao.getAgentStatuses().firstOrNull() ?: emptyList()
        if (existingAgents.isEmpty()) {
            val defaultAgents = listOf(
                AgentStatusEntity("Eyes", "Data Collection", true, "Listening", 0.10f, "Analyzing news and multi-market feeds."),
                AgentStatusEntity("Brain", "Market Intelligence", true, "Calculating", 0.25f, "Computing mathematical factors (Momentum, Carry, Value, Volatility)."),
                AgentStatusEntity("Filter", "Signal Confirmation", true, "Filtering", 0.15f, "Blocking fakeouts, liquidity grabs, and news whipsaws."),
                AgentStatusEntity("Judge", "Scoring Engine", true, "Ready", 0.10f, "Scoring trade setups on a confidence scale of 1-10."),
                AgentStatusEntity("Shield", "Risk Management", true, "Monitoring", 0.10f, "Enforcing 1% risk rule and evaluating the hard Kill Switch."),
                AgentStatusEntity("Hands", "Trade Execution", true, "Ready", 0.05f, "Preparing execution triggers and order sizing parameters."),
                AgentStatusEntity("Broker", "Order Routing", true, "Ready", 0.05f, "Evaluating liquidity venues for optimal execution."),
                AgentStatusEntity("Monitor", "Post-Trade Tracker", true, "Tracking", 0.05f, "Monitoring active float P&L, stop loss execution, and position exit points."),
                AgentStatusEntity("Teacher", "Reinforcement Learner", true, "Learning", 0.05f, "Running daily neural weight optimization algorithms based on profits."),
                AgentStatusEntity("Library", "Event Store", true, "Storing", 0.05f, "Archiving metadata, vector signals, and historic trade archives.")
            )
            dao.insertAgentStatuses(defaultAgents)
        }

        // Initialize Market Tickers
        val existingMarket = dao.getMarketData().firstOrNull() ?: emptyList()
        if (existingMarket.isEmpty()) {
            val defaultMarket = listOf(
                MarketDataEntity("XAU/USD", 2320.50, 0.45f, "BULLISH"),
                MarketDataEntity("EUR/USD", 1.0854, -0.12f, "BEARISH"),
                MarketDataEntity("BTC/USD", 67240.0, 1.85f, "BULLISH"),
                MarketDataEntity("GBP/USD", 1.2688, 0.02f, "NEUTRAL")
            )
            dao.insertMarketData(defaultMarket)
        }
    }

    suspend fun saveUserProfile(user: UserEntity) {
        dao.insertUser(user)
    }

    suspend fun clearHistory() {
        dao.clearAllTrades()
        val user = dao.getUserProfileSynchronous()
        if (user != null) {
            dao.insertUser(user.copy(balance = 100000.0))
        }
    }

    suspend fun toggleKillSwitch(enabled: Boolean) {
        val user = dao.getUserProfileSynchronous()
        if (user != null) {
            dao.insertUser(user.copy(isKillSwitchOn = enabled))
        }
        // Notify Shield Agent
        val shieldAgent = AgentStatusEntity(
            agentName = "Shield",
            role = "Risk Management",
            isActive = true,
            currentMetric = if (enabled) "ARMED_KILL_SWITCH" else "MONITORING",
            factorWeight = 0.10f,
            lastThought = if (enabled) "ALERT: Hard system Kill Switch engaged by operator! Liquidating all positions and blocking execute requests." else "Kill Switch normal. Risk limits active.",
            lastUpdateTimestamp = System.currentTimeMillis()
        )
        dao.updateAgentStatus(shieldAgent)

        if (enabled) {
            // Close all active trades immediately
            val openTrades = dao.getOpenTrades()
            for (trade in openTrades) {
                dao.updateTrade(
                    trade.copy(
                        status = "CLOSED",
                        exitPrice = trade.currentPrice,
                        closeTimestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun updateAgentStatus(status: AgentStatusEntity) {
        dao.updateAgentStatus(status)
    }

    // Run custom model tune from teacher agent
    suspend fun tuneWeights(momentumW: Float, carryW: Float, valueW: Float, volW: Float) {
        // Update Teacher Agent thought
        val teacher = AgentStatusEntity(
            agentName = "Teacher",
            role = "Reinforcement Learner",
            isActive = true,
            currentMetric = "RE-TUNING",
            factorWeight = 0.05f,
            lastThought = "Optimal weights updated via manual overrides: Momentum=$momentumW, Carry=$carryW, Value=$valueW, Volatility=$volW. Saved configuration schema.",
            lastUpdateTimestamp = System.currentTimeMillis()
        )
        dao.updateAgentStatus(teacher)

        // Adjust Brain Agent status and metric
        val brain = AgentStatusEntity(
            agentName = "Brain",
            role = "Market Intelligence",
            isActive = true,
            currentMetric = "RE-CALCULATING",
            factorWeight = 0.25f,
            lastThought = "Factor weights synchronized from Teacher. New bias calculations starting with tuned coefficients.",
            lastUpdateTimestamp = System.currentTimeMillis()
        )
        dao.updateAgentStatus(brain)
    }

    // Dynamic simulate tick execution
    suspend fun executeDynamicSimulationTick(): List<String> {
        val logs = mutableListOf<String>()
        val user = dao.getUserProfileSynchronous() ?: return logs

        if (user.isKillSwitchOn) {
            return listOf("SIMULATION: Engine idle. Kill Switch is ENGAGED.")
        }

        // Update current prices
        val assets = listOf("XAU/USD", "EUR/USD", "BTC/USD", "GBP/USD")
        val currentMarket = mutableListOf<MarketDataEntity>()
        val rand = Random.nextDouble(-0.003, 0.003)

        // Let's load the current market data from DB first
        val oldMarket = dao.getMarketData().firstOrNull() ?: emptyList()
        for (item in oldMarket) {
            val delta = item.price * Random.nextDouble(-0.005, 0.005)
            val newPrice = item.price + delta
            val change = item.changePct + (delta / item.price * 100).toFloat()
            val newTrend = if (delta > 0) "BULLISH" else if (delta < 0) "BEARISH" else "NEUTRAL"
            currentMarket.add(MarketDataEntity(item.symbol, newPrice, change, newTrend))
        }
        if (currentMarket.isNotEmpty()) {
            dao.insertMarketData(currentMarket)
        }

        // Log general update
        logs.add("System: Feeds processed for ${assets.joinToString()}.")

        // Periodically trigger a buy/sell trade or adjust profit on open positions
        val openTrades = dao.getOpenTrades()
        if (openTrades.isNotEmpty()) {
            // Update Floating PnL of open positions
            for (trade in openTrades) {
                // Find latest price from our updated list
                val latestMarket = currentMarket.firstOrNull { it.symbol == trade.symbol }
                if (latestMarket != null) {
                    val pnlDirection = if (trade.orderType == "BUY") 1 else -1
                    val priceDelta = latestMarket.price - trade.entryPrice
                    val rawPnl = priceDelta * trade.volume * pnlDirection * (if (trade.symbol.contains("BTC")) 0.1 else 1000.0)
                    val updatedPnl = Math.round(rawPnl * 100.0) / 100.0

                    // Check Stop Loss or Take Profit
                    var shouldClose = false
                    var exitPrice = latestMarket.price
                    var reason = ""

                    if (trade.stopLoss != null && (
                        (trade.orderType == "BUY" && latestMarket.price <= trade.stopLoss) ||
                        (trade.orderType == "SELL" && latestMarket.price >= trade.stopLoss)
                    )) {
                        shouldClose = true
                        exitPrice = trade.stopLoss
                        reason = "STOP LOSS HIT"
                    } else if (trade.takeProfit != null && (
                        (trade.orderType == "BUY" && latestMarket.price >= trade.takeProfit) ||
                        (trade.orderType == "SELL" && latestMarket.price <= trade.takeProfit)
                    )) {
                        shouldClose = true
                        exitPrice = trade.takeProfit
                        reason = "TAKE PROFIT TRIGGERED"
                    } else if (Random.nextDouble() < 0.10) {
                        // 10% chance to voluntarily exit to simulate booking profit
                        shouldClose = true
                        exitPrice = latestMarket.price
                        reason = "MonitorAgent: Exit Target Achieved (Consensus Booked)"
                    }

                    if (shouldClose) {
                        // Book P&L to user profile
                        val exitPnl = ((exitPrice - trade.entryPrice) * trade.volume * pnlDirection * (if (trade.symbol.contains("BTC")) 0.1 else 1000.0))
                        val finalExitPnl = Math.round(exitPnl * 100.0) / 100.0
                        dao.updateTrade(
                            trade.copy(
                                currentPrice = latestMarket.price,
                                exitPrice = exitPrice,
                                pnl = finalExitPnl,
                                status = "CLOSED",
                                closeTimestamp = System.currentTimeMillis(),
                                triggerReason = reason
                            )
                        )
                        dao.insertUser(user.copy(balance = user.balance + finalExitPnl))

                        logs.add("Monitor: Closed ${trade.orderType} ${trade.symbol} at $exitPrice for a P&L of \$${String.format("%.2f", finalExitPnl)} ($reason).")

                        // Adjust Teacher agent weights based on performance
                        val teacherText = if (finalExitPnl > 0) {
                            "Trade profitable. Neural weights reinforced. Incrementing Momentum factor confidence (value: +0.02)."
                        } else {
                            "Trade unprofitable. Adjusted risk factor parameters. Decreasing Volatility weight to offset whipsaw."
                        }
                        dao.updateAgentStatus(
                            AgentStatusEntity("Teacher", "Reinforcement Learner", true, "REINFORCING", 0.05f, teacherText)
                        )
                        dao.updateAgentStatus(
                            AgentStatusEntity("Monitor", "Post-Trade Tracker", true, "COMPLETED", 0.05f, "Successfully exited position. Log archived.")
                        )
                    } else {
                        // Simply update current status
                        dao.updateTrade(
                            trade.copy(
                                currentPrice = latestMarket.price,
                                pnl = updatedPnl
                            )
                        )
                    }
                }
            }
        } else {
            // No active positions, simulate decision pipeline of the 10 agents to trigger a potential new trade!
            if (Random.nextDouble() < 0.35) { // 35% chance of finding setup on tick
                val selectedAsset = currentMarket.random()
                val signalDir = if (Random.nextBoolean()) "BUY" else "SELL"
                val decisionLogId = UUID.randomUUID().toString().substring(0, 4)

                logs.add("Eyes: Detected increased volatility in ${selectedAsset.symbol} - analyzing trend volume.")
                dao.updateAgentStatus(AgentStatusEntity("Eyes", "Data Collection", true, "FEED_ACTIVE", 0.10f, "Analyzing momentum spike on ${selectedAsset.symbol} relative to higher 4H timeframe."))

                logs.add("Brain: Calculated multi-factor core. Momentum=${if (signalDir == "BUY") "BULLISH" else "BEARISH"}, Value=HOLD, Volatility=STRESSED.")
                dao.updateAgentStatus(AgentStatusEntity("Brain", "Market Intelligence", true, "FACTORS_COMPUTING", 0.25f, "Brain computation triggered bias. Factor weights support taking a $signalDir stance."))

                val passFilter = Random.nextDouble() > 0.15 // 85% pass rate
                if (passFilter) {
                    logs.add("Filter: Whipsaw and false breakout filters passed. Signal validated.")
                    dao.updateAgentStatus(AgentStatusEntity("Filter", "Signal Confirmation", true, "CONFIRMED", 0.15f, "Verified liquidities of ${selectedAsset.symbol}. Market trap risk is negligible."))

                    val setupScore = Random.nextDouble(7.0, 9.8)
                    val formattedScore = String.format("%.1f", setupScore)
                    logs.add("Judge: Setup matched rating $formattedScore/10. High probability score.")
                    dao.updateAgentStatus(AgentStatusEntity("Judge", "Scoring Engine", true, "SCORED", 0.10f, "Scored Setup on ${selectedAsset.symbol} as $formattedScore. Met minimum threshold of 7.0."))

                    // Shield calculation
                    val stopLoss = if (signalDir == "BUY") selectedAsset.price * 0.995 else selectedAsset.price * 1.005
                    val takeProfit = if (signalDir == "BUY") selectedAsset.price * 1.015 else selectedAsset.price * 0.985
                    val accountRisk = user.balance * (user.riskMarginPct / 100.0)
                    val marginDistance = Math.abs(selectedAsset.price - stopLoss)
                    val baseVolume = accountRisk / (marginDistance * (if (selectedAsset.symbol.contains("BTC")) 0.1 else 1000.0))
                    val safeVolume = Math.round(baseVolume * 100.0) / 100.0

                    if (safeVolume > 0.01 && !user.isKillSwitchOn) {
                        logs.add("Shield: Risk limits evaluated. Max risk restricted to ${user.riskMarginPct}% (\$${String.format("%.2f", accountRisk)}). Authorized position size = $safeVolume Lots.")
                        dao.updateAgentStatus(AgentStatusEntity("Shield", "Risk Management", true, "SECURED", 0.10f, "Position approved. Sizing parameters strictly constrained below 1% portfolio risk."))

                        logs.add("Hands: Generating Trade order parameters for execution.")
                        dao.updateAgentStatus(AgentStatusEntity("Hands", "Trade Execution", true, "TRIGGERED", 0.05f, "Executing $signalDir Limit order, entry: ${selectedAsset.price}, targets validated."))

                        logs.add("Broker: Dispatched order execution request to LMAX Prime and Interactive Brokers.")
                        dao.updateAgentStatus(AgentStatusEntity("Broker", "Order Routing", true, "DISPATCHED", 0.05f, "Dispatched execution block with ultra low-latency parameters to Primary Liquidity pools."))

                        // Store trade in DB
                        dao.insertTrade(
                            TradeEntity(
                                symbol = selectedAsset.symbol,
                                orderType = signalDir,
                                entryPrice = selectedAsset.price,
                                currentPrice = selectedAsset.price,
                                volume = safeVolume,
                                status = "OPEN",
                                stopLoss = Math.round(stopLoss * 10000.0) / 10000.0,
                                takeProfit = Math.round(takeProfit * 10000.0) / 10000.0,
                                pnl = 0.0,
                                triggerReason = "Judged $formattedScore/10 setup by multi-agent consensus."
                            )
                        )

                        logs.add("Monitor: Entered $signalDir position on ${selectedAsset.symbol} at ${selectedAsset.price} with $safeVolume lots.")
                        dao.updateAgentStatus(AgentStatusEntity("Monitor", "Post-Trade Tracker", true, "TRACKING", 0.05f, "Monitoring newly entered $signalDir position with SL: $stopLoss, TP: $takeProfit."))
                    } else {
                        logs.add("Shield: Risk clearance REJECTED. Position size parameters out of bounds.")
                        dao.updateAgentStatus(AgentStatusEntity("Shield", "Risk Management", true, "REJECTED_RISK", 0.10f, "Aborting setup. Size requirements violate risk threshold."))
                    }
                } else {
                    logs.add("Filter: Aborting signal. Fakeout liquidity grab / news trap detected on EUR/USD dashboard.")
                    dao.updateAgentStatus(AgentStatusEntity("Filter", "Signal Confirmation", true, "TRAP_BLOCKED", 0.15f, "Liquidity patterns indicate high false breakout risk on upper bounds. Blocked trade entry."))
                    dao.updateAgentStatus(AgentStatusEntity("Judge", "Scoring Engine", true, "STANDBY", 0.10f, "Setup blocked by FilterAgent filter checklist."))
                }
            }
        }
        return logs
    }
}
