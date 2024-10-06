package com.shelflife.instrument.api

import com.shelflife.instrument.model.BaseProduct
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NetClientApi {
    @Headers(
        "Cache-Control: no-cache",
        "Content-Type: application/json"
    )

    @GET("/bestbefore/v1/barcode/")
    fun getBarcodeData(
        @Query("barcode") barcode: String
    ): Call<BaseProduct>

}