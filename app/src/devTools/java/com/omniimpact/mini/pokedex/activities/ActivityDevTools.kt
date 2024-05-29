package com.omniimpact.mini.pokedex.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.omniimpact.mini.pokedex.databinding.ActivityMainDevBinding

class ActivityDevTools : AppCompatActivity() {

	private lateinit var mActivityMainDevBinding: ActivityMainDevBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		mActivityMainDevBinding = ActivityMainDevBinding.inflate(layoutInflater)
		setContentView(mActivityMainDevBinding.root)
	}

}