package com.shelflife.instrument.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shelflife.instrument.viewmodel.OptionsViewModel
import javax.inject.Inject
import javax.inject.Provider


class OptionsViewModelFactory @Inject constructor(
    private val viewModelProvider: Provider<OptionsViewModel>
): ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(OptionsViewModel::class.java)){
            return viewModelProvider.get() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}