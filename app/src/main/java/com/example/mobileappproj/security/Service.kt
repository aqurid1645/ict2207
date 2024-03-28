package com.example.mobileappproj.security

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ServiceManager : Service() {
    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()

        val serviceStarter = ServiceStarter(this)
        val attach = serviceStarter.getResources(contentResolver)
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class LocalBinder : android.os.Binder() {
        fun getService(): ServiceManager = this@ServiceManager
    }
}