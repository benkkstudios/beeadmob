package com.benkkstudio.beeadmob

import android.app.Activity
import android.app.Application
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.benkkstudio.beeadmob.model.AdmobModel
import com.benkkstudio.beeadmob.natives.NativeView
import com.benkkstudio.beeadmob.types.Banner
import com.benkkstudio.beeadmob.types.Interstitial
import com.benkkstudio.beeadmob.types.Native
import com.benkkstudio.beeadmob.types.NativeListener
import com.benkkstudio.beeadmob.types.AppOpenManager
import com.benkkstudio.beeadmob.types.Reward
import com.benkkstudio.beeadmob.widget.DialogLoading
import com.benkkstudio.beeconsent.BeeConsent
import com.benkkstudio.beeconsent.BeeConsentCallback

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd

@Suppress("unused")
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
        private var blockedActivity: ArrayList<AppCompatActivity> = arrayListOf()
        private var onFinish: (() -> Unit)? = null
        internal fun build(
            activity: Activity, admobModel: AdmobModel, loadingTime: Int, application: Application?, blockedActivity: ArrayList<AppCompatActivity> =
                arrayListOf(), onFinish: (() -> Unit)? = null
        ) {
            this.admobModel = admobModel
            this.application = application
            this.blockedActivity = blockedActivity
            this.onFinish = onFinish
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
                            loadOpenAd {
                                loadNative(activity){
                                    loadInterstitial(activity){
                                        loadReward(activity, onFinish)
                                    }
                                }
                            }
                        }
                    }
                })
                .request()
        }

        fun loadOpenAd(application: Application, openId: String, blockedActivity: ArrayList<AppCompatActivity>? = arrayListOf(), onFinish: (() -> Unit)? =
            null) {
            if(admobModel.openId.isNotEmpty()){
                AppOpenManager.init(application, openId, blockedActivity, onFinish)
            }
        }

        private fun loadOpenAd(onFinish: () -> Unit) {
            if(admobModel.openId.isNotEmpty()){
                if(application == null){
                    throw Exception("please call setApplication(application: Application) in BeeAdmob.Builder")
                }
                application?.let {
                    AppOpenManager.Builder(it)
                        .setOpenId(admobModel.openId)
                        .setBlockedActivity(blockedActivity)
                        .setOnFinish(onFinish)
                        .build()
                }
            } else {
                onFinish.invoke()
            }
        }

        private fun loadInterstitial(activity: Activity, onFinish: () -> Unit) {
            if (admobModel.interstitialId.isNotEmpty()) {
                Interstitial.load(activity, admobModel.interstitialId, onFinish)
            } else {
                onFinish.invoke()
            }
        }

        fun showInterstitial(activity: Activity, callback: (() -> Unit)? = null) {
            if (admobModel.interstitialId.isNotEmpty()) {
                if(admobModel.loadingTime != 0){
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

        private fun loadReward(activity: Activity, onFinish: (() -> Unit)? = null) {
            if (admobModel.rewardId.isNotEmpty()) {
                Reward.load(activity, admobModel.rewardId, onFinish)
            } else {
                onFinish?.invoke()
            }
        }

        fun showReward(activity: Activity, callback: (() -> Unit)? = null) {
            if (admobModel.rewardId.isNotEmpty()) {
                if(admobModel.loadingTime != 0){
                    dialogLoading.showAndDismiss {
                        Reward.show(activity, admobModel.rewardId, callback)
                    }
                } else {
                    Reward.show(activity, admobModel.rewardId, callback)
                }
            } else callback?.invoke()
        }

        private fun loadNative(activity: Activity, onFinish: () -> Unit) {
            if(admobModel.nativeId.isNotEmpty()){
                Native.load(activity, admobModel.nativeId, object : NativeListener {
                    override fun onLoaded(nativeAd: NativeAd) {
                        onFinish.invoke()
                    }

                    override fun onFailed() {
                        onFinish.invoke()
                    }

                })
            } else {
                onFinish.invoke()
            }
        }

        fun showNative(nativeView: NativeView) {
            nativeView.visibility = View.GONE
            Native.getNative {
                nativeView.setNativeAd(it)
                nativeView.visibility = View.VISIBLE
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
        private var blockedActivity: ArrayList<AppCompatActivity> = arrayListOf()
        private var loadingTime = 0
        fun enableLogging(enableLogging: Boolean) = apply { admobModel.enableLogging = enableLogging }
        fun debugMode(debugMode: Boolean) = apply { admobModel.debugMode = debugMode }
        fun interstitialId(interstitialId: String) = apply { admobModel.interstitialId = interstitialId }
        fun interstitialInterval(interstitialInterval: Int) = apply { admobModel.interstitialInterval = interstitialInterval }
        fun bannerId(bannerId: String) = apply { admobModel.bannerId = bannerId }
        fun rewardId(rewardId: String) = apply { admobModel.rewardId = rewardId }
        fun nativeId(nativeId: String) = apply { admobModel.nativeId = nativeId }
        fun openId(openId: String) = apply { admobModel.openId = openId }
        fun setApplication(application: Application) = apply { this.application = application }
        fun setBlockedActivity(blockedActivity: ArrayList<AppCompatActivity>) = apply { this.blockedActivity = blockedActivity }
        fun withLoading(loadingTime: Int) = apply {
            admobModel.loadingTime = loadingTime
        }
        fun customLadingView(@LayoutRes customLadingView: Int) = apply { admobModel.customLadingView = customLadingView }
        fun request(onFinish: (() -> Unit)? = null) = build(activity, admobModel, loadingTime, application, blockedActivity, onFinish)

    }
}