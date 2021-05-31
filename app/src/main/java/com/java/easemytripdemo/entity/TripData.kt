package com.java.easemytripdemo.entity

import androidx.room.*
import com.google.gson.annotations.SerializedName
import com.java.easemytripdemo.roomdatabase.Converter

@Entity(tableName = "trip_data")
data class TripData(
    @PrimaryKey()
    @SerializedName("trip_id") var tripId : Int=0,
    @SerializedName("start_time") var startTime : String="",
    @SerializedName("end_time") var endTime : String="",
    @SerializedName("locations") var locations : List<Locations> = ArrayList()
)


