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
import com.omniimpact.mini.pokedex.databinding.ListItemTypeVersionGroupBinding
import com.omniimpact.mini.pokedex.models.Generation
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetGeneration
import com.omniimpact.mini.pokedex.network.api.ApiGetGenerations
import com.omniimpact.mini.pokedex.network.api.ApiGetPokedex
import com.omniimpact.mini.pokedex.network.api.ApiGetPokedexList
import com.omniimpact.mini.pokedex.network.api.ApiGetVersion
import com.omniimpact.mini.pokedex.network.api.ApiGetVersionGroup
import com.omniimpact.mini.pokedex.network.api.ApiGetVersionGroups
import com.omniimpact.mini.pokedex.network.api.ApiGetVersions
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue
import com.omniimpact.mini.pokedex.utilities.UtilityApplicationSettings
import com.omniimpact.mini.pokedex.utilities.UtilityFragmentManager

class FragmentInit : Fragment(), IOnApiLoadQueue {

	//region Local Variables

	private lateinit var mFragmentViewBinding: FragmentInitBinding
	private lateinit var mFragmentTarget: ViewGroup

	private val mBindingMapGenerations: MutableMap<String, ListItemTypeGenerationGroupBinding> =
		mutableMapOf()

	//endregion

	//region Fragment Lifecycle

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		if (UtilityApplicationSettings.selectedVersionGroup.isNotEmpty()) {
			return View(requireContext())
		}
		mFragmentViewBinding = FragmentInitBinding.inflate(layoutInflater)
		return mFragmentViewBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		mFragmentTarget = view.parent as ViewGroup
		if (UtilityApplicationSettings.selectedVersionGroup.isNotEmpty()) {
			loadToPokemonList()
			return
		}
		UtilityLoader.registerApiCallListener(this)
		Log.d(FragmentInit::class.simpleName, "Enqueue Version Groups and Versions...")
		UtilityLoader.addRequests(
			mapOf(
				ApiGetGenerations() to String(),
				ApiGetVersionGroups() to String(),
				ApiGetVersions() to String(),
				ApiGetPokedexList() to String(),
			), requireContext()
		)
	}

	override fun onPause() {
		UtilityLoader.deregisterApiCallListener(this)
		super.onPause()
	}

	//endregion

	//region IOnApiLoadQueue
	override fun onSuccess(success: IApi) {
		when (success) {
			is ApiGetGenerations -> {
				for (generation in ApiGetGenerations.getGenerations()) {
					Log.d(FragmentInit::class.simpleName, "Enqueue Generation ${generation.name}...")
					UtilityLoader.addRequests(
						mapOf(
							ApiGetGeneration() to generation.name
						), requireContext()
					)
				}
			}

			is ApiGetVersionGroups -> {
				for (versionGroup in ApiGetVersionGroups.getVersionGroups()) {
					Log.d(FragmentInit::class.simpleName, "Enqueue Version Group ${versionGroup.name}...")
					UtilityLoader.addRequests(
						mapOf(
							ApiGetVersionGroup() to versionGroup.name
						), requireContext()
					)
				}
			}

			is ApiGetVersions -> {
				for (versionResult in ApiGetVersions.getVersions()) {
					Log.d(FragmentInit::class.simpleName, "Enqueue Version ${versionResult.name}...")
					UtilityLoader.addRequests(
						mapOf(
							ApiGetVersion() to versionResult.name
						), requireContext()
					)
				}
			}

			is ApiGetPokedexList -> {
				for (pokedex in ApiGetPokedexList.getPokedexList()) {
					Log.d(FragmentInit::class.simpleName, "Enqueue Pokedex ${pokedex.name}...")
					UtilityLoader.addRequests(
						mapOf(
							ApiGetPokedex() to pokedex.name
						), requireContext()
					)
				}
			}

			is ApiGetVersionGroup,
			is ApiGetVersion,
			is ApiGetPokedex -> {
				Log.d(FragmentInit::class.simpleName, "Got: ${success.getFriendlyName()}...")
			}

			else -> {

			}
		}
	}

	override fun onFailed(failure: IApi) {
		Log.d(FragmentInit::class.simpleName, "API Call Failed: ${failure.getBaseUrl()}")
	}

	override fun onComplete() {
		mFragmentViewBinding.idTvTitle.text = getString(R.string.select)

		generateGenerationGroupViews()
		generateGameVersionGroupViews()


	}

	//endregion

	//region Utility

	private fun loadToPokemonList() {
		UtilityFragmentManager.using(parentFragmentManager).load(FragmentHome())
			.into(mFragmentTarget).now(addToBackStack = false)
	}

	//endregion

	//region UI

	private fun generateGenerationGroupViews() {

		val generations: List<Generation> = ApiGetGenerations.getGenerations()

		for (basicGeneration in generations) {
			val generation = ApiGetGeneration.getGenerationByName(basicGeneration.name)
			val generationBinding: ListItemTypeGenerationGroupBinding =
				ListItemTypeGenerationGroupBinding.inflate(
					layoutInflater,
					mFragmentViewBinding.idLlVersions,
					true
				)

			mBindingMapGenerations[generation.name] = generationBinding

			generationBinding.idTvTitle.text = ApiGetGeneration.getGenerationNameInEnglish(generation)

		}

	}

	private fun generateGameVersionGroupViews() {

		mBindingMapGenerations.forEach { (generationKey, binding) ->

			var numGames = 0
			val generation = ApiGetGeneration.getGenerationByName(generationKey)
			generation.versionGroups.forEach { generationVersionGroup ->
				var numSpecies = 0
				val versionGroup = ApiGetVersionGroup.getVersionGroupByName(generationVersionGroup.name)
				val versionNameStringsList: ArrayList<String> = arrayListOf()
				versionGroup.versions.forEach { versionGroupVersion ->
					val versionName = versionGroupVersion.name
					versionNameStringsList.add(
						ApiGetVersion.getVersionNameInEnglish(
							ApiGetVersion.getVersionByName(versionName)
						)
					)
					numSpecies = ApiGetPokedex.getPokemonInPokedexList(versionGroup.pokedexes).size
				}
				val namesAsString = versionNameStringsList.joinToString(" ◆ ")
				if (namesAsString.isNotEmpty() && numSpecies > 0) {
					val versionGroupBinding =
						ListItemTypeVersionGroupBinding.inflate(layoutInflater, binding.idLlVersionGroups, true)
					versionGroupBinding.idTvTitle.text = namesAsString
					versionGroupBinding.idTvTitleDetail.text = numSpecies.toString()
					versionGroupBinding.root.setOnClickListener {
						UtilityApplicationSettings.selectedVersionGroup = versionGroup.name
						loadToPokemonList()
					}
					numGames++
				}
			}
			binding.idTvTitleDetail.text =
				getString(R.string.games_species, numGames)

		}

	}

	//endregion

}