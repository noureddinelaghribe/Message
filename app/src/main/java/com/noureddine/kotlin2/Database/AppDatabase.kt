package com.noureddine.kotlin2.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noureddine.kotlin2.DAO.ConversationDao
import com.noureddine.kotlin2.DAO.UserDao
import com.noureddine.kotlin2.model.Conversation
import com.noureddine.kotlin2.model.User
import com.noureddine.kotlin2.utel.AppConstants.DATABASE_NAME
import com.noureddine.kotlin2.utel.AppConstants.VERSTION_DATABASE
import com.noureddine.kotlin2.utel.Converters

@Database(entities = [User::class, Conversation::class], version = VERSTION_DATABASE)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun conversationDao(): ConversationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java,DATABASE_NAME).build()
                INSTANCE = instance
                instance
            }
        }
    }
}