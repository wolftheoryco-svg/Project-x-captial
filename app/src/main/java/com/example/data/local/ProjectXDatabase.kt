package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        TradeEntity::class,
        AgentStatusEntity::class,
        MarketDataEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ProjectXDatabase : RoomDatabase() {
    abstract fun projectXDao(): ProjectXDao

    companion object {
        @Volatile
        private var INSTANCE: ProjectXDatabase? = null

        fun getDatabase(context: Context): ProjectXDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProjectXDatabase::class.java,
                    "project_x_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
