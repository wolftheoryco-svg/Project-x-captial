package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectXDao {
    // User Profile
    @Query("SELECT * FROM user_profile LIMIT 1")
    fun getUserProfile(): Flow<UserEntity?>

    @Query("SELECT * FROM user_profile LIMIT 1")
    suspend fun getUserProfileSynchronous(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Trade tracking
    @Query("SELECT * FROM trade_logs ORDER BY openTimestamp DESC")
    fun getAllTrades(): Flow<List<TradeEntity>>

    @Query("SELECT * FROM trade_logs WHERE status = 'OPEN'")
    suspend fun getOpenTrades(): List<TradeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrade(trade: TradeEntity): Long

    @Update
    suspend fun updateTrade(trade: TradeEntity)

    @Query("DELETE FROM trade_logs")
    suspend fun clearAllTrades()

    // Agent status tracking
    @Query("SELECT * FROM agent_status")
    fun getAgentStatuses(): Flow<List<AgentStatusEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAgentStatuses(statuses: List<AgentStatusEntity>)

    @Update
    suspend fun updateAgentStatus(status: AgentStatusEntity)

    // Market data prices
    @Query("SELECT * FROM market_data")
    fun getMarketData(): Flow<List<MarketDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMarketData(data: List<MarketDataEntity>)
}
