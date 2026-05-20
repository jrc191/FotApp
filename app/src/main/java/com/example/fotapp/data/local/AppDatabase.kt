package com.example.fotapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// ⚠️ Version incrementada a 2 por los nuevos campos en PlayerEntity
// (appearances, rating, passAccuracy, minutesPlayed, yellowCards, redCards, saves)
// fallbackToDestructiveMigration() borra y recrea la BD automáticamente.
@Database(
    entities = [PlayerEntity::class, CommentEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun playerDao(): PlayerDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDatabase::class.java, "futconnect_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
