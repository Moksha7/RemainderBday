package com.example.remainderbday.ui.edit

import android.app.AlarmManager
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.remainderbday.R
import com.example.remainderbday.data.Reminders
import com.example.remainderbday.repositories.Repository
import com.example.remainderbday.util.AlarmUtil
import com.example.remainderbday.util.Destination
import com.example.remainderbday.util.Event
import kotlinx.coroutines.launch
import org.koin.core.component.KoinApiExtension
import java.util.*

class ReminderAddViewModel(
    private val repository: Repository,
    private val reminderIdentifier: String?,
    private val app: Application
) : AndroidViewModel(app) {

    private lateinit var editableReminder: Reminders

    private val alarmManager = app.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        private const val TAG = "ReminderEditViewModel"
    }

    init {
        initializeReminder()
    }


    val _titleEditText = MutableLiveData<String>() //2-way binding


    val _switchRepeat = MutableLiveData<Boolean>() //2-way binding

    private val _dateText = MutableLiveData<String>()
    val dateText: LiveData<String>
        get() = _dateText

    private val _timeText = MutableLiveData<String>()
    val timeText: LiveData<String>
        get() = _timeText

    private val _repeatIntervalText = MutableLiveData<String>()
    val repeatIntervalText: LiveData<String>
        get() = _repeatIntervalText

    private val _repeatIntervalUnitText = MutableLiveData<String>()
    val repeatIntervalUnitText: LiveData<String>
        get() = _repeatIntervalUnitText

    private val _navigationEvent = MutableLiveData<Event<Destination>>()
    val navigationEvent: LiveData<Event<Destination>>
        get() = _navigationEvent

    private val _snackBarEvent = MutableLiveData<Event<String>>()
    val snackBarEvent: LiveData<Event<String>>
        get() = _snackBarEvent

    private val _showDeleteDialogEvent = MutableLiveData<Event<String>>()
    val showDeleteDialogEvent: LiveData<Event<String>>
        get() = _showDeleteDialogEvent

    private val _showDatePickerEvent = MutableLiveData<Event<String>>()
    val showDatePickerEvent: LiveData<Event<String>>
        get() = _showDatePickerEvent

    private val _showTimePickerEvent = MutableLiveData<Event<String>>()
    val showTimePickerEvent: LiveData<Event<String>>
        get() = _showTimePickerEvent

    private val _showNumberDialogEvent = MutableLiveData<Event<String>>()
    val showEditDialogEvent: LiveData<Event<String>>
        get() = _showNumberDialogEvent

    private val _showListDialogEvent = MutableLiveData<Event<String>>()
    val showListDialogEvent: LiveData<Event<String>>
        get() = _showListDialogEvent


    fun navigateUp() {
        _navigationEvent.value = Event(Destination.UP)
    }

    fun initializeReminder() {
        viewModelScope.launch {
            if (reminderIdentifier != null) {
                editableReminder = repository.getReminderById(reminderIdentifier) ?: Reminders()
                updateUI()
            } else {
                editableReminder = Reminders()
            }
            Log.d(TAG, "showlog: identifier is ${editableReminder.reminderIndentifier}")
            Log.d(TAG, "showlog: identifier is ${editableReminder.repeat}")
            Log.d(TAG, "showlog: identifier is ${editableReminder.date}")
            Log.d(TAG, "showlog: identifier is ${editableReminder.time}")
            Log.d(TAG, "showlog: identifier is ${editableReminder.repeatValue.toString()}")
            Log.d(TAG, "showlog: identifier is ${editableReminder.repeatUnit}")

        }
    }

    @KoinApiExtension
    fun saveReminder(title:String,
                     date:String, time: String, repeat :Boolean, interval:String,
                     unit:String) {
        if (_titleEditText.value.isNullOrEmpty()) {
            _snackBarEvent.value = Event(app.getString(R.string.snackbartext_emptyReminder))
        } else {
           updateReminder(title,date,time,repeat,interval,unit)
            viewModelScope.launch {
                if (reminderIdentifier == null) {
                    editableReminder = Reminders(0,
                        UUID.randomUUID().toString(),title, date, time, repeat,interval.toInt(),unit,true)
                    repository.insert(editableReminder)
                    editableReminder = repository.getLatestReminder()!!
                } else {
                    //editableReminder = Reminders(0,"",title, date, time, repeat,interval.toInt(),unit,true)
                    repository.update(editableReminder)
                    cancelExistingAlarm()
                }
                createReminderAlarm()
                _navigationEvent.value = Event(Destination.UP)
            }
        }
    }

   fun updateReminder(strTitle:String,strdate:String, strtime: String, brepeat :Boolean, interval:String,
    unit:String) {
        editableReminder.apply {
            title = strTitle
            date = strdate
            time = strtime
            repeat = brepeat
            repeatValue = interval.toInt()
            repeatUnit = unit
        }

    }

    fun updateUI() {
        _titleEditText.value = editableReminder.title
        _switchRepeat.value = editableReminder.repeat
        _dateText.value = editableReminder.date
        _timeText.value = editableReminder.time
        _repeatIntervalText.value = editableReminder.repeatValue.toString()
        _repeatIntervalUnitText.value = editableReminder.repeatUnit
    }


    @KoinApiExtension
    fun deleteAndNavigateToList() {
        viewModelScope.launch {
            repository.delete(editableReminder)
            cancelExistingAlarm()
            _navigationEvent.value = Event(Destination.UP)
        }
    }


    @KoinApiExtension
     fun createReminderAlarm() {
        if (editableReminder.isActive) {
            AlarmUtil.createAlarm(
                app.applicationContext,
                editableReminder,
                alarmManager
            )
        }
        Log.d("RemainderAddViewModel","alarm created")
    }

    @KoinApiExtension
    fun cancelExistingAlarm() {
        AlarmUtil.cancelAlarm(
            app.applicationContext,
            editableReminder,
            alarmManager)
    }

    fun updateDateTextView(date: String) {
        _dateText.value = date
    }

    fun updateTimeTextView(time: String) {
        _timeText.value = time
    }

    fun updateReminderUnit(item: String) {
        _repeatIntervalUnitText.value = item
    }

    fun updateReminderValue(valueInput: String) {
        _repeatIntervalText.value = valueInput
    }








}
