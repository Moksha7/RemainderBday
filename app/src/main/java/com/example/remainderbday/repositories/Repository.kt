package com.example.remainderbday.repositories

import android.content.Context
import com.example.remainderbday.data.Reminders
import com.example.remainderbday.data.db.RemindersDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class Repository(
    private val reminderDao: RemindersDao,
    private val context: Context
) {

    fun getAllReminders() = reminderDao.getReminders()

    suspend fun getRemindersList():List<Reminders>?{
        return withContext(Dispatchers.IO){
            reminderDao.getRemindersList()
        }
    }

    suspend fun getReminderById(reminderId: String): Reminders? {
        return withContext(Dispatchers.IO) {
            reminderDao.getReminderById(reminderId)
        }
    }

    suspend fun getLatestReminder():Reminders?{
        return  withContext(Dispatchers.IO){
            reminderDao.getLatestReminder()
        }
    }

      fun insert(reminder: Reminders){

            reminderDao.insertReminder(reminder)

    }

     fun update(reminder: Reminders) {

            reminderDao.updateReminder(reminder)

    }
    suspend fun delete(reminder: Reminders) {
        withContext(Dispatchers.IO) {
            reminderDao.delete(reminder)
        }
    }
}