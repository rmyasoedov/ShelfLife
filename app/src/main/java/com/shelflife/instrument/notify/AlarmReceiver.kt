package com.shelflife.instrument.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shelflife.instrument.BundleVar
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.MyConst
import com.shelflife.instrument.R
import com.shelflife.instrument.model.room.Notification
import com.shelflife.instrument.repository.RoomRepository
import com.shelflife.instrument.ui.MainActivity
import com.shelflife.instrument.util.MyDateFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    init {
        MyApp.getComponent().inject(this)
    }

    @Inject
    lateinit var roomRepository: RoomRepository

    private lateinit var notificationManager: NotificationManager

    val groupKey = "com.shelflife.instrument.NOTIFICATION_GROUP"

    override fun onReceive(context: Context?, intent: Intent?) {

        CoroutineScope(Dispatchers.Main).launch {
            val delayProducts = roomRepository.getDelayProducts()

            delayProducts.forEach {
                val delayDays = MyDateFormatter.calculateDaysUntil(it.dateEnd)
                val delayPeriod = it.notificationPeriod.toInt()
                if(delayDays in 1..delayPeriod){
                    val textMessage = "Напоминаем! до окончания срока годности осталось ${delayPeriod} дн."
                    sendNotification(
                        context!!,
                        productId = it.id,
                        title = it.productName,
                        text = textMessage)
                    sendSummaryNotification(context)
                    roomRepository.insertNotify(Notification(
                        title = it.productName,
                        textMessage =  textMessage,
                        dateTime = System.currentTimeMillis()
                    ))
                    //удаление метки уведомления
                    roomRepository.updateProduct(it.apply {
                        notificationPeriod = ""
                    })
                }
            }

            val expiredProducts = roomRepository.getExpiredProducts()
            expiredProducts.forEach {
                val textMessage =  "Сегодня истекает срок годности товара."
                sendNotification(
                    context!!,
                    productId = it.id,
                    title = it.productName,
                    text = textMessage
                )
                sendSummaryNotification(context)
                roomRepository.insertNotify(Notification(
                    title = it.productName,
                    textMessage =  textMessage,
                    dateTime = System.currentTimeMillis()
                ))
                //удаляем метку уведомления
                roomRepository.updateProduct(it.apply {
                    notificationExpired = false
                })
            }
        }


    }

    fun sendNotification(context: Context, productId: Int,  title: String, text: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationBuilder = NotificationCompat.Builder(context, MyConst.channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_small_icon)  // Убедитесь, что у вас есть иконка
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // Приоритет уведомления
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(groupKey)
            .addAction(0, "Просмотр", intentViewProduct(productId))
            .addAction(0, "Удалить", actionIntent(BundleVar.DeleteProduct, productId))
            .addAction(0, "Напомнить завтра", actionIntent(BundleVar.RemindTomorrow, productId))

        val notification = notificationBuilder.build()

        createNotificationChannel(context)
        notificationManager.notify(productId, notification)
    }

    fun actionIntent(operation: String, productId: Int): PendingIntent{
        val intent = Intent(MyApp.appContext, OperationReceiver::class.java).apply {
            action = operation
            putExtra(BundleVar.ProductID, productId)
        }

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            MyApp.appContext,
            productId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE  // Используем флаг IMMUTABLE для Android 12+
        )

        return pendingIntent
    }

    private fun intentViewProduct(productId: Int): PendingIntent{
        val activityIntent = Intent(MyApp.appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra(BundleVar.ProductID, productId)
        }

        // Создаем PendingIntent для запуска Activity из уведомления
        val pendingIntent = PendingIntent.getActivity(
            MyApp.appContext, productId, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        return pendingIntent
    }

    fun sendSummaryNotification(context: Context) {
        val summaryNotification = NotificationCompat.Builder(context, MyConst.channelId)
//            .setContentTitle("У вас новые уведомления")
//            .setContentText("Нажмите, чтобы увидеть все уведомления")
            .setSmallIcon(R.drawable.ic_small_icon)
//            .setStyle(NotificationCompat.InboxStyle() // Стиль для суммарного уведомления
//                .setSummaryText("У вас несколько уведомлений")
//            )
            .setAutoCancel(true)
            .setGroup(groupKey)  // Привязываем к той же группе
            .setGroupSummary(true)  // Указываем, что это суммарное уведомление
            .build()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(0, summaryNotification)
    }

    private fun createNotificationChannel(context: Context) {
        // Проверка для Android 8.0 и выше
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = MyConst.CHANNEL_NAME
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(MyConst.channelId, name, importance)
            //channel.description = "Channel Description"
            // Регистрируем канал в системе
            notificationManager.createNotificationChannel(channel)
        }
    }
}