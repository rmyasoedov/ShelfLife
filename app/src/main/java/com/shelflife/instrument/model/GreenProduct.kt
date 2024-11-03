package com.shelflife.instrument.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GreenProduct(
    @SerializedName("title")
    @Expose
    val title: String? = null
): Serializable

data class GreenItems(
    @SerializedName("title")
    @Expose
    val title: String? = null
): Serializable
