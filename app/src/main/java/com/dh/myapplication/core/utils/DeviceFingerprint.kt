package com.dh.myapplication.core.utils
import android.content.Context
import android.os.Build

fun getFingerprint(context: Context): String {
    val fingerprint = StringBuilder()
   fingerprint.append("FINGERPRINT: ${Build.FINGERPRINT}\n")
    return fingerprint.toString()
}
