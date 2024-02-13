package com.benkkstudio.beeadmob.interfaces

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

interface OpenListener {
    fun loaded(openAd: AppOpenAd)
    fun failLoad(error: LoadAdError)
    fun dismiss()
    fun failShow(error: AdError)
    fun showed()
}