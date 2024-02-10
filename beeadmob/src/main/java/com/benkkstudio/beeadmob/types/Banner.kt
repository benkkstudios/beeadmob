package com.benkkstudio.beeadmob.types

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.benkkstudio.beeadmob.BeeAdRequest
import com.benkkstudio.beeadmob.BeeAdmob
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError


internal object Banner {
    fun show(activity: Activity, view: ViewGroup, bannerId: String) {
        view.visibility = View.GONE
        val adView = AdView(activity)
        adView.adUnitId = bannerId
        adView.setAdSize(adSize(activity, view))
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() { // Code to be executed when an ad finishes loading.
                view.addView(adView, 0)
                view.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(adError: LoadAdError) { // Code to be executed when an ad request fails.
                BeeAdmob.logging("Admob Banner : " + adError.message)
                BeeAdmob.logging("Admob Banner : " + adError.code)
            }

            override fun onAdOpened() { // Code to be executed when an ad opens an overlay that
            }

            override fun onAdClicked() { // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() { // Code to be executed when the user is about to return
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