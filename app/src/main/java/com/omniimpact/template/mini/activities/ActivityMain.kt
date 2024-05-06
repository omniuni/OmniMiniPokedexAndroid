package com.omniimpact.template.mini.activities

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.omniimpact.template.mini.databinding.ActivityMainBinding

class ActivityMain : AppCompatActivity() {

	private lateinit var mActivityViewBinding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		mActivityViewBinding = ActivityMainBinding.inflate(layoutInflater)
		ViewCompat.setOnApplyWindowInsetsListener(
			mActivityViewBinding.root
		) { _, windowInsets -> // _ = input view
			updateViewForInsets(windowInsets)
			WindowInsetsCompat.CONSUMED
		}
		setContentView(mActivityViewBinding.root)
		setSupportActionBar(mActivityViewBinding.idToolbar)
	}

	private fun updateViewForInsets(windowInsets: WindowInsetsCompat) {
		val insetValues = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
		mActivityViewBinding.idFlStatusBar.minimumHeight = insetValues.top
		mActivityViewBinding.idFlNavigation.minimumHeight = insetValues.bottom
		mActivityViewBinding.idFcvActivityMain.updateLayoutParams<MarginLayoutParams> {
			rightMargin = insetValues.right
		}
	}

}