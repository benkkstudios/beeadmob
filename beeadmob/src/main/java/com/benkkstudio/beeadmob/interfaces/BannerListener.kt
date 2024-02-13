package com.benkkstudio.beeadmob.interfaces

import com.google.android.gms.ads.LoadAdError

interface BannerListener {
    fun loaded()
    fun failLoad(error: LoadAdError)
}