package com.java.easemytripdemo.utils

import android.content.Context
import android.content.SharedPreferences

enum class ServiceState {
    STARTED,
    STOPPED,
}

private const val name = "EASE_MY_TRIP_NAME"
private const val key = "EASE_MY_TRIP_KEY"

fun setServiceState(context: Context, state: ServiceState) {
    val sharedPrefs = getPreferences(context)
    sharedPrefs.edit().let {
        it.putString(key, state.name)
        it.apply()
    }
}

fun getServiceState(context: Context): ServiceState {
    val sharedPrefs = getPreferences(context)
    val value = sharedPrefs.getString(key, ServiceState.STOPPED.name)
    return ServiceState.valueOf(value.toString())
}

private fun getPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(name, 0)
}
