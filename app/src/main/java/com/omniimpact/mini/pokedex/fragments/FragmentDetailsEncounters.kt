package com.omniimpact.mini.pokedex.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsRoutesBinding
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
import com.omniimpact.mini.pokedex.network.api.ApiGetPokedex
import com.omniimpact.mini.pokedex.network.api.ApiGetVersion
import com.omniimpact.mini.pokedex.network.api.ApiGetVersionGroup
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue
import com.omniimpact.mini.pokedex.utilities.UtilityApplicationSettings

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

	override fun onComplete() {}

	override fun onSuccess(success: IApi) {
		when(success){
			is ApiGetEncounters -> {
				mVersionGroup.versions.forEach {
					mEncountersByVersion[it.name] = ApiGetEncounters.getSimplifiedEncountersForPokemon(mSourceItem.pokemonSpecies.name, it.name)
				}
				updateUi()
				getEncounterLocations()
				getEncounterMethods()
			}
		}
	}

	override fun onFailed(failure: IApi) {}

	private fun getEncounterLocations(){

	}

	private fun getEncounterMethods(){

	}

	private fun updateUi(){
		Log.d(FragmentDetailsEncounters::class.simpleName, "Found encounters in ${mEncountersByVersion.size} versions.")
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
			switch.idTvVersion.text = "$versionDisplayName (${listOfEncounters.size})"
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
		mFragmentViewBinding.idLlQuickTest.removeAllViews()
		mEncountersByVersion[mSelectedVersion.name]?.forEach { areaDetailsPair ->
			val tv = TextView(requireContext())
			var text = "In ${mSelectedVersion.name}, found at ${areaDetailsPair.first.name}.\n"
			areaDetailsPair.second.encounterDetails.distinct().forEach {
				text+="Find by \"${it.method.name}\", ${it.chance}% chance, between levels ${it.minLevel} and ${it.maxLevel}.\n"
			}
			tv.text = text
			tv.updatePadding(bottom = 20)
			mFragmentViewBinding.idLlQuickTest.addView(tv)
		}
	}


}