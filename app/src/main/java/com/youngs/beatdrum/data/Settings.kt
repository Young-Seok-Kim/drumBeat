package com.youngs.beatdrum.data

import android.content.Context
import android.content.SharedPreferences

/**
 * 마지막으로 쓴 값들을 기억한다. 앱을 다시 켜도 60 BPM·3초로 돌아가지 않게.
 * 박자표는 메트로놈과 초견 화면이 같은 값을 쓰므로 여기서 한 키로 공유한다.
 */
object Settings {
    private const val PREFS = "beatdrum_settings"
    const val KEY_BPM = "bpm"
    const val KEY_NOTE_POS = "note_pos"            // 0=4분, 1=8분, 2=16분
    const val KEY_TIME_SIG_POS = "time_sig_pos"    // 0=2/4, 1=3/4, 2=4/4
    const val KEY_SIGHTREAD_SECONDS = "sightread_seconds"

    const val DEFAULT_BPM = 60
    const val DEFAULT_TIME_SIG_POS = 2
    const val DEFAULT_SIGHTREAD_SECONDS = 3

    fun prefs(context: Context): SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** 박자표 스피너 위치 → 한 마디 박자 수 */
    fun beatsPerMeasure(timeSigPos: Int): Int = when (timeSigPos) {
        0 -> 2
        1 -> 3
        else -> 4
    }

    /** 음표 스피너 위치 → 4분음표 기준 배수 */
    fun noteMultiplier(notePos: Int): Int = when (notePos) {
        1 -> 2
        2 -> 4
        else -> 1
    }
}
