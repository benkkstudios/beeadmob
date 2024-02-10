package com.benkkstudio.beeadmob.types

import android.app.Activity
import com.benkkstudio.beeadmob.BeeAdRequest
import com.benkkstudio.beeadmob.BeeAdmob
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


internal object Interstitial {
    private var interstitialAd: InterstitialAd? = null

    fun load(activity: Activity, interstitialId: String) {
        InterstitialAd.load(
            activity,
            interstitialId,
            BeeAdRequest.build(activity),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    BeeAdmob.logging("InterstitialAd : " + adError.message)
                    BeeAdmob.logging("InterstitialAd : " + adError.code)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }
            })
    }


    fun show(activity: Activity, interstitialId: String, callback: (() -> Unit)? = null) {
        if (interstitialAd == null) {
            callback?.invoke()
            return
        }
        interstitialAd?.let {
            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    callback?.invoke()
                    load(activity, interstitialId)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    callback?.invoke()
                    load(activity, interstitialId)
                    BeeAdmob.logging("Admob InterstitialAd : " + adError.message)
                    BeeAdmob.logging("Admob InterstitialAd : " + adError.code)
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                }
            }
            it.show(activity)
        }
    }
}