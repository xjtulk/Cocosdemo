package com.example.cocos_demo

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import androidx.fragment.app.FragmentActivity
import com.example.cocos_demo.databinding.LayoutMainActivityBinding

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("cocos-demo", "MainActivity onCreate")
        val binding = LayoutMainActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.btnEnterGame.setOnClickListener {
            startActivity(Intent().apply {
                setClass(this@MainActivity, CocosGameActivity::class.java)
            })
        }
    }
}