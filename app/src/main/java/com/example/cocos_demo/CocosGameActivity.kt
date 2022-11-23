package com.example.cocos_demo

import android.media.AudioManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import com.cocos.lib.*

class CocosGameActivity : FragmentActivity() {

    private var orientationHelper: CocosOrientationHelper? = null
    private var keyCodeHandler: CocosKeyCodeHandler? = null
    private var sensorHandler: CocosSensorHandler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null && createdAlready) {
            clearOnFinish()
        }
        createdAlready = true
        val gameResPath = intent.getStringExtra(Constants.LAUNCH_INTENT_EXTRA_KEY_GAME_RES_PATH)
        if (!gameResPath.isNullOrEmpty()) {
            CocosHelper.updateResPath(gameResPath)
        }
        CocosPreLoader.preload()
        GlobalObject.setActivity(this)
        CocosHelper.init(this)
        CanvasRenderingContext2DImpl.init(this)
        this.volumeControlStream = AudioManager.STREAM_MUSIC
        initView()
        CocosHelper.setKeepScreenOn(true)
        keyCodeHandler = CocosKeyCodeHandler()
        sensorHandler = CocosSensorHandler(this)
        orientationHelper = CocosOrientationHelper(this)
        initFragment()
    }

    private fun initView() {
        val frameLayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        FrameLayout(this).apply {
            layoutParams = frameLayoutParams
            this@CocosGameActivity.setContentView(this)
            addView(CocosPreLoader.fetchCocosSurfaceView())
            val fragmentContainerView = FragmentContainerView(context).apply {
                id = R.id.game_extend_container
            }
            val fragmentContainerViewLp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            addView(fragmentContainerView, fragmentContainerViewLp)
        }
    }

    override fun finish() {
        super.finish()
        clearOnFinish()
    }

    private fun clearOnFinish() {
        JsbBridgeWrapper.removeAllListeners()
        CanvasRenderingContext2DImpl.destroy()
        CocosPreLoader.onGameActivityFinish()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction().replace(R.id.game_extend_container, CocosGameFragment()).commit()
    }

    override fun onPause() {
        super.onPause()
        sensorHandler?.onPause()
        orientationHelper?.onPause()
    }

    override fun onResume() {
        super.onResume()
        sensorHandler?.onResume()
        orientationHelper?.onResume()
    }

    override fun onStop() {
        super.onStop()
        CocosNativeInterface.onStopNative()
        CocosPreLoader.fetchCocosSurfaceView().visibility = View.INVISIBLE
    }

    override fun onStart() {
        super.onStart()
        CocosNativeInterface.onStartNative()
        CocosPreLoader.fetchCocosSurfaceView().visibility = View.VISIBLE
    }

    override fun onLowMemory() {
        super.onLowMemory()
        if (!isFinishing) {
            CocosNativeInterface.onLowMemoryNative()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return (keyCodeHandler?.onKeyDown(keyCode, event) ?: false) || super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        return (keyCodeHandler?.onKeyUp(keyCode, event) ?: false) || super.onKeyUp(keyCode, event)
    }

    companion object {

        private var createdAlready = false

    }

}