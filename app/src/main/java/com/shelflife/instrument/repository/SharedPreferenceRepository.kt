package com.shelflife.instrument.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.shelflife.instrument.model.Options
import javax.inject.Inject

class SharedPreferenceRepository @Inject constructor(private val mSettings: SharedPreferences) {

    companion object{
        const val APP_OPTIONS = "appOptions"
    }

    fun saveOptions(dataOptions: Options){
        val jsonOptions = Gson().toJson(dataOptions)
        mSettings.edit().putString(APP_OPTIONS, jsonOptions).apply()
    }

    fun getOptions(): Options?{
        mSettings.getString(APP_OPTIONS, null)?.let { jsonOptions ->
            return Gson().fromJson(jsonOptions, Options::class.java)
        }
        return null
    }
}