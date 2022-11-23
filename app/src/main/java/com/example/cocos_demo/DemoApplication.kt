package com.example.cocos_demo

import android.app.Application
import android.content.Context
import android.content.Intent
import com.cocos.lib.ApplicationWrapper

class DemoApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        ApplicationWrapper.getInstance().attachBaseContext(base)
        ApplicationWrapper.getInstance().startService(Intent().apply {
            setClass(this@DemoApplication, CocosPreloadService::class.java)
        })
    }

}