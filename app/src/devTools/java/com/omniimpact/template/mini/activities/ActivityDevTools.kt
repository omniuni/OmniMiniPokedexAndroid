package com.omniimpact.template.mini.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.omniimpact.template.mini.databinding.ActivityMainDevBinding

class ActivityDevTools: AppCompatActivity() {

    private lateinit var mActivityMainDevBinding: ActivityMainDevBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mActivityMainDevBinding = ActivityMainDevBinding.inflate(layoutInflater)
        setContentView(mActivityMainDevBinding.root)
    }

}