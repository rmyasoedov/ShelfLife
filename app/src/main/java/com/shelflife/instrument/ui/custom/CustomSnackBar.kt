package com.shelflife.instrument.ui.custom

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.snackbar.Snackbar
import com.shelflife.instrument.R
import com.shelflife.instrument.databinding.CustomSnackbarBinding

enum class TypeMessage{
    ERROR, WARNING, MESSAGE
}

class CustomSnackBar {
    companion object{
        @SuppressLint("RestrictedApi")
        fun showSnackBar(view: ViewGroup, layoutInflater: LayoutInflater, typeMessage: TypeMessage, message: String): Snackbar?{
            try {
                val snackbar = Snackbar.make(view, "", 3000)
                val binding = CustomSnackbarBinding.inflate(layoutInflater, view, false)
                snackbar.view.setBackgroundColor(Color.TRANSPARENT)
                snackbar.view.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                val param: MarginLayoutParams = snackbar.view.layoutParams as MarginLayoutParams
                param.setMargins(0,0,0,0)
                snackbar.view.layoutParams = param

                // now change the layout of the snackbar
                val snackbarLayout = snackbar.view as Snackbar.SnackbarLayout

                val params = snackbarLayout.layoutParams as CoordinatorLayout.LayoutParams
                params.gravity = Gravity.TOP

                // set padding of the all corners as 0
                snackbarLayout.setPadding(0, 0, 0, 0)

                binding.tvText.text = message

                val bgViewDrawable = binding.clMain.background

                val colorInt = when(typeMessage){
                    TypeMessage.ERROR -> {
                        binding.ivIcon.setImageResource(R.drawable.ic_error_red)
                        binding.ivIcon.visibility = View.VISIBLE
                        R.color.sb_error_color
                    }
                    TypeMessage.WARNING -> {
                        binding.ivIcon.setImageResource(R.drawable.ic_warning_fff)
                        binding.ivIcon.visibility = View.VISIBLE
                        R.color.sb_warning_color
                    }
                    TypeMessage.MESSAGE -> {
                        binding.ivIcon.visibility = View.GONE
                        R.color.sb_message_color
                    }
                }
                val wrappedDrawable = DrawableCompat.wrap(bgViewDrawable)
                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(view.context.applicationContext, colorInt))

                binding.ivClose.setOnClickListener {
                    snackbar.dismiss()
                }

                snackbarLayout.addView(binding.root, 0)
                snackbarLayout.layoutParams = params
                snackbar.show()

                return snackbar
            }catch (e: Exception){
                return null
            }
        }
    }
}