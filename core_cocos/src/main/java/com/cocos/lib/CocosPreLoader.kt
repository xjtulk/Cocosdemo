package com.cocos.lib

import android.graphics.SurfaceTexture
import android.os.Build
import android.view.Surface
import android.view.SurfaceHolder

object CocosPreLoader {

    const val TAG = "CocosPreLoader"

    private var surfaceView: CocosSurfaceView? = null

    private val surfaceHolderCallback = object: SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            CocosLogWrapper.log(TAG, "surfaceCreated")
            CocosNativeInterface.onSurfaceCreatedNative(holder.surface)
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            CocosLogWrapper.log(TAG, "surfaceChanged, format:$format, width:$width, height:$height")
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            CocosLogWrapper.log(TAG, "surfaceDestroyed")
        }
    }

    private var preLoaded = false

    private val surfaceTexture by lazy {
        SurfaceTexture(0)
    }

    private val preLoadSurface by lazy {
        Surface(surfaceTexture)
    }

    fun preload() {
        if (preLoaded) {
            return
        }
        CocosLogWrapper.log("preload called")
        preLoaded = true
        CocosSoLoader.ensureLoadCocosNativeLibrary()
        CocosNativeInterface.onCreateNative(
            ApplicationWrapper.getInstance(),
            ApplicationWrapper.getInstance().assets,
            ApplicationWrapper.getInstance().obbDir?.absolutePath,
            Build.VERSION.SDK_INT
        )
        reCreateSurfaceView()
        CocosNativeInterface.onSurfaceCreatedNative(preLoadSurface)
    }

    private fun reCreateSurfaceView() : CocosSurfaceView {
        surfaceView?.holder?.removeCallback(surfaceHolderCallback)
        val local = CocosSurfaceView(ApplicationWrapper.getInstance()).apply {
            holder?.addCallback(surfaceHolderCallback)
        }
        surfaceView = local
        return local
    }

    fun fetchCocosSurfaceView() : CocosSurfaceView {
        return surfaceView ?: reCreateSurfaceView()
    }

    fun onGameActivityFinish() {
        CocosLogWrapper.log(TAG, "onGameActivityDestroy")
        CocosNativeInterface.onSurfaceCreatedNative(preLoadSurface)
        reCreateSurfaceView()
        CocosNativeInterface.restartEngine()
    }

}