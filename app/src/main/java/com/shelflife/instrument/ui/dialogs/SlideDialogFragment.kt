package com.shelflife.instrument.ui.dialogs


import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.Nullable
import androidx.fragment.app.DialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

open class SlideDialogFragment : DialogFragment() {

    private var durationAnimate = 500L
    private var closeDuration = 400L

    private var childView: View ?= null
    private var parentView: ViewGroup ?= null

    //Признак закрытия без выполнения действия на диалоге
    var onlyClose = true

    fun showView(contentView: View, mainView: View){
        contentView.visibility = View.GONE
        this.childView = contentView
        this.parentView = mainView as ViewGroup

        CoroutineScope(Dispatchers.Main).launch {
            delay(150)
            val transition: android.transition.Transition = android.transition.Slide(Gravity.BOTTOM)
            transition.duration = durationAnimate
            transition.addTarget(contentView)
            android.transition.TransitionManager.beginDelayedTransition(mainView, transition)
            contentView.visibility = View.VISIBLE
        }
    }

    fun closeView(contentView: View?=null, mainView: View?=null){
        val view = contentView?:this.childView
        val parentView = if (mainView==null) this.parentView else mainView as ViewGroup
        CoroutineScope(Dispatchers.Main).launch {
            val transition: android.transition.Transition = android.transition.Slide(Gravity.BOTTOM)
            transition.duration = closeDuration
            transition.addTarget(view)
            android.transition.TransitionManager.beginDelayedTransition(parentView, transition)
            view!!.visibility = View.GONE
            delay(closeDuration)
            if (isAdded) {
                dismissAllowingStateLoss()
            }
        }
    }

    //Установить длительность выезжающего блока
    fun setOpenDuration(time: Long){
        this.durationAnimate = time
    }

    //Установить длительность исчезания блока
    fun setCloseDuration(time: Long){
        this.closeDuration = time
    }


    override fun onCreateDialog(@Nullable savedInstanceState: Bundle?): Dialog {
        val dialog: Dialog = super.onCreateDialog(savedInstanceState)
        val window: Window? = dialog.window
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE)
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}