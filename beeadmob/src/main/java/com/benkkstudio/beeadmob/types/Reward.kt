package com.benkkstudio.beeadmob.types

import android.app.Activity
import com.benkkstudio.beeadmob.BeeAdRequest
import com.benkkstudio.beeadmob.interfaces.BeeAdmobListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback


internal object Reward {
    private var rewardedAd: RewardedAd? = null
    private var beeAdmobListener: BeeAdmobListener? = null

    fun setListener(beeAdmobListener: BeeAdmobListener? = null) {
        this.beeAdmobListener = beeAdmobListener
    }

    fun load(activity: Activity, rewardId: String, onFinish: (() -> Unit)? = null) {
        RewardedAd.load(
            activity,
            rewardId,
            BeeAdRequest.build(activity),
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                    onFinish?.invoke()
                    beeAdmobListener?.rewardListener?.failLoad(adError)
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                    onFinish?.invoke()
                    beeAdmobListener?.rewardListener?.loaded(ad)
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
                    beeAdmobListener?.rewardListener?.showed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    callback?.invoke()
                    load(activity, rewardId)
                    beeAdmobListener?.rewardListener?.failShow(adError)
                }

                override fun onAdDismissedFullScreenContent() {
                    if (rewardSuccess) {
                        callback?.invoke()
                    }
                    load(activity, rewardId)
                    beeAdmobListener?.rewardListener?.dismiss()
                }
            }
            it.show(activity) {
                rewardSuccess = true
            }
        }

    }

}