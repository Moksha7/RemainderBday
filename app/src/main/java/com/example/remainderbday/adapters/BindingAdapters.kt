package com.example.remainderbday.adapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.remainderbday.data.Reminders


@BindingAdapter("remindersList")
fun RecyclerView.submitReminderList(reminders: List<Reminders>?) {
    val adapter = this.adapter as RemindersListAdapter
    adapter.submitList(reminders)
}



