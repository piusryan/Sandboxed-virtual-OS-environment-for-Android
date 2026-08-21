package com.abstergo

import android.app.Application

class AbstergoOSApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: AbstergoOSApplication
            private set
    }
}
