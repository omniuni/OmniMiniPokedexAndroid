package com.omniimpact.mini.pokedex

import android.app.Application
import com.omniimpact.mini.pokedex.network.UtilityLoader

class ApplicationBase : Application() {

	override fun onCreate() {
		super.onCreate()
		UtilityLoader.updateContext(this)
	}
}