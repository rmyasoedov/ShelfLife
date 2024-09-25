package com.shelflife.instrument.util

import android.app.Activity
import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object Keyboard {

    interface KeyboardEvent{
        fun onChange(status: Boolean)
    }

    fun showKeyboardListener(activity: Activity, listener: KeyboardEvent){
        // Получаем корневой View
        val rootView = activity.window.decorView.rootView

        // Добавляем слушатель для отслеживания изменений размеров окна
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val r = Rect()
            rootView.getWindowVisibleDisplayFrame(r)

            // Высота видимой области окна
            val visibleHeight = r.height()

            // Полная высота окна
            val totalHeight = rootView.height

            // Разница между полной высотой и видимой высотой
            val heightDiff = totalHeight - visibleHeight

            // Проверяем, является ли разница высоты значительной,
            // чтобы считать, что клавиатура поднята
            val keyboardOpen = heightDiff > (totalHeight * 0.15)

            //Возвращаем признак отображения клавиатуры
            listener.onChange(keyboardOpen)
        }
    }

    fun showKeyboard(view: EditText, activity: Activity){
        view.requestFocus()
        (activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(view,  0)
    }

    //Отложенный запуск клавиатуры
    fun showKeyboardDelay(editText: EditText,activity: Activity){
        CoroutineScope(Dispatchers.Main).launch {
            delay(500)
            showKeyboard(editText, activity)
        }
    }

    // Сокрытие клавиатуры
    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}