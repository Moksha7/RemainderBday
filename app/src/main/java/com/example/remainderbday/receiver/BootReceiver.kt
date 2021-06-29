package com.example.remainderbday.receiver

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.remainderbday.data.Reminders
import com.example.remainderbday.repositories.Repository
import com.example.remainderbday.util.AlarmUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@KoinApiExtension
class BootReceiver : BroadcastReceiver(), KoinComponent {
    private var reminders: List<Reminders>? = null

    companion object {
        private const val TAG = "BootReceiver"
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        //@ this stage all reminders are cancelled
        //we need to create alarms for each reminder that was active
        intent?.let {
            Log.d(TAG, "onReceive: intent not null ")
            if (intent.action == "android.intent.action.BOOT_COMPLETED") {
                Log.d(TAG, "onReceive: intent action is correct")
                val alarmManager = context!!.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val repository: Repository by inject()
                CoroutineScope(Dispatchers.Main).launch {
                    reminders = repository.getRemindersList()
                    Log.d(TAG, "reminders $reminders")
                    val listOfReminders = reminders //Read, so we can smartCast
                    if (!listOfReminders.isNullOrEmpty()) {
                        listOfReminders.forEach { reminder ->
                            if (reminder.isActive) {
                                AlarmUtil.createAlarm(
                                    context.applicationContext,
                                    reminder,
                                    alarmManager
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}