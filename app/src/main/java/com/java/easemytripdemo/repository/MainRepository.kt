package com.java.easemytripdemo.repository
import androidx.lifecycle.LiveData
import com.java.easemytripdemo.dao.TripDataDao
import com.java.easemytripdemo.entity.TripData

private const val TAG = "MainRepository"

class MainRepository() {
    fun getTripData(tripDataDao: TripDataDao) : LiveData<List<TripData>>
    {
     return  tripDataDao.getTripData()
    }


    fun getTripId(tripDataDao: TripDataDao) : List<Int> {
        return tripDataDao.getTripId()
    }

    suspend fun getTripDataToShow(tripDataDao: TripDataDao) : List<TripData>
    {
        return  tripDataDao.getTripDataForExport()
    }
}