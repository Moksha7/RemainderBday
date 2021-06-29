package com.example.remainderbday.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.remainderbday.data.Reminders

private const val REMAINDER_DB_NAME = "remainder.db"


@Database(entities = [Reminders::class], version = 1, exportSchema = false)
abstract class RemindersDB : RoomDatabase() {
    abstract val remindersDao:RemindersDao

    companion object {
        fun getInstance(context: Context): RemindersDB = Room.databaseBuilder(
            context.applicationContext,
            RemindersDB::class.java, REMAINDER_DB_NAME
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
}