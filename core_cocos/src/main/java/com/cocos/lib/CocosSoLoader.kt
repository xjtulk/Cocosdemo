package com.cocos.lib

object CocosSoLoader {

    private var soLoaded = false

    fun ensureLoadCocosNativeLibrary() {
        if (soLoaded) {
            return
        }
        kotlin.runCatching {
            System.loadLibrary("cocos")
            soLoaded = true
        }.exceptionOrNull()?.let {
            CocosLogWrapper.log(CocosPreLoader.TAG, "loadCocosNativeLibrary fail, ${it.message}")
        }
    }

}