package com.benkkstudio.beeadmob.types

import android.app.Activity
import com.benkkstudio.beeadmob.BeeAdRequest
import com.benkkstudio.beeadmob.BeeAdmob
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


internal object Reward {
    private var rewardedAd: RewardedAd? = null

    fun load(activity: Activity, rewardId: String) {
        RewardedAd.load(
            activity,
            rewardId,
            BeeAdRequest.build(activity),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    BeeAdmob.logging("Admob RewardedAd : " + adError.message)
                    BeeAdmob.logging("Admob RewardedAd : " + adError.code)
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
            })
    }

    fun show(activity: Activity, rewardId: String, callback: (() -> Unit)? = null) {
        if (rewardedAd == null) {
            callback?.invoke()
            return
        }
        var rewardSuccess = false
        rewardedAd?.let {
            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdShowedFullScreenContent() {
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    callback?.invoke()
                    load(activity, rewardId)
                    BeeAdmob.logging("Admob RewardedAd : " + adError.message)
                    BeeAdmob.logging("Admob RewardedAd : " + adError.code)
                }

                override fun onAdDismissedFullScreenContent() {
                    if (rewardSuccess) {
                        callback?.invoke()
                    }
                    load(activity, rewardId)
                }
            }
            it.show(activity) {
                rewardSuccess = true
            }
        }

    }

}