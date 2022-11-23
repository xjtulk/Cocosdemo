package com.example.cocos_demo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.cocos.lib.CocosPreLoader

class CocosPreloadService : Service() {

    override fun onCreate() {
        super.onCreate()
        CocosPreLoader.preload()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}