package com.java.easemytripdemo.roomdatabase

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.java.easemytripdemo.entity.Locations

class Converter {

    private var gson = Gson()
    @TypeConverter
    fun convertLocationListToString(list: List<Locations>): String {
        return  gson.toJson(list)
    }

    @TypeConverter
    fun convertStringToLocationList(value: String): List<Locations>? {
        val listType = object : TypeToken<List<Locations>>() {}.type
        return gson.fromJson(value, listType)
    }
}