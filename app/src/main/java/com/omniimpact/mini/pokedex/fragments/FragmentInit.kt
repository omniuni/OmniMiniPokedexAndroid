package com.omniimpact.mini.pokedex.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentInitBinding
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetVersion
import com.omniimpact.mini.pokedex.network.api.ApiGetVersionGroup
import com.omniimpact.mini.pokedex.network.api.ApiGetVersionGroups
import com.omniimpact.mini.pokedex.network.api.ApiGetVersions
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue

class FragmentInit : Fragment(), IOnApiLoadQueue {

	private lateinit var mFragmentViewBinding: FragmentInitBinding

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mFragmentViewBinding = FragmentInitBinding.inflate(layoutInflater)
		return mFragmentViewBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		UtilityLoader.registerListener(this)
		Log.d(FragmentInit::class.simpleName, "Enqueue Version Groups and Versions...")
		UtilityLoader.enqueue(mapOf(
			ApiGetVersionGroups to String(),
			ApiGetVersions to String()
		))
	}

	override fun onPause() {
		UtilityLoader.removeListener(this)
		super.onPause()
	}

	override fun onComplete() {
		mFragmentViewBinding.idTvTitle.text = getString(R.string.select)
	}

	override fun onSuccess(success: IApi) {
		when(success){
			is ApiGetVersionGroups -> {
				for(i in 1..ApiGetVersionGroups.getNumberOfVersionGroups()){
					Log.d(FragmentInit::class.simpleName, "Enqueue Version Group $i...")
					UtilityLoader.enqueue(mapOf(
						ApiGetVersionGroup to i.toString()
					))
				}
			}
			is ApiGetVersions -> {
				for(i in 1..ApiGetVersions.getNumberOfVersions()){
					UtilityLoader.enqueue(mapOf(
						ApiGetVersion to i.toString()
					))
				}
			}
			is ApiGetVersionGroup -> {
				Log.d(FragmentInit::class.simpleName, "Got: ${success.getFriendlyName()}...")
			}
			else -> {

			}
		}
	}

	override fun onFailed(failure: IApi) {

	}

}