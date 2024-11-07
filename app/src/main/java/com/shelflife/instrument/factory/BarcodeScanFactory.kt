package com.shelflife.instrument.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shelflife.instrument.viewmodel.BarcodeScanViewModel
import javax.inject.Provider
import javax.inject.Inject

class BarcodeScanFactory @Inject constructor(
    private val viewModelProvider: Provider<BarcodeScanViewModel>
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(BarcodeScanViewModel::class.java)){
            return viewModelProvider.get() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}