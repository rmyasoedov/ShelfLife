package com.shelflife.instrument.factory

import com.shelflife.instrument.viewmodel.SharedViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider

class SharedViewModelFactory @Inject constructor(
    private val viewModelProvider: Provider<SharedViewModel>
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SharedViewModel::class.java)){
            return viewModelProvider.get() as T
        }
        return super.create(modelClass)
    }
}