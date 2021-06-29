package com.example.remainderbday

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import com.example.remainderbday.data.db.RemindersDB
import com.example.remainderbday.repositories.Repository
import com.example.remainderbday.ui.edit.ReminderAddViewModel
import com.example.remainderbday.ui.list.RemindersListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module

class App : Application() {
    private val koinModule = module {
        single {
            //Database
            RemindersDB.getInstance(androidContext())
        }
        single {
            val db: RemindersDB = get()
            Repository(db.remindersDao, androidContext())
        }


        viewModel { (reminderId: String) ->
            ReminderAddViewModel(get(), reminderId, this@App)
        }
        viewModel {
            RemindersListViewModel(get(),this@App)
        }

    }


    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(koinModule)
        }
        createNotificationChannel(getString(R.string.reminder_notification_channel_id),
            getString(R.string.reminder_notification_channel_name),
            getString(R.string.reminder_notification_channel_description))
    }

    fun createNotificationChannel(channelId: String, channelName: String,description:String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.apply {
                enableLights(true)
                lightColor = Color.BLUE
                enableVibration(true)
            }
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
            notificationChannel.description = description
            val notificationManager = ContextCompat.getSystemService(this.applicationContext,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }


}

