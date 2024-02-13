package com.benkkstudio.beeadmob.interfaces

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd

interface RewardListener {
    fun loaded(rewardAd: RewardedAd)
    fun failLoad(error: LoadAdError)
    fun dismiss()
    fun failShow(error: AdError)
    fun showed()
}