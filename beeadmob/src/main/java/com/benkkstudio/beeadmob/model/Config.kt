package com.benkkstudio.beeadmob.model

import androidx.appcompat.app.AppCompatActivity
import com.benkkstudio.beeadmob.R


internal data class Config(
    var debugMode: Boolean = false,
    var loadingTime: Int = 0,
    var interstitialInterval: Int? = 0,
    var customLadingView: Int = R.layout.dialog_progress,
    var blockedActivity: ArrayList<AppCompatActivity> = arrayListOf()
)