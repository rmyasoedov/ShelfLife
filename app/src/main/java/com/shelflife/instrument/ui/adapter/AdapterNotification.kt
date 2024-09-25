package com.shelflife.instrument.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shelflife.instrument.databinding.ItemNotifyBinding
import com.shelflife.instrument.model.room.Notification
import com.shelflife.instrument.util.MyDateFormatter

class AdapterNotification(val listNotification: List<Notification>):
    RecyclerView.Adapter<AdapterNotification.NotificationViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding =
            ItemNotifyBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {holder.binding.apply {
        tvTitle.text = listNotification[position].title
        tvText.text = listNotification[position].textMessage
        tvDateTime.text = MyDateFormatter.formatUnixTime(listNotification[position].dateTime)
    }}

    override fun getItemCount() = listNotification.size

    class NotificationViewHolder(val binding: ItemNotifyBinding) : RecyclerView.ViewHolder(binding.root)
}