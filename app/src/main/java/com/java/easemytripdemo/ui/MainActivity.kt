package com.java.easemytripdemo.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.java.easemytripdemo.R
import com.java.easemytripdemo.databinding.ActivityMainBinding
import com.java.easemytripdemo.utils.*
import com.java.easemytripdemo.utils.PermissionUtils.log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var mBinding :ActivityMainBinding
    private lateinit var viewModel :MainActivityViewModel
    private lateinit var activityAdapter: MainActivityAdapter
    private lateinit var  mIntent: Intent
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        setContentView(mBinding.root)
        mBinding.rvUserLocation.apply {
            layoutManager =  LinearLayoutManager(context)
            activityAdapter = MainActivityAdapter()
            adapter =activityAdapter
        }

        mBinding.btEndTrip.setOnClickListener(this)
        mBinding.btStartTrip.setOnClickListener(this)
        mBinding.btExportData.setOnClickListener(this)
        subscribeTripData()
    }

    private fun subscribeTripData() {
        var count = 0
        viewModel.dataState.observe(this, Observer { user ->
            Log.d(TAG, "subscribeTripData: Called")
            activityAdapter.setData(user)
            ++count
            mBinding.rvUserLocation.smoothScrollToPosition((count))
            viewModel.getDataFromRoom()
        })

    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isPermissionRequired() -> {
                when{
                    PermissionUtils.isAccessFineLocationGranted(this) -> {
                        when {
                            PermissionUtils.isLocationEnabled(this) -> {
                                mBinding.btStartTrip.isEnabled = true
                            }
                            else -> {
                                PermissionUtils.showGPSNotEnabledDialog(this)
                            }
                        }
                    }

                    else -> {
                        PermissionUtils.requestAccessFineLocationPermission(
                            this,
                            LOCATION_PERMISSION_REQUEST_CODE
                        )
                    }
                }
            }
            else -> mBinding.btStartTrip.isEnabled = true
        }
    }


    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            mBinding.btStartTrip.isEnabled = true
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(this, getString(R.string.location_permission_not_granted), Toast.LENGTH_LONG).show()
                    onStart()
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            mBinding.btEndTrip.id ->  actionOnService(Actions.STOP)
            mBinding.btStartTrip.id -> actionOnService(Actions.START)
            mBinding.btExportData.id -> {
                viewModel.getDataFromRoom()
                val intent = Intent(this , ShowJsonData::class.java)
                intent.putExtra("JsonData",viewModel.tripData)
                startActivity(intent)
            }

        }
    }


    private fun actionOnService(action: Actions) {
        if (getServiceState(this) == ServiceState.STOPPED && action == Actions.STOP) return
        Intent(this, LocationService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                log("Starting the service in >=26 Mode")
                startForegroundService(it)
                return
            }
            log("Starting the service in < 26 Mode")
            startService(it)
        }
    }

}