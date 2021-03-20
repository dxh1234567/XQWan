package com.jj.xqwan

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import cn.jj.base.baseclass.BaseApplication


/**
 *  Created By duXiaHui
 *  on 2021/1/30
 */
class UGirlApplication : BaseApplication() {
    companion object {
        lateinit var instance: UGirlApplication
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}