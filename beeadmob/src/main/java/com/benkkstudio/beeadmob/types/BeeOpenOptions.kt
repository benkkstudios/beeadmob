package com.benkkstudio.beeadmob.types

import android.app.Application
import androidx.appcompat.app.AppCompatActivity

class BeeOpenOptions {
    class Builder(val application: Application) {
        var blockedActivity: ArrayList<AppCompatActivity> = arrayListOf()
        fun setBlockedActivity(blockedActivity: ArrayList<AppCompatActivity>) = apply { this.blockedActivity = blockedActivity }
        fun build(): Builder = Builder(application)
    }
}