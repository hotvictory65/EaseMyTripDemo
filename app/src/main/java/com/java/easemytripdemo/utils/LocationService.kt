package com.java.easemytripdemo.utils

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.java.easemytripdemo.R
import com.java.easemytripdemo.dao.TripDataDao
import com.java.easemytripdemo.entity.Locations
import com.java.easemytripdemo.entity.TripData
import com.java.easemytripdemo.ui.MainActivity
import com.java.easemytripdemo.utils.PermissionUtils.covertEpochTODateFormat
import com.java.easemytripdemo.utils.PermissionUtils.log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val TAG = "LocationService"

@AndroidEntryPoint
@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class LocationService : Service() {



    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false
    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationRequest :LocationRequest
    private lateinit var mLocationCallback :LocationCallback
    private lateinit var  tripData :TripData
    private lateinit var locationList: ArrayList<Locations>

    @Inject lateinit var tripDataDao : TripDataDao


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        val notification = createNotification()
        startForeground(1, notification)
    }

    @DelicateCoroutinesApi
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand executed with startId: $startId")
        if (intent != null) {
            val action = intent.action
            Log.d(TAG,"using an intent with action $action")
            when (action) {
                Actions.START.name -> startService()
                Actions.STOP.name -> stopService()
                else -> Log.d(TAG,"This should never happen. No action in the received intent")
            }
        } else {
            Log.d(TAG,
                "with a null intent. It has been probably restarted by the system."
            )
        }
        // by returning this we make sure the service is restarted if the system kills the service
        return START_STICKY
    }


    @SuppressLint("MissingPermission")
    fun  setUpLocationListener() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 2 seconds with high accuracy
        locationRequest = LocationRequest().setInterval(5000).setFastestInterval(5000).setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {

                    val locations = Locations(location.latitude,location.latitude,covertEpochTODateFormat(location.time),location.accuracy)
                    locationList.add(locations)
                    tripData.locations = locationList
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            tripDataDao.insert(tripData)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }

                    Log.d(
                        TAG,
                        "onLocationResult: ${location.latitude.toString() + " " + location.latitude.toString() + " " + location.accuracy +" "+  covertEpochTODateFormat(location.time)} "
                    )

                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            mLocationCallback,
            Looper.myLooper()
        )

    }


    /**
     * <h4>stopLocationUpdates</h4>
     *
     * This method is used for removing the Location Updates.
     */
    private fun stopLocationUpdates() {

        // Removing location updates
        if(this::fusedLocationProviderClient.isInitialized && this::mLocationCallback.isInitialized) {
            fusedLocationProviderClient.let {
                it.removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener(OnCompleteListener { task: Task<Void?>? ->
                        log("Location updates stopped!")
                    })
            }
        }
    }



    private fun startService() {
        if (isServiceStarted) return
        log("Starting the foreground service task")
        Toast.makeText(this, "Service starting its task", Toast.LENGTH_SHORT).show()
        isServiceStarted = true
        setServiceState(this, ServiceState.STARTED)
        locationList = ArrayList()
        tripData = TripData();

        tripData.tripId = 1

        tripData.startTime =PermissionUtils.getCurrentDate()

        // we need this lock so our service gets not affected by Doze Mode
        wakeLock =
            (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                    acquire()
                }
            }

        setUpLocationListener()
    }


    private fun stopService() {
        log("Stopping the foreground service")
        Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show()
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                }
            }

            tripData.endTime =PermissionUtils.getCurrentDate()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    tripDataDao.insert(tripData)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }


            stopForeground(true)
            stopSelf()
        } catch (e: Exception) {
            log("Service stopped without being started: ${e.message}")
        }
        stopLocationUpdates()
        isServiceStarted = false
        setServiceState(this, ServiceState.STOPPED)
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        val restartServiceIntent = Intent(applicationContext, LocationService::class.java).also {
            it.setPackage(packageName)
        };
        val restartServicePendingIntent: PendingIntent = PendingIntent.getService(this, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
        applicationContext.getSystemService(Context.ALARM_SERVICE);
        val alarmService: AlarmManager = applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager;
        alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePendingIntent);
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "EMT SERVICE CHANNEL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(notificationChannelId,
                "emt Service notifications channel",
                NotificationManager.IMPORTANCE_HIGH
            ).let {
                it.description = "EMT trip service"
                it.enableLights(true)
                it.lightColor = Color.RED
                it.enableVibration(true)
                it.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
                it
            }
            notificationManager.createNotificationChannel(channel)
        }

        val pendingIntent: PendingIntent = Intent(this, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(this, 0, notificationIntent, 0)
        }

        val builder: Notification.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Notification.Builder(
            this,
            notificationChannelId
        ) else Notification.Builder(this)

        return builder
            .setContentTitle("EMT Service")
            .setContentText("User location is fetching... ")
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_emt)
            .setTicker("Ticker text")
            .setPriority(Notification.PRIORITY_HIGH) // for under android 26 compatibility
            .build()
    }

}
