package com.shelflife.instrument.repository

import com.shelflife.instrument.api.MyResponse
import com.shelflife.instrument.api.NetClientApi
import com.shelflife.instrument.model.BaseProduct
import javax.inject.Inject

class BarcodeNetRepository @Inject constructor(private val netClientApi: NetClientApi) {
    suspend fun loadBarcodeData(barcode: String): MyResponse<BaseProduct>{
        return MyResponse.getResponse(netClientApi.getBarcodeData(barcode))
    }
}