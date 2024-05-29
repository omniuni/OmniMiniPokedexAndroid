package com.omniimpact.mini.pokedex.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsBinding
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsOverviewBinding
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonDetails
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonSpecies
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue

class FragmentDetailsOverview : Fragment(), IOnApiLoadQueue {


	//region Variables

	private lateinit var mFragmentViewBinding: FragmentDetailsOverviewBinding

	//endregion

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mFragmentViewBinding = FragmentDetailsOverviewBinding.inflate(layoutInflater)
		UtilityLoader.registerApiCallListener(this)
		return mFragmentViewBinding.root
	}

	override fun onResume() {
		UtilityLoader.registerApiCallListener(this)
		super.onResume()
	}

	override fun onPause() {
		UtilityLoader.deregisterApiCallListener(this)
		super.onPause()
	}

	override fun onComplete() {}

	override fun onSuccess(success: IApi) {}

	override fun onFailed(failure: IApi) {}


}