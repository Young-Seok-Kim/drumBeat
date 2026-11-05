package com.youngs.beatdrum

import android.app.Application
import com.google.android.gms.ads.MobileAds
import kotlin.concurrent.thread

class BeatDrumApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // 광고 SDK 초기화는 1~2초 걸린다. 화면이 뜬 뒤 프래그먼트에서 시작하면 그만큼 배너가 늦는다.
        // 앱이 뜨자마자 백그라운드에서 시작해 두면 MainActivity 가 loadAd 할 때쯤 끝나 있다.
        // (메인 스레드에서 부르면 첫 화면이 그만큼 늦게 뜨므로 스레드를 따로 쓴다)
        thread(name = "ads-init") {
            MobileAds.initialize(this@BeatDrumApp)
        }
    }
}
