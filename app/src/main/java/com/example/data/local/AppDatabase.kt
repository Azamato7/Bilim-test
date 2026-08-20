package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.QuestionDao
import com.example.data.local.dao.StudentSubmissionDao
import com.example.data.local.dao.TestSessionDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.QuestionEntity
import com.example.data.local.entity.StudentSubmissionEntity
import com.example.data.local.entity.TestSessionEntity
import com.example.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        TestSessionEntity::class,
        QuestionEntity::class,
        StudentSubmissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun testSessionDao(): TestSessionDao
    abstract fun questionDao(): QuestionDao
    abstract fun studentSubmissionDao(): StudentSubmissionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "milliy_sertifikat_db"
                ).fallbackToDestructiveMigration(true).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
