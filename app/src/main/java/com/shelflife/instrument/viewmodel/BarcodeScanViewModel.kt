package com.shelflife.instrument.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shelflife.instrument.model.BaseProduct
import com.shelflife.instrument.repository.BarcodeNetRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RequestType{
    data object LoadStart: RequestType()
    data class onError(val message: String): RequestType()
    data class onResult(val productName: String): RequestType()
    data object LoadStop: RequestType()
}

class BarcodeScanViewModel @Inject constructor(private val barcodeNetRepository: BarcodeNetRepository): ViewModel() {

    private val loaderBarcode = MutableStateFlow<String?>(null)
    val getBarcodeResult: StateFlow<RequestType> = loaderBarcode
        .filterNotNull()
        .flatMapLatest {barcode->
            flow {
                emit(RequestType.LoadStart)
                try {
                    val result = barcodeNetRepository.loadBarcodeData(barcode)
                    if(result.error || result.body==null || result.body?.name==null){
                        throw Exception()
                    }
                    result.body?.name?.let { productName->
                        emit(RequestType.onResult(productName))
                    }
                }catch (e: Exception){

                    emit(RequestType.onError("Не удалось получить данные"))
                }
                emit(RequestType.LoadStop)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, RequestType.LoadStart)

    fun loadBarcodeData(barcode: String){
        loaderBarcode.value = barcode
        loaderBarcode.value = null //сбрасываем последнее состояние
    }
}