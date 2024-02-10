package com.benkkstudio.beeadmobexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.benkkstudio.beeadmob.BeeAdmob
import com.benkkstudio.beeadmob.natives.NativeView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val nativeView = findViewById<NativeView>(R.id.nativeView)
        val bannerContainer = findViewById<LinearLayoutCompat>(R.id.bannerContainer)
        val buttonInter = findViewById<AppCompatButton>(R.id.buttonInter)
        val buttonReward = findViewById<AppCompatButton>(R.id.buttonReward)
        val blockedActivity = arrayListOf<AppCompatActivity>()
        BeeAdmob.Builder(this)
            .bannerId(BeeAdmob.DummyAdmob.BANNER)
            .interstitialId(BeeAdmob.DummyAdmob.INTERSTITIAL)
            .rewardId(BeeAdmob.DummyAdmob.REWARD)
            .nativeId(BeeAdmob.DummyAdmob.NATIVE)
            .openId(BeeAdmob.DummyAdmob.OPEN)
            .setApplication(App.instance())
            .setBlockedActivity(blockedActivity)
            .withLoading(3000)
            .enableLogging(true)
            .debugMode(true)
            .request {
                BeeAdmob.showNative(nativeView)
                BeeAdmob.showBanner(this, bannerContainer)
                buttonInter.setOnClickListener {
                    BeeAdmob.showInterstitial(this) {
                            //inter closed or failed
                    }
                }

                buttonReward.setOnClickListener {
                    BeeAdmob.showReward(this) {
                            //reward closed or failed
                    }
                }
//                BeeAdmob.loadOpenAd(App.instance(), BeeAdmob.DummyAdmob.OPEN, onFinish = {
//                    BeeAdmob.showNative(nativeView)
//                    BeeAdmob.showBanner(this, bannerContainer)
//                    buttonInter.setOnClickListener {
//                        BeeAdmob.showInterstitial(this) {
//                            //inter closed or failed
//                        }
//                    }
//
//                    buttonReward.setOnClickListener {
//                        BeeAdmob.showReward(this) {
//                            //reward closed or failed
//                        }
//                    }
//                })
            }
    }

}