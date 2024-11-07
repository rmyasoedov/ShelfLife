package com.shelflife.instrument.util

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.Services
import javax.inject.Inject

object AndroidSystem {

    val getServices: Services get() = getAvailableServices(MyApp.appContext)

    //Установлены ли сервисы Google
    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(context)
        return resultCode == ConnectionResult.SUCCESS
    }

    //Установлены ли сервисы Huawei
    private fun isHuaweiMobileServicesAvailable(context: Context): Boolean {
        val availability = HuaweiApiAvailability.getInstance()
        val resultCode = availability.isHuaweiMobileServicesAvailable(context)
        return resultCode == ConnectionResult.SUCCESS
    }

    private fun getAvailableServices(context: Context): Services {
        return when {
            isGooglePlayServicesAvailable(context) -> Services.GOOGLE
            isHuaweiMobileServicesAvailable(context) -> Services.HUAWEI
            else -> Services.NONE
        }
    }
}