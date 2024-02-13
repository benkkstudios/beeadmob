package com.benkkstudio.beeadmob

import android.app.Activity
import android.app.Application
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.benkkstudio.beeadmob.interfaces.BeeAdmobListener
import com.benkkstudio.beeadmob.model.Config
import com.benkkstudio.beeadmob.natives.NativeView
import com.benkkstudio.beeadmob.types.AppOpenManager
import com.benkkstudio.beeadmob.types.Banner
import com.benkkstudio.beeadmob.types.BeeOpenOptions
import com.benkkstudio.beeadmob.types.Interstitial
import com.benkkstudio.beeadmob.types.Native
import com.benkkstudio.beeadmob.types.Reward
import com.benkkstudio.beeadmob.widget.DialogLoading
import com.benkkstudio.beeconsent.BeeConsent
import com.benkkstudio.beeconsent.BeeConsentCallback
import com.google.android.gms.ads.MobileAds


@Suppress("unused")
class BeeAdmob {
    companion object {
        private lateinit var dialogLoading: DialogLoading
        private lateinit var beeAdmobAdsUnit: BeeAdmobAdsUnit
        private lateinit var config: Config
        private var beeAdmobListener: BeeAdmobListener? = null
        private var beeOpenOptions: BeeOpenOptions.Builder? = null
        internal fun build(
            activity: Activity,
            beeAdmobListener: BeeAdmobListener? = null,
            beeAdmobAdsUnit: BeeAdmobAdsUnit? = null,
            config: Config,
            beeOpenOptions: BeeOpenOptions.Builder?
        ) {
            this.config = config
            this.beeAdmobListener = beeAdmobListener
            this.beeOpenOptions = beeOpenOptions
            Banner.setListener(beeAdmobListener)
            Interstitial.setListener(beeAdmobListener)
            Reward.setListener(beeAdmobListener)
            Native.setListener(beeAdmobListener)
            if (beeAdmobAdsUnit != null) {
                this.beeAdmobAdsUnit = beeAdmobAdsUnit
                dialogLoading = DialogLoading(activity, config.loadingTime, config.customLadingView)
                requestConsent(activity)
            }
        }


        private fun requestConsent(activity: Activity) {
            BeeConsent.Builder(activity)
                .debugMode(config.debugMode)
                .enableLogging(config.debugMode)
                .listener(object : BeeConsentCallback {
                    override fun onRequested() {
                        MobileAds.initialize(activity) {
                            loadOpenAd()
                            loadInterstitial(activity)
                            loadReward(activity)
                            loadNative(activity) {
                                beeAdmobListener?.initListener?.onInit()
                            }
                        }
                    }
                })
                .request()
        }

        fun loadOpenAd(
            application: Application, openId: String, blockedActivity: ArrayList<AppCompatActivity>? = arrayListOf(), onFinish: (() -> Unit)? =
                null
        ) {
            if (beeAdmobAdsUnit.openId.isNotEmpty()) {
                AppOpenManager.init(application, openId, blockedActivity, onFinish)
            }
        }

        private fun loadOpenAd() {
            if (beeOpenOptions == null || beeAdmobAdsUnit.openId.isBlank()) {
                return
            } else {
                AppOpenManager.Builder(beeOpenOptions!!.application)
                    .setOpenId(beeAdmobAdsUnit.openId)
                    .setBlockedActivity(beeOpenOptions!!.blockedActivity)
                    .setListener(beeAdmobListener)
                    .build()
            }
        }

        private fun loadInterstitial(activity: Activity) {
            if (beeAdmobAdsUnit.interstitialId.isNotEmpty()) {
                Interstitial.load(activity, beeAdmobAdsUnit.interstitialId)
            }
        }

        fun showInterstitial(activity: Activity, callback: (() -> Unit)? = null) {
            if (beeAdmobAdsUnit.interstitialId.isNotEmpty()) {
                if (config.loadingTime != 0) {
                    dialogLoading.showAndDismiss {
                        Interstitial.show(activity, beeAdmobAdsUnit.interstitialId, callback)
                    }
                } else {
                    Interstitial.show(activity, beeAdmobAdsUnit.interstitialId, callback)
                }
            } else callback?.invoke()
        }


        private var clickCount = 0
        fun showInterstitialRandom(activity: Activity, callback: (() -> Unit)? = null) {
            if (beeAdmobAdsUnit.interstitialInterval <= 0) {
                showInterstitial(activity, callback)
            } else {
                clickCount++
                if (clickCount == beeAdmobAdsUnit.interstitialInterval) {
                    showInterstitial(activity, callback)
                    clickCount = 0
                } else callback?.invoke()
            }
        }

        private fun loadReward(activity: Activity) {
            if (beeAdmobAdsUnit.rewardId.isNotEmpty()) {
                Reward.load(activity, beeAdmobAdsUnit.rewardId)
            }
        }

        fun showReward(activity: Activity, callback: (() -> Unit)? = null) {
            if (beeAdmobAdsUnit.rewardId.isNotEmpty()) {
                if (config.loadingTime != 0) {
                    dialogLoading.showAndDismiss {
                        Reward.show(activity, beeAdmobAdsUnit.rewardId, callback)
                    }
                } else {
                    Reward.show(activity, beeAdmobAdsUnit.rewardId, callback)
                }
            } else callback?.invoke()
        }

        private fun loadNative(activity: Activity, onFinish: () -> Unit) {
            if (beeAdmobAdsUnit.nativeId.isNotEmpty()) {
                Native.load(activity, beeAdmobAdsUnit.nativeId) {
                    onFinish.invoke()
                }
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
            if (beeAdmobAdsUnit.bannerId.isNotEmpty()) {
                Banner.show(activity, adsContainer, beeAdmobAdsUnit.bannerId)
            }
        }
    }


    class Builder(private val activity: Activity) {
        private val config = Config()
        private var beeAdmobAdsUnit: BeeAdmobAdsUnit? = null
        private var beeAdmobListener: BeeAdmobListener? = null
        private var beeOpenOptions: BeeOpenOptions.Builder? = null
        fun setAdsUnit(beeAdmobAdsUnit: BeeAdmobAdsUnit) = apply { this.beeAdmobAdsUnit = beeAdmobAdsUnit }
        fun setListener(beeAdmobListener: BeeAdmobListener?) = apply { this.beeAdmobListener = beeAdmobListener }
        fun setOpenAdsOptions(beeOpenOptions: BeeOpenOptions.Builder) = apply { this.beeOpenOptions = beeOpenOptions }
        fun setDebugMode(debugMode: Boolean) = apply { config.debugMode = debugMode }
        fun setLoadingTime(loadingTime: Int) = apply { config.loadingTime = loadingTime }
        fun setLoadingView(@LayoutRes customLadingView: Int) = apply { config.customLadingView = customLadingView }
        fun build() = build(activity, beeAdmobListener, beeAdmobAdsUnit, config, beeOpenOptions)
    }

}