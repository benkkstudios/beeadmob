package com.benkkstudio.beeadmob.model

import com.benkkstudio.beeadmob.R


internal data class AdmobModel(
    var debugMode: Boolean = false,
    var enableLogging: Boolean = false,
    var interstitialId: String = "",
    var bannerId: String = "",
    var rewardId: String = "",
    var nativeId: String = "",
    var openId: String = "",
    var loadingTime: Int = 0,
    var interstitialInterval: Int? = 0,
    var customLadingView: Int = R.layout.dialog_progress
)