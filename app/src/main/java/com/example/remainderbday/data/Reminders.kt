package com.example.remainderbday.data

import android.annotation.SuppressLint
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "reminders")
data class Reminders(

    @ColumnInfo(name = "_id")
    @PrimaryKey(autoGenerate = true)
    var id:Int =0,


    @ColumnInfo(name="reminder_identifier")
    var reminderIndentifier:String = UUID.randomUUID().toString(),


    @ColumnInfo(name = "title")
    var title: String = "",

    @SuppressLint("SimpleDateFormat")
    var date: String = SimpleDateFormat("EEE, d MMM yyyy").format(Date()),

    var time: String = SimpleDateFormat("h:mm a").format(Date()),

    var repeat:Boolean = true,

    @ColumnInfo(name = "repeat_value")
    var repeatValue:Int = 1,

    @ColumnInfo(name = "repeat_unit")
    var repeatUnit:String = "Day",

    @ColumnInfo(name = "active")
    var isActive:Boolean = true

    )