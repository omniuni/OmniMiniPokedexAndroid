package com.omniimpact.mini.pokedex.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsMatchupsBinding
import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.omniimpact.mini.pokedex.models.ModelPokemonDetailsType
import com.omniimpact.mini.pokedex.models.PokedexPokemonEntry
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetPokedex
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonDetails
import com.omniimpact.mini.pokedex.network.api.ApiGetType
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue
import com.omniimpact.mini.pokedex.utilities.view.UtilityDetailsView

class FragmentDetailsMatchups : Fragment(), IOnApiLoadQueue {

	//region Variables

	private lateinit var mFragmentViewBinding: FragmentDetailsMatchupsBinding

	private var mPokemonEntryNumber: Int = -1
	private var mCombinedPokedexName: String = String()

	private var mSourceItem: PokedexPokemonEntry = PokedexPokemonEntry()
	private var mPokemonId: Int = -1
	private var mTypesLoaded: Boolean = false

	//endregion

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.also {
			if (it.keySet().contains(FragmentDetails.KEY_POKEMON_ENTRY_NUMBER) && it.keySet()
					.contains(FragmentDetails.KEY_COMBINED_POKEDEX)
			) {
				mPokemonEntryNumber = it.getInt(FragmentDetails.KEY_POKEMON_ENTRY_NUMBER)
				mCombinedPokedexName = it.getString(FragmentDetails.KEY_COMBINED_POKEDEX, String())
			}
		}
		mSourceItem = ApiGetPokedex.getPokedexPokemonEntry(mCombinedPokedexName, mPokemonEntryNumber)
		mPokemonId = ApiGetPokedex.getPokemonIdFromUrl(mSourceItem.pokemonSpecies.url)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mFragmentViewBinding = FragmentDetailsMatchupsBinding.inflate(layoutInflater)
		return mFragmentViewBinding.root
	}

	override fun onResume() {
		super.onResume()
		UtilityLoader.registerApiCallListener(this)
		UtilityLoader.addRequests(
			mapOf(
				ApiGetPokemonDetails() to mSourceItem.pokemonSpecies.name,
			), requireContext()
		)
	}

	override fun onPause() {
		UtilityLoader.deregisterApiCallListener(this)
		super.onPause()
	}

	override fun onComplete() {
		if(mTypesLoaded) updateTypeDetails(ApiGetPokemonDetails.getPokemonDetails(mSourceItem.pokemonSpecies.name))
	}

	override fun onSuccess(success: IApi) {

		when(success){
			is ApiGetPokemonDetails -> {
				val details = ApiGetPokemonDetails.getPokemonDetails(mSourceItem.pokemonSpecies.name)
				details.types.forEach { typeSlot ->
					val typeName: String = typeSlot.type.name
					UtilityLoader.addRequests(
						mapOf(
							ApiGetType() to typeName
						), requireContext()
					)
				}
			}
			is ApiGetType -> {
				mTypesLoaded = true
			}
		}


	}

	override fun onFailed(failure: IApi) {}

	private val mTypeMap: MutableMap<Int, ArrayList<ModelPokemonDetailsType>> = mutableMapOf()

	private fun updateTypeDetails(details: ModelPokemonDetails) {

		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idRvStrengthMajor,
			R.string.label_double_damage,
			R.drawable.ic_dmg_up,
			mTypeMap
		)
		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idRvDefenseMajor,
			R.string.label_full_defense,
			R.drawable.ic_def_full,
			mTypeMap
		)
		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idRvDefenseHigh,
			R.string.label_double_defense,
			R.drawable.ic_def_high,
			mTypeMap
		)

		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idRvDamageMinor,
			R.string.label_half_damage,
			R.drawable.ic_dmg_down,
			mTypeMap
		)
		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idRvDamageNone,
			R.string.label_no_damage,
			R.drawable.ic_dmg_none,
			mTypeMap
		)
		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idRvWeaknessMajor,
			R.string.label_half_defense,
			R.drawable.ic_dmg_double,
			mTypeMap
		)

	}


}