package com.shelflife.instrument.api

import com.google.gson.JsonElement
import com.shelflife.instrument.model.BaseProduct
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface NetGreenApi {
    @Headers(
        "Cache-Control: no-cache",
        "Content-Type: application/json"
    )

    @GET("/api/v1/products/search/?storeId=2")
    fun getBarcodeData(
        @Query("search") barcode: String
    ): Call<JsonElement>
}