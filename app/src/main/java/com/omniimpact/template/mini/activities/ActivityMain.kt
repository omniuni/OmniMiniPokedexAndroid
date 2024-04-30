package com.omniimpact.template.mini.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.omniimpact.template.mini.databinding.ActivityMainBinding

class ActivityMain: AppCompatActivity() {

    private lateinit var mActivityViewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mActivityViewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mActivityViewBinding.root)
    }

    override fun onStart() {
        super.onStart()
        mActivityViewBinding.idBtnExit.setOnClickListener {
            finish()
        }
    }

}