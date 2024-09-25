package com.shelflife.instrument.viewmodel

import androidx.lifecycle.ViewModel
import com.shelflife.instrument.util.MyDateFormatter
import javax.inject.Inject

class OptionsViewModel @Inject constructor() : ViewModel() {

    fun getTimeStr(hour: Int, min: Int): String{
        val hour = if(hour<10) "0${hour}" else "$hour"
        val min = if(min<10) "0${min}" else "$min"
        return "$hour:$min"
    }

    fun getTimeInt(timeStr: String): Pair<Int, Int>{
        return MyDateFormatter.getTimeInt(timeStr)
    }
}