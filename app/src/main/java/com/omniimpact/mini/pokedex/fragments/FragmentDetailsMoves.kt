package com.omniimpact.mini.pokedex.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsMovesBinding
import com.omniimpact.mini.pokedex.fragments.FragmentDetails.Companion.KEY_COMBINED_POKEDEX
import com.omniimpact.mini.pokedex.fragments.FragmentDetails.Companion.KEY_POKEMON_ENTRY_NUMBER
import com.omniimpact.mini.pokedex.models.PokedexPokemonEntry
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetPokedex
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue

class FragmentDetailsMoves : Fragment, IOnApiLoadQueue {

	@Suppress("ConvertSecondaryConstructorToPrimary")
	constructor(): super()

	//region Variables

	private lateinit var mFragmentViewBinding: FragmentDetailsMovesBinding

	private var mPokemonEntryNumber: Int = -1
	private var mCombinedPokedexName: String = String()

	private var mSourceItem: PokedexPokemonEntry = PokedexPokemonEntry()
	private var mPokemonId: Int = -1

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
	}

	@SuppressLint("ClickableViewAccessibility")
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mFragmentViewBinding = FragmentDetailsMovesBinding.inflate(layoutInflater)
		return mFragmentViewBinding.root
	}

	override fun onResume() {
		UtilityLoader.registerApiCallListener(this)
		/*
		UtilityLoader.addRequests(
			mapOf(
				ApiGetPokemonDetails() to mSourceItem.pokemonSpecies.name,
				ApiGetPokemonSpecies() to mSourceItem.pokemonSpecies.name
			), requireContext()
		)
		*/
		super.onResume()
	}

	override fun onPause() {
		UtilityLoader.deregisterApiCallListener(this)
		super.onPause()
	}

	override fun onComplete() {}

	override fun onSuccess(success: IApi) {
		//when(success){}
	}

	override fun onFailed(failure: IApi) {}


}