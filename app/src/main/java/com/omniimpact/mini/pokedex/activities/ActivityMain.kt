package com.omniimpact.mini.pokedex.activities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.omniimpact.mini.pokedex.databinding.ActivityMainBinding
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadProgress

class ActivityMain : AppCompatActivity(), IOnApiLoadProgress {

	private lateinit var mActivityViewBinding: ActivityMainBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		mActivityViewBinding = ActivityMainBinding.inflate(layoutInflater)
		onComplete()
		UtilityLoader.registerProgressListener(this)
		ViewCompat.setOnApplyWindowInsetsListener(
			mActivityViewBinding.root
		) { _, windowInsets -> // _ = input view
			updateViewForInsets(windowInsets)
			WindowInsetsCompat.CONSUMED
		}
		setContentView(mActivityViewBinding.root)
		setSupportActionBar(mActivityViewBinding.idToolbar)
	}

	override fun onStop() {
		super.onStop()
		UtilityLoader.deregisterProgressListener(this)

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

	override fun onApiProgress(apiCallName: String, batchTotal: Int, batchComplete: Int) {
		mActivityViewBinding.idIncludeNetworking.idTvNetworkStatus.text = apiCallName
		mActivityViewBinding.idIncludeNetworking.idFlNetworkStatusContainer.visibility = View.VISIBLE
	}

	override fun onComplete() {
		mActivityViewBinding.idIncludeNetworking.idFlNetworkStatusContainer.visibility = View.GONE
	}


}