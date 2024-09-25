package com.shelflife.instrument.model

import com.shelflife.instrument.MyConst

data class Options(
    var defaultUntilDays: String = MyConst.defaultUntilDays,
    var isNotifyExpired: Boolean = MyConst.defaultNotifyExpired,
    var timeNotification: String = MyConst.defaultNotifyTime
)
