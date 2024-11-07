package com.shelflife.instrument.repository

import com.google.gson.JsonElement
import com.shelflife.instrument.api.MyResponse
import com.shelflife.instrument.api.NetClientApi
import com.shelflife.instrument.api.NetGreenApi
import com.shelflife.instrument.model.BaseProduct
import javax.inject.Inject
import javax.inject.Named

class BarcodeNetRepository @Inject constructor(
    private val netClientApi: NetClientApi,
    private val netGreenApi: NetGreenApi
) {
    suspend fun loadBarcodeData(barcode: String): MyResponse<BaseProduct>{
        return MyResponse.getResponse(netClientApi.getBarcodeData(barcode))
    }

    suspend fun loadGreenBarcodeData(barcode: String): MyResponse<JsonElement>{
        return MyResponse.getResponse(netGreenApi.getBarcodeData(barcode))
    }
}