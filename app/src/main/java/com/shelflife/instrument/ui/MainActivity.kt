package com.shelflife.instrument.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.shelflife.instrument.BundleVar
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.R
import com.shelflife.instrument.databinding.ActivityMainBinding
import com.shelflife.instrument.factory.SharedViewModelFactory
import com.shelflife.instrument.ui.custom.CustomSnackBar
import com.shelflife.instrument.ui.custom.TypeMessage
import com.shelflife.instrument.ui.menu.CategoryFragment
import com.shelflife.instrument.ui.menu.MainFragment
import com.shelflife.instrument.ui.menu.NotificationFragment
import com.shelflife.instrument.ui.menu.OptionFragment
import com.shelflife.instrument.util.AnimateView
import com.shelflife.instrument.util.Permission
import com.shelflife.instrument.util.UserScreenManager
import com.shelflife.instrument.viewmodel.SharedViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var sharedViewModelFactory: SharedViewModelFactory
    private val sharedViewModel: SharedViewModel by viewModels {sharedViewModelFactory}

    @Inject
    lateinit var userScreenManager: UserScreenManager

    private val menuItems: List<LinearLayout> by lazy {
        listOf(
            binding.llMain,
            binding.llCategory,
            binding.llExpired,
            binding.llOptions
        )
    }

    private var activeItemView: LinearLayout?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        MyApp.getComponent().inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val openProductId: Int = intent.getIntExtra(BundleVar.ProductID, 0)

        //ставим слушателя на переключение фрагметов и меняем подстветку меню в зависимости ототкрытого фрагмента
        supportFragmentManager.addOnBackStackChangedListener{
            val currentFragment = supportFragmentManager.findFragmentById(binding.fContainerView.id)

            when(currentFragment){
                is MainFragment -> {
                    setItemDownMenu(binding.llMain)
                }

                is CategoryFragment ->{
                    setItemDownMenu(binding.llCategory)
                }

                is OptionFragment -> {
                    setItemDownMenu(binding.llOptions)
                }

                is NotificationFragment -> {
                    setItemDownMenu(binding.llExpired)
                }
            }
        }

        setItemDownMenu(binding.llMain)

        menuItems.forEach { item->
            item.setOnClickListener {

                if(item==activeItemView){
                    return@setOnClickListener
                }

                activeItemView = item

                AnimateView(item).animateAlpha()

                when(item){
                    binding.llMain ->{
                        userScreenManager.openMainFragment(this)
                    }
                    binding.llExpired->{
                        userScreenManager.openNotificationFragment(this)
                    }
                    binding.llCategory->{
                        userScreenManager.openCategoryFragment(this)
                    }
                    binding.llOptions->{
                        userScreenManager.openOptionsFragment(this)
                    }
                }
            }
        }

        //Загрузка стортового экрана
        userScreenManager.openMainFragment(this, productId = openProductId)
    }

    fun setItemDownMenu(selectItem: LinearLayout?=null){
        val inactiveColor = ContextCompat.getColor(this, R.color.down_menu_color) //получаем активный цвет
        val activeColor = ContextCompat.getColor(this, R.color.active_menu_item_color) //получаем неактивный цвет

        //помечаем активный пункт меню
        selectItem?.let {
            activeItemView = it
        }

        menuItems.forEach { item->
            val icon = item.getChildAt(0) as ImageView
            val textView = item.getChildAt(1) as TextView

            // Получение векторного рисунка иконки
            val drawable = icon.drawable

            if(item==(selectItem ?: menuItems[0])){
                textView.setTextColor(activeColor)
                drawable?.setTint(activeColor)// Установка цвета фона
            }else{
                textView.setTextColor(inactiveColor)
                drawable?.setTint(inactiveColor)// Установка цвета фона
            }

            val modifiedDrawable = drawable?.mutate() // Получение Drawable с измененными цветами
            icon.setImageDrawable(modifiedDrawable) // Установка измененного Drawable в ImageView
        }
    }

    override fun onBackPressed() {
        if(supportFragmentManager.findFragmentById(R.id.fContainerView) is MainFragment){
            finish()
            return
        }

        super.onBackPressedDispatcher.onBackPressed()
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Permission.requestPermission(android.Manifest.permission.POST_NOTIFICATIONS, this)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                sharedViewModel.snackBarMessage.collect{
                    showSnackBar(it)
                }
            }
        }
    }

    fun showSnackBar(message: String){
        CustomSnackBar.showSnackBar(binding.root, layoutInflater, TypeMessage.MESSAGE, message)
    }
}