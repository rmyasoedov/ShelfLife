package com.shelflife.instrument

object MyConst {

    const val CHANNEL_NAME = "Предупреждения"
    const val channelId = "warning_channel"
    const val USE_BARCODE_FINDER = true

    const val BASE_URL = "http://195.201.133.94:8000/"

    const val defaultUntilDays = "3"
    const val defaultNotifyTime = "13:00"
    const val defaultNotifyExpired = false

    const val limitMotifyMessage = 100
}

object BundleVar{
    const val ProductID = "productId"
    const val CategoryID = "categoryId"

    const val NotifyOperation = "notifyOperation"
    const val DeleteProduct = "deleteProduct"
    const val ViewProduct = "viewProduct"
    const val RemindTomorrow = "remindTomorrow"

    const val BroadcastEvent = "broadcast-event"
    const val ProductName = "productName"

}