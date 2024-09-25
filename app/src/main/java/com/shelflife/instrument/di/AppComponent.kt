package com.shelflife.instrument.di

import android.app.AlarmManager
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.ui.MainActivity
import com.shelflife.instrument.di.module.AppModule
import com.shelflife.instrument.di.module.DataBaseModule
import com.shelflife.instrument.notify.AlarmReceiver
import com.shelflife.instrument.notify.OperationReceiver
import com.shelflife.instrument.ui.ProductFragment
import com.shelflife.instrument.ui.dialogs.AboutCategoryDialog
import com.shelflife.instrument.ui.menu.CategoryFragment
import com.shelflife.instrument.ui.menu.MainFragment
import com.shelflife.instrument.ui.menu.NotificationFragment
import com.shelflife.instrument.ui.menu.OptionFragment
import com.shelflife.instrument.util.UserScreenManager
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, DataBaseModule::class])
interface  AppComponent {
    fun inject(mainActivity: MainActivity)
    fun inject(mainFragment: MainFragment)
    fun inject(categoryFragment: CategoryFragment)
    fun inject(productFragment: ProductFragment)
    fun inject(aboutCategoryDialog: AboutCategoryDialog)
    fun inject(alarmReceiver: AlarmReceiver)
    fun inject(operationReceiver: OperationReceiver)
    fun inject(optionFragment: OptionFragment)
    fun inject(notificationFragment: NotificationFragment)
    fun inject(myApp: MyApp)
}