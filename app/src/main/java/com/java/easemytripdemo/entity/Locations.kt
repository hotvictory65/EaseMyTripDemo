package com.java.easemytripdemo.entity

import com.google.gson.annotations.SerializedName

data class Locations(
    @SerializedName("latitude") var latitude : Double,
    @SerializedName("longitude") var longitude : Double,
    @SerializedName("timestamp") var timestamp : String,
    @SerializedName("accuracy") var accuracy : Float
)
