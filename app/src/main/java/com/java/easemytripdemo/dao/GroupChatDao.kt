package com.java.easemytripdemo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.java.easemytripdemo.entity.TripData

@Dao
interface TripDataDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tripData: TripData): Long

    @Query("SELECT * FROM trip_data")
    fun getTripData(): LiveData<List<TripData>>

    @Query("SELECT tripId FROM trip_data")
    fun getTripId(): List<Int>

    @Query("SELECT * FROM trip_data")
    suspend fun getTripDataForExport(): List<TripData>
}