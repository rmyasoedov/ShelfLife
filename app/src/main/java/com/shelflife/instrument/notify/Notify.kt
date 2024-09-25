package com.shelflife.instrument.notify

import android.app.NotificationManager
import android.content.Context

object Notify {

    fun cancelManager(context: Context, id: Int){
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(id)
            val activeNotifications = notificationManager.activeNotifications.toList()

            if(activeNotifications.size==1 && activeNotifications[0].id==0){
                notificationManager.cancel(0)
            }
        }catch (_:Exception){}

    }
}