@file:Suppress("DEPRECATION")

package com.benkkstudio.beeadmob.types

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.benkkstudio.beeadmob.BeeAdRequest
import com.benkkstudio.beeadmob.interfaces.BeeAdmobListener
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

internal class AppOpenManager private constructor(
    private val application: Application,
    private val openId: String,
    private val blockedActivity: ArrayList<AppCompatActivity>? = arrayListOf(),
    onFinish: (() ->
    Unit)? = null,
    private var beeAdmobListener: BeeAdmobListener? = null
) :
    LifecycleObserver, Application.ActivityLifecycleCallbacks {
    companion object {
        fun init(application: Application, openId: String, blockedActivity: ArrayList<AppCompatActivity>? = arrayListOf(), onFinish: (() -> Unit)? = null) {
            Builder(application)
                .setOpenId(openId)
                .setBlockedActivity(blockedActivity)
                .setOnFinish(onFinish)
                .build()
        }
    }

    private var currentActivity: Activity? = null
    private var appOpenAd: AppOpenAd? = null
    private var isShowingAd = false
    private var loadTime: Long = 0
    private var showTime: Long = 0

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        onFinish?.invoke()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfAvailable()
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
    }

    private fun showAdIfAvailable() {
        if (!isShowingAd && isAdAvailable() && isNotBlockedActivity()) {
            appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    appOpenAd = null
                    isShowingAd = false
                    showTime = Date().time
                    fetchAd()
                    beeAdmobListener?.openListener?.dismiss()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    beeAdmobListener?.openListener?.failShow(adError)
                }

                override fun onAdShowedFullScreenContent() {
                    isShowingAd = true
                    beeAdmobListener?.openListener?.showed()
                }
            }
            appOpenAd!!.show(currentActivity!!)
        } else {
            fetchAd()
        }
    }

    fun fetchAd() {
        if (isAdAvailable()) {
            return
        }
        AppOpenAd.load(application, openId, BeeAdRequest.build(application.applicationContext), object :
            AppOpenAd.AppOpenAdLoadCallback() {
            override fun onAdLoaded(ad: AppOpenAd) {
                super.onAdLoaded(ad)
                appOpenAd = ad
                loadTime = Date().time
                beeAdmobListener?.openListener?.loaded(ad)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                beeAdmobListener?.openListener?.failLoad(adError)
            }
        })
    }

    private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * 4
    }

    override fun onActivityPaused(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    private fun isNotBlockedActivity(): Boolean {
        blockedActivity?.let {
            currentActivity?.let {
                blockedActivity.forEach { activity ->
                    if (activity::class.java == it::class.java) {
                        return false
                    }
                }
            }
        }
        return true
    }

    class Builder(private val application: Application) {
        private var openId: String = ""
        private var blockedActivity: ArrayList<AppCompatActivity>? = arrayListOf()
        private var beeAdmobListener: BeeAdmobListener? = null
        private var onFinish: (() -> Unit)? = null
        fun setListener(beeAdmobListener: BeeAdmobListener? = null) = apply { this.beeAdmobListener = beeAdmobListener }
        fun setOpenId(openId: String) = apply { this.openId = openId }
        fun setBlockedActivity(blockedActivity: ArrayList<AppCompatActivity>? = arrayListOf()) = apply { this.blockedActivity = blockedActivity }
        fun setOnFinish(onFinish: (() -> Unit)? = null) = apply { this.onFinish = onFinish }
        fun build() = AppOpenManager(application, openId, blockedActivity, onFinish, beeAdmobListener)
    }
}