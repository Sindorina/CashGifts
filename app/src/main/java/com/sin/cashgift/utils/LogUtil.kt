package com.sin.cashgift.utils

import android.util.Log

object LogUtil {
    private const val VERBOSE = 1
    private const val DEBUG = 2
    private const val INFO = 3
    private const val WARN = 4
    private const val ERROR = 5
    private const val RELEASE = 6
    private const val CONTROL = VERBOSE //测试使用 VERBOSE 正式使用 RELEASE

    fun logV(TAG: String?, result: String) {
        if (CONTROL <= VERBOSE) {
            Log.v(TAG, result)
        }
    }

    fun logI(TAG: String?, result: String) {
        if (CONTROL <= VERBOSE) {
            Log.i(TAG, result)
        }
    }

    fun logD(TAG: String?, result: String) {
        if (CONTROL <= VERBOSE) {
            Log.d(TAG, result)
        }
    }

    fun logW(TAG: String?, result: String) {
        if (CONTROL <= VERBOSE) {
            Log.w(TAG, result)
        }
    }

    fun logE(TAG: String?, result: String) {
        if (CONTROL <= VERBOSE) {
            Log.e(TAG, result)
        }
    }
}