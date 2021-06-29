package com.example.remainderbday.data.db


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.remainderbday.data.Reminders

@Dao
interface RemindersDao{
    /*Room automatically handle liveData on a background thread*/
    @Query("SELECT * FROM reminders ORDER BY _id DESC")
     fun getReminders():LiveData<List<Reminders>>

    @Query("SELECT * FROM reminders ORDER BY _id DESC")
     fun getRemindersList():List<Reminders>?

    @Query("SELECT * FROM reminders WHERE reminder_identifier = :reminderId")
      fun getReminderById(reminderId:String):Reminders?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
      fun insertReminder(reminder:Reminders) : Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
     fun updateReminder(reminder:Reminders)

    @Delete
     fun delete(reminder: Reminders)

    @Query("DELETE FROM reminders")
     fun clearAllreminders()

    @Query("SELECT * FROM reminders ORDER BY _id DESC LIMIT 1")
      fun getLatestReminder():Reminders?

}