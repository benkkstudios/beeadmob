package com.benkkstudio.beeadmob

import android.app.Activity
import android.app.Application
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.benkkstudio.beeadmob.model.AdmobModel
import com.benkkstudio.beeadmob.natives.NativeView
import com.benkkstudio.beeadmob.types.Banner
import com.benkkstudio.beeadmob.types.Interstitial
import com.benkkstudio.beeadmob.types.Native
import com.benkkstudio.beeadmob.types.NativeListener
import com.benkkstudio.beeadmob.types.OpenAds
import com.benkkstudio.beeadmob.types.Reward
import com.benkkstudio.beeadmob.widget.DialogLoading
import com.benkkstudio.beeconsent.BeeConsent
import com.benkkstudio.beeconsent.BeeConsentCallback

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd

class BeeAdmob {
    class DummyAdmob {
        companion object {
            const val INTERSTITIAL = "ca-app-pub-3940256099942544/1033173712"
            const val BANNER = "ca-app-pub-3940256099942544/6300978111"
            const val REWARD = "ca-app-pub-3940256099942544/5224354917"
            const val OPEN = "ca-app-pub-3940256099942544/9257395921"
            const val NATIVE = "/6499/example/native"
        }
    }

    companion object {
        private lateinit var admobModel: AdmobModel
        private lateinit var dialogLoading: DialogLoading
        private var application: Application? = null
        private var blockedActivity: ArrayList<Activity> = arrayListOf()

        internal fun build(
            activity: Activity, admobModel: AdmobModel, loadingTime: Long, application: Application?, blockedActivity: ArrayList<Activity> =
                arrayListOf()
        ) {
            this.admobModel = admobModel
            this.application = application
            this.blockedActivity = blockedActivity
            dialogLoading = DialogLoading(activity, loadingTime, admobModel.customLadingView)
            requestConsent(activity)
        }

        internal fun logging(any: Any) {
            try {
                if (admobModel.enableLogging) {
                    Log.e("ABENK : ", any.toString())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun requestConsent(activity: Activity) {
            BeeConsent.Builder(activity)
                .debugMode(admobModel.debugMode)
                .enableLogging(admobModel.enableLogging)
                .listener(object : BeeConsentCallback {
                    override fun onRequested() {
                        MobileAds.initialize(activity) {
                            loadOpenAd()
                            loadInterstitial(activity)
                            loadReward(activity)
                        }
                    }
                })
                .request()
        }

        private fun loadOpenAd() {
            application?.let {
                OpenAds.Builder(it)
                    .setOpenId(admobModel.openId)
                    .setBlockedActivity(blockedActivity)
                    .build()
            }
        }

        private fun loadInterstitial(activity: Activity) {
            if (admobModel.interstitialId.isNotEmpty()) {
                Interstitial.load(activity, admobModel.interstitialId)
            }
        }

        fun showInterstitial(activity: Activity, callback: (() -> Unit)? = null) {
            if (admobModel.interstitialId.isNotEmpty()) {
                if(admobModel.withLoading){
                    dialogLoading.showAndDismiss {
                        Interstitial.show(activity, admobModel.interstitialId, callback)
                    }
                } else {
                    Interstitial.show(activity, admobModel.interstitialId, callback)
                }
            } else callback?.invoke()
        }


        private var clickCount = 0
        fun showInterstitialRandom(activity: Activity, callback: (() -> Unit)? = null) {
            admobModel.interstitialInterval?.let { interval ->
                clickCount++
                if (clickCount == interval) {
                    showInterstitial(activity, callback)
                    clickCount = 0
                } else callback?.invoke()
            }
        }

        private fun loadReward(activity: Activity) {
            if (admobModel.rewardId.isNotEmpty()) {
                Reward.load(activity, admobModel.rewardId)
            }
        }

        fun showReward(activity: Activity, callback: (() -> Unit)? = null) {
            if (admobModel.rewardId.isNotEmpty()) {
                if(admobModel.withLoading){
                    dialogLoading.showAndDismiss {
                        Reward.show(activity, admobModel.rewardId, callback)
                    }
                } else {
                    Reward.show(activity, admobModel.rewardId, callback)
                }
            } else callback?.invoke()
        }


        fun loadNative(activity: Activity, nativeView: NativeView) {
            nativeView.visibility = View.GONE
            if (admobModel.nativeId.isNotEmpty()) {
                Native.getNative(activity, admobModel.nativeId, object : NativeListener {
                    override fun onLoaded(nativeAd: NativeAd) {
                        nativeView.setNativeAd(nativeAd)
                        nativeView.visibility = View.VISIBLE
                    }

                    override fun onFailed() {
                        nativeView.visibility = View.GONE
                    }
                })
            }

        }

        fun showBanner(activity: Activity, adsContainer: ViewGroup) {
            if (admobModel.bannerId.isNotEmpty()) {
                Banner.show(activity, adsContainer, admobModel.bannerId)
            }
        }
    }


    class Builder(private val activity: Activity) {
        private var application: Application? = null
        private val admobModel = AdmobModel()
        private val blockedActivity: ArrayList<Activity> = arrayListOf()
        private var loadingTime = 0L
        fun enableLogging(enableLogging: Boolean) = apply { admobModel.enableLogging = enableLogging }
        fun debugMode(debugMode: Boolean) = apply { admobModel.debugMode = debugMode }
        fun interstitialId(interstitialId: String) = apply { admobModel.interstitialId = interstitialId }
        fun interstitialInterval(interstitialInterval: Int) = apply { admobModel.interstitialInterval = interstitialInterval }
        fun bannerId(bannerId: String) = apply { admobModel.bannerId = bannerId }
        fun rewardId(rewardId: String) = apply { admobModel.rewardId = rewardId }
        fun nativeId(nativeId: String) = apply { admobModel.nativeId = nativeId }
        fun withLoading(withLoading: Boolean, loadingTime: Long?) = apply {
            admobModel.withLoading = withLoading
            this.loadingTime = loadingTime ?: 3000
        }

        fun customLadingView(@LayoutRes customLadingView: Int) = apply { admobModel.customLadingView = customLadingView }
        fun withOpenAd(application: Application, openId: String, blockedActivity: ArrayList<Activity>? = null) = apply {
            admobModel.openId = openId
            this.application = application
            blockedActivity?.let { this.blockedActivity.addAll(blockedActivity) }
        }

        fun request() = build(activity, admobModel, loadingTime, application, blockedActivity)
    }


}