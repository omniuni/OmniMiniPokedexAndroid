package com.omniimpact.mini.pokedex.activities

import android.os.Bundle
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.omniimpact.mini.pokedex.databinding.ActivityMainBinding
import com.omniimpact.mini.pokedex.network.UtilityLoader

class ActivityMain : AppCompatActivity() {

	private lateinit var mActivityViewBinding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		mActivityViewBinding = ActivityMainBinding.inflate(layoutInflater)
		UtilityLoader.attachOutputUi(
			mActivityViewBinding.idIncludeNetworking.idFlNetworkStatusContainer,
			mActivityViewBinding.idIncludeNetworking.idTvNetworkStatus
		)
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
		mActivityViewBinding.idToolbar.updatePadding(
			right = insetValues.right,
			left = insetValues.left
		)
		mActivityViewBinding.idFcvActivityMain.updateLayoutParams<MarginLayoutParams> {
			rightMargin = insetValues.right
			leftMargin = insetValues.left
		}
		mActivityViewBinding.idFlNavigation.minimumHeight = insetValues.bottom
	}

}