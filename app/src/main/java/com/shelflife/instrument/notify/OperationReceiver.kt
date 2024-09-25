package com.shelflife.instrument.notify

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shelflife.instrument.BundleVar
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.repository.RoomRepository
import com.shelflife.instrument.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class OperationReceiver : BroadcastReceiver() {

    init {
        MyApp.getComponent().inject(this)
    }

    @Inject
    lateinit var roomRepository: RoomRepository

    override fun onReceive(context: Context?, intent: Intent?) {

        val productId = intent?.getIntExtra(BundleVar.ProductID,0) ?: 0

        try {
            when(intent?.action){
                //Удалить
                BundleVar.DeleteProduct -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        roomRepository.deleteProductId(productId)

                        //Отправляем id продукта на главный экран чтобы убрать его из списка
                        val intent = Intent(BundleVar.BroadcastEvent)
                        intent.putExtra(BundleVar.DeleteProduct, productId)
                        LocalBroadcastManager.getInstance(MyApp.appContext).sendBroadcast(intent)
                    }
                    Notify.cancelManager(context!!, productId)
                }
                //Напомнить завтра
                BundleVar.RemindTomorrow ->{
                    CoroutineScope(Dispatchers.Main).launch {
                        roomRepository.remindTomorrow(productId)
                    }
                    Notify.cancelManager(context!!, productId)
                }
            }
        }catch (_:Exception){}
    }
}