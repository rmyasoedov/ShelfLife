package com.shelflife.instrument.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "product",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns =  ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Product(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "product_name")
    var productName: String,
    @ColumnInfo(name = "category_id", index = true)
    var categoryId: Int,
    @ColumnInfo(name = "date_start")
    var dateStart: String,
    @ColumnInfo(name = "date_end")
    var dateEnd: String,
    @ColumnInfo(name = "shelf_life")
    var shelfLife: String,
    @ColumnInfo(name = "notification_period")
    var notificationPeriod: String,
    @ColumnInfo(name = "notification_expired")
    var notificationExpired: Boolean
)
