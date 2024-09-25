package com.shelflife.instrument.ui

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.di.AppComponent
import com.shelflife.instrument.ui.custom.CustomSnackBar
import com.shelflife.instrument.ui.custom.TypeMessage

open class BaseFragment : Fragment() {

    protected val appComponent: AppComponent = MyApp.getComponent()
    protected var snackbar: Snackbar? = null

    protected fun showSnackBar(view: ViewGroup, typeMessage: TypeMessage, message: String){
        snackbar = CustomSnackBar.showSnackBar(
            view = view,
            layoutInflater = layoutInflater,
            typeMessage = typeMessage,
            message = message
        )
    }

    fun View.visible(){
        this.visibility = View.VISIBLE
    }

    fun View.invisible(){
        this.visibility = View.INVISIBLE
    }

    fun View.gone(){
        this.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        snackbar?.dismiss()
    }
}