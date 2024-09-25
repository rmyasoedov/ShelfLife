package com.shelflife.instrument.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shelflife.instrument.viewmodel.RoomViewModel
import javax.inject.Inject
import javax.inject.Provider

class RoomViewModelFactory @Inject constructor(
    private val viewModelProvider: Provider<RoomViewModel>
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(RoomViewModel::class.java)){
            return viewModelProvider.get() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}