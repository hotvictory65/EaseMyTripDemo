package com.java.easemytripdemo.roomdatabase


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.java.easemytripdemo.dao.TripDataDao
import com.java.easemytripdemo.entity.TripData

@Database(entities = [TripData::class], version = 1,exportSchema = false)
@TypeConverters(Converter::class)
abstract class RoomDatabaseMain: RoomDatabase() {
    abstract fun tripDap(): TripDataDao

    companion object{
        const val DATABASE_NAME: String = "ease_my_trip_db"
    }

}