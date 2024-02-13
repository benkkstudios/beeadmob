package com.benkkstudio.beeadmob.interfaces

import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

interface NativeListener {
    fun loaded(nativeAd: NativeAd)
    fun failLoad(error: LoadAdError)
}