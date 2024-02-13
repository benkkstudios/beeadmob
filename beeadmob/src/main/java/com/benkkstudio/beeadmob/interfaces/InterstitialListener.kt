package com.benkkstudio.beeadmob.interfaces

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd

interface InterstitialListener {
    fun loaded(interstitialAd: InterstitialAd)
    fun failLoad(error: LoadAdError)
    fun dismiss()
    fun failShow(error: AdError)
    fun showed()
}