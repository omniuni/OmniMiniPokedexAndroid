package com.omniimpact.template.mini.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.omniimpact.template.mini.databinding.ActivityMainBinding

class ActivityMain: AppCompatActivity() {

    private lateinit var mActivityViewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mActivityViewBinding = ActivityMainBinding.inflate(layoutInflater)
        ViewCompat.setOnApplyWindowInsetsListener(mActivityViewBinding.root
        ) { _, windowInsets -> // _ = input view
            updateViewForInsets(windowInsets)
            WindowInsetsCompat.CONSUMED
        }
        setContentView(mActivityViewBinding.root)
    }

    private fun updateViewForInsets(windowInsets: WindowInsetsCompat){
        val insetValues = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        mActivityViewBinding.idAbl.updatePadding(top = insetValues.top)
        mActivityViewBinding.idFlNavigation.minimumHeight = insetValues.bottom
    }

}