package com.shelflife.instrument

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.huawei.hms.api.HuaweiApiAvailability
import com.shelflife.instrument.di.AppComponent
import com.shelflife.instrument.di.DaggerAppComponent
import com.shelflife.instrument.di.module.AppModule
import com.shelflife.instrument.model.Options
import com.shelflife.instrument.notify.AlarmReceiver
import com.shelflife.instrument.repository.SharedPreferenceRepository
import com.shelflife.instrument.util.MyDateFormatter
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import java.util.Calendar
import javax.inject.Inject

class MyApp : Application() {

    @Inject
    lateinit var sharedPreference: SharedPreferenceRepository

    companion object{
        private lateinit var appComponent: AppComponent
        fun getComponent(): AppComponent = appComponent
        lateinit var appContext: Context

        fun updateDailyCheck() {
            (appContext as MyApp).scheduleDailyCheck()
        }
    }

    override fun onCreate() {
        super.onCreate()
        // Creating an extended library configuration.
        val config = AppMetricaConfig.newConfigBuilder(BuildConfig.API_KEY)
            .withLocationTracking(true)
            .build()
        // Initializing the AppMetrica SDK.
        AppMetrica.activate(this, config)
        appContext = this

        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
        appComponent.inject(this)

        //println("Google: ${isGooglePlayServicesAvailable(this)}")
        //println("Huawei: ${isHuaweiMobileServicesAvailable(this)}")

        scheduleDailyCheck()
    }

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleDailyCheck() {
        cancelScheduledCheck(this)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val options = sharedPreference.getOptions() ?: Options()
        val (hour, minute) = MyDateFormatter.getTimeInt(options.timeNotification)

        // Настраиваем время
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Повторяющийся будильник каждый день
//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelScheduledCheck(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Отмена существующего будильника
        alarmManager.cancel(pendingIntent)
    }

}