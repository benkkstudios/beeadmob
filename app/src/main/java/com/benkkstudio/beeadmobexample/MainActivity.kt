package com.benkkstudio.beeadmobexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.benkkstudio.beeadmob.AdsType
import com.benkkstudio.beeadmob.BeeAdmob
import com.benkkstudio.beeadmob.interfaces.BeeAdmobListener
import com.benkkstudio.beeadmob.natives.NativeView
import com.benkkstudio.beeadmob.types.BeeOpenOptions
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.rewarded.RewardedAd

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val blockedActivity = arrayListOf<AppCompatActivity>()

        val beeOpenOptions = BeeOpenOptions.Builder(App.instance())
            .setBlockedActivity(blockedActivity)
            .build()

        BeeAdmob.Builder(this)
            .setDebugMode(true)
            .setAdsUnit(AdsUtils)
            .setListener(beeAdmobListener)
            .setOpenAdsOptions(beeOpenOptions)
            .setLoadingTime(3000)
            .build()
    }

    private fun setupView() {
        val nativeView = findViewById<NativeView>(R.id.nativeView)
        val bannerContainer = findViewById<LinearLayoutCompat>(R.id.bannerContainer)
        val buttonInter = findViewById<AppCompatButton>(R.id.buttonInter)
        val buttonReward = findViewById<AppCompatButton>(R.id.buttonReward)

        BeeAdmob.showBanner(this, bannerContainer)
        BeeAdmob.showNative(nativeView)
        buttonInter.setOnClickListener {
            BeeAdmob.showInterstitial(this)
        }
        buttonReward.setOnClickListener {
            BeeAdmob.showReward(this)
        }
    }

    private val beeAdmobListener = object : BeeAdmobListener() {
        override fun onInitialized() {
            super.onInitialized()
            setupView()
        }

        override fun onBannerLoaded() {
            super.onBannerLoaded()
        }

        override fun onInterstitialLoaded(interstitial: InterstitialAd) {
            super.onInterstitialLoaded(interstitial)
        }

        override fun onRewardLoaded(rewardAd: RewardedAd) {
            super.onRewardLoaded(rewardAd)
        }

        override fun onOpenLoaded(openAd: AppOpenAd) {
            super.onOpenLoaded(openAd)
        }

        override fun onNativeLoaded(nativeAd: NativeAd) {
            super.onNativeLoaded(nativeAd)
        }

        override fun onAdFailedToLoad(type: AdsType, error: LoadAdError) {
            super.onAdFailedToLoad(type, error)
        }

        override fun onDismissFullScreenContent(type: AdsType) {
            super.onDismissFullScreenContent(type)
        }

        override fun onFailedShowFullScreenContent(type: AdsType, error: AdError) {
            super.onFailedShowFullScreenContent(type, error)
        }

        override fun onAdShowedFullScreenContent(type: AdsType) {
            super.onAdShowedFullScreenContent(type)
        }
    }
}