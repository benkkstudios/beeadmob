package com.benkkstudio.beeadmob.interfaces

import com.benkkstudio.beeadmob.AdsType
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
    open fun onRewardLoaded( rewardAd: RewardedAd) {}
    open fun onOpenLoaded(openAd: AppOpenAd) {}
    open fun onNativeLoaded(nativeAd: NativeAd) {}
    open fun onAdFailedToLoad(type: AdsType, error: LoadAdError) {}
    open fun onDismissFullScreenContent(type: AdsType) {}
    open fun onFailedShowFullScreenContent(type: AdsType, error: AdError) {}
    open fun onAdShowedFullScreenContent(type: AdsType) {}

    val initListener = object : InitListener {
        override fun onInit() {
            onInitialized()
        }
    }

    val nativeListener = object : NativeListener {
        override fun loaded(nativeAd: NativeAd) {
            onNativeLoaded( nativeAd)
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(AdsType.NATIVE, error)
        }
    }

    val openListener = object : OpenListener {
        override fun loaded(openAd: AppOpenAd) {
            onOpenLoaded(openAd)
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(AdsType.APP_OPEN, error)
        }

        override fun dismiss() {
            onDismissFullScreenContent(AdsType.APP_OPEN)
        }

        override fun failShow(error: AdError) {
            onFailedShowFullScreenContent(AdsType.APP_OPEN, error)
        }

        override fun showed() {
            onAdShowedFullScreenContent(AdsType.APP_OPEN)
        }
    }
    val rewardListener = object : RewardListener {
        override fun loaded(rewardAd: RewardedAd) {
            onRewardLoaded(rewardAd)
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(AdsType.REWARD, error)
        }

        override fun dismiss() {
            onDismissFullScreenContent(AdsType.REWARD)
        }

        override fun failShow(error: AdError) {
            onFailedShowFullScreenContent(AdsType.REWARD, error)
        }

        override fun showed() {
            onAdShowedFullScreenContent(AdsType.REWARD)
        }
    }

    val bannerListener = object : BannerListener {
        override fun loaded() {
            onBannerLoaded()
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(AdsType.BANNER, error)
        }
    }

    val interstitialListener = object : InterstitialListener {
        override fun loaded(interstitialAd: InterstitialAd) {
            onInterstitialLoaded(interstitialAd)
        }

        override fun failLoad(error: LoadAdError) {
            onAdFailedToLoad(AdsType.INTERSTITIAL, error)
        }

        override fun dismiss() {
            onDismissFullScreenContent(AdsType.INTERSTITIAL)
        }

        override fun failShow(error: AdError) {
            onFailedShowFullScreenContent(AdsType.INTERSTITIAL, error)
        }

        override fun showed() {
            onAdShowedFullScreenContent(AdsType.INTERSTITIAL)
        }
    }

}