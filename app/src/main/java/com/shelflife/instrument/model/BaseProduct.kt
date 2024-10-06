package com.shelflife.instrument.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class BaseProduct(
    @SerializedName("code")
    @Expose
    val code: String? = null,

    @SerializedName("name")
    @Expose
    val name: String? = null
): Serializable