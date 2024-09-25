package com.shelflife.instrument.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import javax.inject.Inject

class SharedViewModel @Inject constructor() : ViewModel() {

    private val _snackBarMessage = MutableLiveData<String>()
    val snackBarMessage: LiveData<String> get() = _snackBarMessage

    fun showSnackBar(message: String) {
        _snackBarMessage.value = message
    }
}