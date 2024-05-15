package com.omniimpact.mini.pokedex.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentInitBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeGenerationGroupBinding
import com.omniimpact.mini.pokedex.models.Generation
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetGeneration
import com.omniimpact.mini.pokedex.network.api.ApiGetGenerations
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue
import com.omniimpact.mini.pokedex.utilities.UtilityApplicationSettings
import com.omniimpact.mini.pokedex.utilities.UtilityFragmentManager

class FragmentInit : Fragment(), IOnApiLoadQueue {

	//region Local Variables

	private lateinit var mFragmentViewBinding: FragmentInitBinding
	private lateinit var mFragmentTarget: ViewGroup

	private val mBindingMapGenerations: MutableMap<String, ListItemTypeGenerationGroupBinding> = mutableMapOf()

	//endregion

	//region Fragment Lifecycle

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
		mFragmentTarget = view.parent as ViewGroup
		if(UtilityApplicationSettings.selectedVersion.isNotEmpty()){
			loadToPokemonList()
			return
		}
		UtilityLoader.registerApiCallListener(this)
		Log.d(FragmentInit::class.simpleName, "Enqueue Version Groups and Versions...")
		UtilityLoader.addRequests(mapOf(
			ApiGetGenerations() to String(),
			//ApiGetVersionGroups to String(),
			//ApiGetVersions to String(),
			//ApiGetPokedexList to String()
		), requireContext())
	}

	override fun onPause() {
		UtilityLoader.deregisterApiCallListener(this)
		super.onPause()
	}

	//endregion

	//region IOnApiLoadQueue
	override fun onSuccess(success: IApi) {
		when(success){
			is ApiGetGenerations -> {
				for(generation in ApiGetGenerations.getGenerations()){
					Log.d(FragmentInit::class.simpleName, "Enqueue Generation ${generation.name}...")
					UtilityLoader.addRequests(mapOf(
						ApiGetGeneration() to generation.name
					), requireContext())
				}
			}
//			is ApiGetVersionGroups -> {
//				for (i in 1..ApiGetVersionGroups.getVersionGroups().size) {
//					Log.d(FragmentInit::class.simpleName, "Enqueue Version Group $i...")
//					UtilityLoader.addRequests(
//						mapOf(
//							ApiGetVersionGroup to i.toString()
//						), requireContext()
//					)
//				}
//			}
//			is ApiGetVersions -> {
//				for(i in 1..ApiGetVersions.getNumberOfVersions()){
//					Log.d(FragmentInit::class.simpleName, "Enqueue Version $i...")
//					UtilityLoader.addRequests(mapOf(
//						ApiGetVersion to i.toString()
//					), requireContext())
//				}
//			}
//			is ApiGetVersionGroup -> {
//				Log.d(FragmentInit::class.simpleName, "Got: ${success.getFriendlyName()}...")
//			}
//			is ApiGetVersion -> {
//				Log.d(FragmentInit::class.simpleName, "Got: ${success.getFriendlyName()}...")
//			}
			else -> {

			}
		}
	}

	override fun onFailed(failure: IApi) {
		Log.d(FragmentInit::class.simpleName, "API Call Failed: ${failure.getUrl()}")
	}

	override fun onComplete() {
		mFragmentViewBinding.idTvTitle.text = getString(R.string.select)

		generateGenerationGroupViews()
		//generateGameVersionGroupViews()


	}

	//endregion

	//region Utility
	private fun loadToPokemonList(){
		UtilityFragmentManager.using(parentFragmentManager).load(FragmentHome())
			.into(mFragmentTarget).now(addToBackStack = false)
	}

	//endregion

	//region UI

	private fun generateGenerationGroupViews(){

		val generations: List<Generation> = ApiGetGenerations.getGenerations()

		for (basicGeneration in generations){
			val generation = ApiGetGeneration.getGenerationByName(basicGeneration.name)
			val generationBinding: ListItemTypeGenerationGroupBinding =
				ListItemTypeGenerationGroupBinding.inflate(layoutInflater, mFragmentViewBinding.idLlVersions, true)

			mBindingMapGenerations[generation.name] = generationBinding

			generationBinding.idTvTitle.text = ApiGetGeneration.getGenerationNameInEnglish(generation)

		}

	}
/*
	private fun generateGameVersionGroupViews(){

		mBindingMapGenerations.forEach{ (generationKey, binding) ->

			var numGames = 0
			val generation = ApiGetGeneration.getGenerationByName(generationKey)
			generation.versionGroups.forEach { generationVersionGroup ->
				val versionGroup = ApiGetVersionGroup.getVersionGroupByName(generationVersionGroup.name)
				val versionNameStringsList: ArrayList<String> = arrayListOf()
				versionGroup.versions.forEach { versionGroupVersion ->
					val versionName = versionGroupVersion.name
					versionNameStringsList.add(ApiGetVersion.getVersionByName(versionName).nameEn)
				}
				val namesAsString = versionNameStringsList.joinToString(" ◆ ")
				if(namesAsString.isNotEmpty()){
					val versionGroupBinding =
						ListItemTypeVersionGroupBinding.inflate(layoutInflater, binding.idLlVersionGroups, true)
					versionGroupBinding.idTvTitle.text = namesAsString
					versionGroupBinding.root.setOnClickListener {
						UtilityApplicationSettings.selectedVersion = versionGroup.name
						loadToPokemonList()
					}
					numGames++
				}
			}
			binding.idTvTitleDetail.text =
				getString(R.string.games_species, numGames)

		}

	}*/

	//endregion

}