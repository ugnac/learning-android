package com.learn.common

import android.os.Build

object Config {
    const val AGE_1DAY: Long = 86400000

    const val AGE_1HOUR = 3600000

    const val AGE_1WEEK: Long = 604800000

    const val AGE_2MIN = 120000

    const val AGE_3DAY: Long = 259200000

    const val AGE_3SEC = 3000

    const val AGE_DEFAULT: Long = 120000

    const val MAX_RETRIES_DEFAULT = 1

    const val TIMEOUT_DEFAULT = 3000

    val SYSTEM_HTTP_UA = ("Dalvik/1.6.0 (Linux; U; Android "
            + Build.VERSION.RELEASE + "; "
            + Build.MODEL + " Build/"
            + Build.ID + ")")

    var isDebuggable = false
}