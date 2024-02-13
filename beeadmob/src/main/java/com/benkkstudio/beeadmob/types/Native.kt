package com.benkkstudio.beeadmob.types

import android.annotation.SuppressLint
import android.app.Activity
import com.benkkstudio.beeadmob.BeeAdRequest
import com.benkkstudio.beeadmob.interfaces.BeeAdmobListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

internal object Native {
    private val listNative = arrayListOf<NativeAd>()
    private var beeAdmobListener: BeeAdmobListener? = null
    fun setListener(beeAdmobListener: BeeAdmobListener? = null) {
        this.beeAdmobListener = beeAdmobListener
    }

    @SuppressLint("MissingPermission")
    fun load(activity: Activity, nativeId: String, onFinish: (() -> Unit)? = null) {
        val adLoader = AdLoader.Builder(activity, nativeId)
            .forNativeAd { nativeAd: NativeAd ->
                listNative.add(nativeAd)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    super.onAdFailedToLoad(adError)
                    onFinish?.invoke()
                    beeAdmobListener?.nativeListener?.failLoad(adError)
                }

                override fun onAdLoaded() {
                    super.onAdLoaded()
                    onFinish?.invoke()
                    beeAdmobListener?.nativeListener?.loaded(listNative.random())
                }
            })
            .build()
        adLoader.loadAd(BeeAdRequest.build(activity))
    }


    fun getNative(callback: (NativeAd) -> Unit) {
        if (listNative.isNotEmpty()) {
            callback.invoke(listNative.random())
        }
    }
}