package com.shelflife.instrument.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object Permission {

    fun requestPermission(permission: String, activity: Activity) {
        val rc = ActivityCompat.checkSelfPermission(activity, permission)
        if (rc == PackageManager.PERMISSION_GRANTED) {
            return
        }
        val permissions = arrayOf(permission)
        if (!ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                permission
            )
        ) {
            ActivityCompat.requestPermissions(activity, permissions, 1)
        }
    }

    fun checkRequestPermission(context: Context, permission: String): Boolean{
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}