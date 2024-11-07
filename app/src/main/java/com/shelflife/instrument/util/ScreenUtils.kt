package com.shelflife.instrument.util

import android.app.Activity
import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets

object ScreenUtils {
    fun dpToPx(context: Context?, dp: Float): Float {
        return if (context == null) {
            (-1).toFloat()
        } else dp * context.resources.displayMetrics.density
    }

    fun pxToDp(context: Context?, px: Float): Float {
        return if (context == null) {
            (-1).toFloat()
        } else px / context.resources.displayMetrics.density
    }

    fun getWidthScreen(context: Context): Int{
        return context.resources.displayMetrics.widthPixels
    }

    fun getHeightScreen(context: Context): Int{
        return context.resources.displayMetrics.heightPixels
    }

    fun getHeightFullscreen(activity: Activity): Int{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Для API 30 и выше
            val windowMetrics = activity.windowManager.currentWindowMetrics
            windowMetrics.bounds.height()
        } else {
            // Для более старых версий (API 23-29)
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
            displayMetrics.heightPixels
        }
    }

    fun dimenDpToPxInt(context: Context?, dimen: Int): Int{
        return context!!.resources.getDimension(dimen).toInt()
    }
}