package com.example.remainderbday.ui.list

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.remainderbday.data.Reminders
import com.example.remainderbday.repositories.Repository
import com.example.remainderbday.util.AlarmUtil
import com.example.remainderbday.util.Event
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension

class RemindersListViewModel(
    private val repository: Repository,
    private val app: Application
) : ViewModel() {

    private val _fabNavListenner= MutableLiveData<Event<Unit>>()
    val fabNavListenner: LiveData<Event<Unit>>
        get() = _fabNavListenner


    fun fabNavTriggered(){
        _fabNavListenner.value = Event(Unit)
    }


    companion object {
        private const val TAG = "RemindersListViewModel"
    }
    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val reminders = repository.getAllReminders()
    val isEmpty = Transformations.map(reminders) {
        it.isEmpty()
    }

    fun updateReminder(reminder: Reminders) {
        viewModelScope.launch {
            repository.update(reminder)
        }
    }

    @KoinApiExtension
    fun updateAlarm(reminder: Reminders) {
            if(reminder.isActive){
                createReminderAlarm(reminder)
            }else{
                cancelExistingAlarm(reminder)
            }
    }

  @KoinApiExtension
  private  fun createReminderAlarm(reminder: Reminders) {
          AlarmUtil.createAlarm(
              app.applicationContext,
              reminder,
              alarmManager
          )
      Log.d(TAG, "createReminderAlarm: ${reminder.id}")
    }

  @KoinApiExtension
  private  fun cancelExistingAlarm(reminder: Reminders) {
        AlarmUtil.cancelAlarm(
            app.applicationContext,
            reminder,
            alarmManager
        )
      Log.d(TAG, "cancelExistingAlarm: ${reminder.id}")
    }

}