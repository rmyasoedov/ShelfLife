package com.shelflife.instrument.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "text_message")
    var textMessage: String,
    @ColumnInfo(name = "date_time")
    var dateTime: Long
)