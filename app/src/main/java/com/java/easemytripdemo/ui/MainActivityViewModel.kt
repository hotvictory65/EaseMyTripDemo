package com.java.easemytripdemo.ui

import android.app.Application
import androidx.lifecycle.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.java.easemytripdemo.dao.TripDataDao
import com.java.easemytripdemo.entity.Locations
import com.java.easemytripdemo.entity.TripData
import com.java.easemytripdemo.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel
@Inject
constructor(private val mainRepository: MainRepository, val tripDataDao: TripDataDao, application: Application
) : AndroidViewModel(application) {


    val dataState: LiveData<List<TripData>> = mainRepository.getTripData(tripDataDao)
    lateinit var tripId : List<Int>
    lateinit var tripData : String
    val applicationContext = application



    fun getTripId() {
        viewModelScope.launch(Dispatchers.IO) {
            tripId = mainRepository.getTripId(tripDataDao)
        }
    }

    fun getDataFromRoom()  {
        viewModelScope.launch(Dispatchers.IO) {
            val gson =  Gson()
            updateTripData(gson.toJson(mainRepository.getTripDataToShow(tripDataDao)))
        }
    }

    private fun updateTripData(tripDataFromIo: String) {
        viewModelScope.launch(Dispatchers.Main) {
            tripData = tripDataFromIo
        }
    }

}