package com.shelflife.instrument.util

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.graphics.drawable.DrawableCompat


object MyUiElementFormatter{

    fun setBgColor(view: View, bgColor: Int){
        // Получите оригинальный фон элемента
        val originalDrawable: Drawable = view.background
        // Создайте копию фонового рисунка
        val drawableCopy: Drawable = originalDrawable.constantState?.newDrawable()?.mutate() ?: originalDrawable
        // Установите цвет заливки для копии
        DrawableCompat.setTint(drawableCopy, bgColor)
        // Установите изменённый рисунок для элемента
        view.background = drawableCopy
    }

    @SuppressLint("RestrictedApi")
    fun setRadioButtonColor(radioButton: AppCompatRadioButton, uncheckedColor: Int, checkedColor: Int){
        val colorStateList = ColorStateList(
            arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            ), intArrayOf(
                uncheckedColor,
                checkedColor
            )
        )
        radioButton.supportButtonTintList = colorStateList
    }

    fun setCheckboxColor(checkBox: CheckBox, activeColor: Int, inactiveColor: Int){
        val states = arrayOf(
            intArrayOf(android.R.attr.state_checked), // активное состояние
            intArrayOf(-android.R.attr.state_checked) // неактивное состояние
        )

        val colors = intArrayOf(activeColor, inactiveColor)

        val checkBoxColorList = ColorStateList(states, colors)
        checkBox.buttonTintList = checkBoxColorList
    }
}