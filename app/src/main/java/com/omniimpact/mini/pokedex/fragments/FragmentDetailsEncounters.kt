package com.omniimpact.mini.pokedex.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsRoutesBinding
import com.omniimpact.mini.pokedex.databinding.ListItemEncounterBinding
import com.omniimpact.mini.pokedex.databinding.ListItemEncounterLocationBinding
import com.omniimpact.mini.pokedex.databinding.ListItemVersionSwitchBinding
import com.omniimpact.mini.pokedex.fragments.FragmentDetails.Companion.KEY_COMBINED_POKEDEX
import com.omniimpact.mini.pokedex.fragments.FragmentDetails.Companion.KEY_POKEMON_ENTRY_NUMBER
import com.omniimpact.mini.pokedex.models.ModelEncounterLocationArea
import com.omniimpact.mini.pokedex.models.ModelEncounterVersionDetail
import com.omniimpact.mini.pokedex.models.ModelVersion
import com.omniimpact.mini.pokedex.models.ModelVersionGroup
import com.omniimpact.mini.pokedex.models.PokedexPokemonEntry
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetEncounters
import com.omniimpact.mini.pokedex.network.api.ApiGetLocationArea
import com.omniimpact.mini.pokedex.network.api.ApiGetPokedex
import com.omniimpact.mini.pokedex.network.api.ApiGetVersion
import com.omniimpact.mini.pokedex.network.api.ApiGetVersionGroup
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue
import com.omniimpact.mini.pokedex.utilities.UtilityApplicationSettings
import java.util.Locale

class FragmentDetailsEncounters : Fragment, IOnApiLoadQueue {

	@Suppress("ConvertSecondaryConstructorToPrimary")
	constructor(): super()

	//region Variables

	private lateinit var mFragmentViewBinding: FragmentDetailsRoutesBinding

	private var mPokemonEntryNumber: Int = -1
	private var mCombinedPokedexName: String = String()

	private var mSourceItem: PokedexPokemonEntry = PokedexPokemonEntry()
	private var mPokemonId: Int = -1
	private var mVersionGroup: ModelVersionGroup = ModelVersionGroup()

	private var mEncountersByVersion: MutableMap<String, List<Pair<ModelEncounterLocationArea, ModelEncounterVersionDetail>>> = mutableMapOf()

	private var mGotLocations = false
	// private var mGotMethods = false

	//endregion

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.also {
			if (it.keySet().contains(KEY_POKEMON_ENTRY_NUMBER) && it.keySet()
					.contains(KEY_COMBINED_POKEDEX)
			) {
				mPokemonEntryNumber = it.getInt(KEY_POKEMON_ENTRY_NUMBER)
				mCombinedPokedexName = it.getString(KEY_COMBINED_POKEDEX, String())
			}
		}
		mSourceItem = ApiGetPokedex.getPokedexPokemonEntry(mCombinedPokedexName, mPokemonEntryNumber)
		mPokemonId = ApiGetPokedex.getPokemonIdFromUrl(mSourceItem.pokemonSpecies.url)
		mVersionGroup =
			ApiGetVersionGroup.getVersionGroupByName(
				UtilityApplicationSettings.getString(
					requireContext(),
					UtilityApplicationSettings.KEY_STRING_SELECTED_VERSION,
					String()
				)
			)
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mFragmentViewBinding = FragmentDetailsRoutesBinding.inflate(layoutInflater)
		mFragmentViewBinding.idCvHeader.visibility = View.GONE
		return mFragmentViewBinding.root
	}

	override fun onResume() {
		UtilityLoader.registerApiCallListener(this)
		UtilityLoader.addRequests(
			mapOf(
				ApiGetEncounters() to mSourceItem.pokemonSpecies.name,
			), requireContext()
		)
		super.onResume()
	}

	override fun onPause() {
		UtilityLoader.deregisterApiCallListener(this)
		super.onPause()
	}

	override fun onComplete() {
		if(mGotLocations) {
			updateUi()
		}
	}

	override fun onSuccess(success: IApi) {
		when(success){
			is ApiGetEncounters -> {
				mVersionGroup.versions.forEach {
					mEncountersByVersion[it.name] = ApiGetEncounters.getSimplifiedEncountersForPokemon(mSourceItem.pokemonSpecies.name, it.name)
				}
				getEncounterLocations()
				getEncounterMethods()
			}
			is ApiGetLocationArea -> {
				mGotLocations = true
			}
		}
	}

	override fun onFailed(failure: IApi) {}

	private fun getEncounterLocations(){
		var hasEncounters = false
		mEncountersByVersion.forEach { listOfEncounters ->
			listOfEncounters.value.forEach { pairOfEncounters ->
				val locationName = pairOfEncounters.first.name
				UtilityLoader.addRequests(
					mapOf(
						ApiGetLocationArea() to locationName,
					), requireContext()
				)
				hasEncounters = true
			}
		}
		if(!hasEncounters){
			mFragmentViewBinding.idCvLoading.visibility = View.GONE
			mFragmentViewBinding.idCvNoResults.visibility = View.VISIBLE
		}
	}

	private fun getEncounterMethods(){
	}

	private fun updateUi(){
		Log.d(FragmentDetailsEncounters::class.simpleName, "Found encounters in ${mEncountersByVersion.size} versions.")
		mFragmentViewBinding.idCvLoading.visibility = View.GONE
		mFragmentViewBinding.idCvHeader.visibility = View.VISIBLE
		setUpSwitches()
		loadEncounters()
	}

	private val mSwitchMap: MutableMap<String, SwitchMaterial> = mutableMapOf()
	private var mSelectedVersion: ModelVersion = ModelVersion()
	private fun setUpSwitches(){
		if(mFragmentViewBinding.idLlSwitches.childCount > 0) return
		mEncountersByVersion.forEach { (versionName, listOfEncounters) ->
			val switch = ListItemVersionSwitchBinding.inflate(layoutInflater, mFragmentViewBinding.idLlSwitches, true)
			mSwitchMap[versionName] = switch.idSwVersion
			val version = ApiGetVersion.getVersionByName(versionName)
			val versionDisplayName = ApiGetVersion.getVersionNameInEnglish(version)
			switch.idTvVersion.text = String.format(Locale.getDefault(), "%s (%d)", versionDisplayName, listOfEncounters.size)
			switch.idSwVersion.setOnCheckedChangeListener { _, isChecked ->
				if(isChecked){
					mSelectedVersion = version
				}
				updateSwitches()
			}
		}
		mSwitchMap.entries.first().value.isChecked = true
	}

	private fun updateSwitches(){
		for (mutableEntry in mSwitchMap) {
			mutableEntry.value.isChecked = mutableEntry.key ==  mSelectedVersion.name
		}
		loadEncounters()
	}

	private fun loadEncounters(){

		mFragmentViewBinding.idLlEncounterLocations.removeAllViews()
		mEncountersByVersion[mSelectedVersion.name]?.forEach { areaDetailsPair ->

			val locationName = ApiGetLocationArea.getEnglishNameByKey(areaDetailsPair.first.name)

			val newEncounterLocation = ListItemEncounterLocationBinding.inflate(
				layoutInflater,
				mFragmentViewBinding.idLlEncounterLocations,
				true
			)
			newEncounterLocation.idTvLocation.text = locationName

			var lastMethod = String()
			var totalPercent = 0
			areaDetailsPair.second.encounterDetails.forEach { encounter ->
				totalPercent+=encounter.chance
			}
			areaDetailsPair.second.encounterDetails.forEach { encounter ->

				val levelText = if(encounter.minLevel != encounter.maxLevel){
					"Levels ${encounter.minLevel} - ${encounter.maxLevel}"
				} else {
					"Level ${encounter.minLevel}"
				}


				val newEncounter = ListItemEncounterBinding.inflate(
					layoutInflater,
					newEncounterLocation.idLlLocations,
					true
				)
				newEncounter.idTvLevels.text = levelText
				if(encounter.method.name != lastMethod) {
					newEncounter.idTvMethod.text = encounter.method.name
					lastMethod = encounter.method.name
				} else {
					newEncounter.idTvMethod.text = String.format(Locale.getDefault(), "\"")
				}
				newEncounter.idTvPercent.text = String.format(Locale.getDefault(), "%d%%", encounter.chance)
				newEncounter.idPbPercent.progress = encounter.chance
				newEncounter.idPbPercent.max = totalPercent

			}

		}


	}


}