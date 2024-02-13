package com.benkkstudio.beeadmob.types

import android.annotation.SuppressLint
import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.benkkstudio.beeadmob.BeeAdRequest
import com.benkkstudio.beeadmob.interfaces.BeeAdmobListener
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@SuppressLint("MissingPermission")
internal object Banner {
    private var beeAdmobListener: BeeAdmobListener? = null
    fun setListener(beeAdmobListener: BeeAdmobListener? = null) {
        this.beeAdmobListener = beeAdmobListener
    }

    fun show(activity: Activity, view: ViewGroup, bannerId: String) {
        view.visibility = View.GONE
        val adView = AdView(activity)
        adView.adUnitId = bannerId
        adView.setAdSize(adSize(activity, view))
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                view.addView(adView, 0)
                view.visibility = View.VISIBLE
                beeAdmobListener?.bannerListener?.loaded()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                beeAdmobListener?.bannerListener?.failLoad(adError)
            }
        }
        adView.loadAd(BeeAdRequest.build(activity))
    }

    @Suppress("DEPRECATION")
    private fun adSize(activity: Activity, view: ViewGroup): AdSize {
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val density = outMetrics.density
        var adWidthPixels = view.width.toFloat()
        if (adWidthPixels == 0f) {
            adWidthPixels = outMetrics.widthPixels.toFloat()
        }
        val adWidth = (adWidthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }
}