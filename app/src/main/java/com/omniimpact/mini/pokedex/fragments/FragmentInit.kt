package com.omniimpact.mini.pokedex.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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
import com.omniimpact.mini.pokedex.utilities.UtilityApplicationSettings
import com.omniimpact.mini.pokedex.utilities.UtilityFragmentManager

class FragmentInit : Fragment(), IOnApiLoadQueue {

	private lateinit var mFragmentViewBinding: FragmentInitBinding

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		if(UtilityApplicationSettings.selectedVersion.isNotEmpty()){
			return View(requireContext())
		}
		mFragmentViewBinding = FragmentInitBinding.inflate(layoutInflater)
		return mFragmentViewBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		if(UtilityApplicationSettings.selectedVersion.isNotEmpty()){
			UtilityFragmentManager.using(parentFragmentManager).load(FragmentHome())
				.into(view.parent as ViewGroup).now(addToBackStack = false)
			return
		}
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

		val versionGroups = ApiGetVersionGroups.getVersionGroups()

		for(versionGroup in versionGroups){

			val tvGroupHeader = TextView(requireContext())
			tvGroupHeader.text = versionGroup.name

			mFragmentViewBinding.idLlVersions.addView(tvGroupHeader)

			val versionsInGroup = ApiGetVersionGroup.getVersionGroupByName(versionGroup.name)

			for(version in versionsInGroup.versions){

				val tvGroupEntry = TextView(requireContext())
				tvGroupEntry.text = "--"+version.name+", "+ApiGetVersion.getVersionByName(version.name).nameEn

				mFragmentViewBinding.idLlVersions.addView(tvGroupEntry)

			}


		}

	}

	override fun onSuccess(success: IApi) {
		when(success){
			is ApiGetVersionGroups -> {
				for(i in 1..ApiGetVersionGroups.getVersionGroups().size){
					Log.d(FragmentInit::class.simpleName, "Enqueue Version Group $i...")
					UtilityLoader.enqueue(mapOf(
						ApiGetVersionGroup to i.toString()
					))
				}
			}
			is ApiGetVersions -> {
				for(i in 1..ApiGetVersions.getNumberOfVersions()){
					Log.d(FragmentInit::class.simpleName, "Enqueue Version $i...")
					UtilityLoader.enqueue(mapOf(
						ApiGetVersion to i.toString()
					))
				}
			}
			is ApiGetVersionGroup -> {
				Log.d(FragmentInit::class.simpleName, "Got: ${success.getFriendlyName()}...")
			}
			is ApiGetVersion -> {
				Log.d(FragmentInit::class.simpleName, "Got: ${success.getFriendlyName()}...")
			}
			else -> {

			}
		}
	}

	override fun onFailed(failure: IApi) {

	}

}