package com.benkkstudio.beeadmobexample

import android.app.Application

class App : Application() {
    companion object {
        private var instance: App? = null
        fun instance(): App {
            return instance!!
        }
    }


    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}