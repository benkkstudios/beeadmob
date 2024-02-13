package com.benkkstudio.beeadmob.types

import android.app.Activity
import com.benkkstudio.beeadmob.BeeAdRequest
import com.benkkstudio.beeadmob.interfaces.BeeAdmobListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


internal object Interstitial {
    private var interstitialAd: InterstitialAd? = null
    private var beeAdmobListener: BeeAdmobListener? = null

    fun setListener(beeAdmobListener: BeeAdmobListener? = null) {
        this.beeAdmobListener = beeAdmobListener
    }

    fun load(activity: Activity, interstitialId: String, onFinish: (() -> Unit)? = null) {
        InterstitialAd.load(
            activity,
            interstitialId,
            BeeAdRequest.build(activity),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                    onFinish?.invoke()
                    beeAdmobListener?.interstitialListener?.failLoad(adError)
                }

                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    onFinish?.invoke()
                    beeAdmobListener?.interstitialListener?.loaded(ad)
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
                    load(activity, interstitialId, null)
                    beeAdmobListener?.interstitialListener?.dismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    callback?.invoke()
                    load(activity, interstitialId, null)
                    beeAdmobListener?.interstitialListener?.failShow(adError)
                }

                override fun onAdShowedFullScreenContent() {
                    interstitialAd = null
                }
            }
            it.show(activity)
        }
    }
}