package com.benkkstudio.beeadmob.interfaces

import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardedAd

abstract class BeeAdmobListener {
    open fun onInitialized() {}
    open fun onBannerLoaded() {}
    open fun onInterstitialLoaded(interstitial: InterstitialAd) {}
    open fun onRewardLoaded(rewardAd: RewardedAd) {}
    open fun onOpenLoaded(openAd: AppOpenAd) {}
    open fun onNativeLoaded(nativeAd: NativeAd) {}
    open fun onAdFailedToLoad(error: LoadAdError) {}
    open fun onDismissFullScreenContent() {}
    open fun onFailedShowFullScreenContent(error: AdError) {}
    open fun onAdShowedFullScreenContent() {}

    val initListener = object : InitListener {
        override fun onInit() {
            onInitialized()
        }
    }

    val nativeListener = object : NativeListener {
        override fun loaded(nativeAd: NativeAd) {
            onNativeLoaded(nativeAd)
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(error)
        }
    }

    val openListener = object : OpenListener {
        override fun loaded(openAd: AppOpenAd) {
            onOpenLoaded(openAd)
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(error)
        }

        override fun dismiss() {
            onDismissFullScreenContent()
        }

        override fun failShow(error: AdError) {
            onFailedShowFullScreenContent(error)
        }

        override fun showed() {
            onAdShowedFullScreenContent()
        }
    }
    val rewardListener = object : RewardListener {
        override fun loaded(rewardAd: RewardedAd) {
            onRewardLoaded(rewardAd)
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(error)
        }

        override fun dismiss() {
            onDismissFullScreenContent()
        }

        override fun failShow(error: AdError) {
            onFailedShowFullScreenContent(error)
        }

        override fun showed() {
            onAdShowedFullScreenContent()
        }
    }

    val bannerListener = object : BannerListener {
        override fun loaded() {
            onBannerLoaded()
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(error)
        }
    }

    val interstitialListener = object : InterstitialListener {
        override fun loaded(interstitialAd: InterstitialAd) {
            onInterstitialLoaded(interstitialAd)
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(error)
        }

        override fun dismiss() {
            onDismissFullScreenContent()
        }

        override fun failShow(error: AdError) {
            onFailedShowFullScreenContent(error)
        }

        override fun showed() {
            onAdShowedFullScreenContent()
        }
    }

}