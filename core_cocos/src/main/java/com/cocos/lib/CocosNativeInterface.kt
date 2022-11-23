package com.cocos.lib

import android.content.Context
import android.content.res.AssetManager
import android.view.Surface

object CocosNativeInterface {

    external fun restartEngine()

    external fun onSurfaceCreatedNative(surface: Surface)
    external fun onSurfaceDestroyNative()

    external fun onStopNative()
    external fun onStartNative()

    external fun onLowMemoryNative()

    external fun onCreateNative(
        context: Context,
        resourceManager: AssetManager,
        obbPath: String?,
        sdkVersion: Int
    )

}