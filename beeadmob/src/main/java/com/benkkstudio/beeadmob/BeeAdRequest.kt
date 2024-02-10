package com.benkkstudio.beeadmob

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.benkkstudio.beeconsent.BeeConsent
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest


internal object BeeAdRequest : AdRequest.Builder() {
    @Suppress("DEPRECATION")
    fun build(activity: Activity): AdRequest {
        if (BeeConsent.isConsent(activity)) {
            val extras = Bundle()
            extras.putString("npa", "1")
            addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        }
        return build()
    }

    @Suppress("DEPRECATION")
    fun build(context: Context): AdRequest {
        if (BeeConsent.isConsent(context)) {
            val extras = Bundle()
            extras.putString("npa", "1")
            addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        }
        return build()
    }

    @Deprecated(
        "please use BeeAdRequest.build(context: Context)",
        ReplaceWith("BeeAdRequest.build(context)", "com.google.android.gms.ads.AdRequest.Builder")
    )
    override fun build(): AdRequest {
        return super.build()
    }
}