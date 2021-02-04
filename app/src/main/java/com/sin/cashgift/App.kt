package com.sin.cashgift

import android.content.Context
import androidx.multidex.MultiDexApplication

class App : MultiDexApplication(){
    companion object{
        lateinit var mContext: Context
    }
    override fun onCreate() {
        super.onCreate()
        mContext = this
    }
}