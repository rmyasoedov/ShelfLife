package com.shelflife.instrument.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.model.room.Notification
import com.shelflife.instrument.model.room.Product
import java.util.concurrent.Executors

@Database(
    entities = [
        Category::class,
        Product::class,
        Notification::class
    ],
    version = 1
)
abstract class RoomDb : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {
        @Volatile
        private var INSTANCE: RoomDb? = null

        fun getDb(context: Context): RoomDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDb::class.java,
                    "room.db"
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Вызов вставки по завершению создания базы данных
                        Executors.newSingleThreadExecutor().execute {
                            // Получаем инстанс базы данных
                            INSTANCE?.let {
                                it.getDao().insertDefaultCategory()
                            }
                        }
                    }
                })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}