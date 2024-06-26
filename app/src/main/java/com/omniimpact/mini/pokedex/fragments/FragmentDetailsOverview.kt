package com.omniimpact.mini.pokedex.fragments

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsOverviewBinding
import com.omniimpact.mini.pokedex.databinding.ListItemPokemonEvolutionBinding
import com.omniimpact.mini.pokedex.databinding.ListItemStatBinding
import com.omniimpact.mini.pokedex.fragments.FragmentDetails.Companion.KEY_COMBINED_POKEDEX
import com.omniimpact.mini.pokedex.fragments.FragmentDetails.Companion.KEY_POKEMON_ENTRY_NUMBER
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChain
import com.omniimpact.mini.pokedex.models.ModelPokemonSpecies
import com.omniimpact.mini.pokedex.models.PokedexPokemonEntry
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetPokedex
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonDetails
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonEvolutions
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonSpecies
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue
import com.omniimpact.mini.pokedex.utilities.UtilityApplicationSettings
import com.omniimpact.mini.pokedex.utilities.UtilityNavigationCoordinator
import com.squareup.picasso.Picasso

class FragmentDetailsOverview : Fragment, IOnApiLoadQueue {

	@Suppress("ConvertSecondaryConstructorToPrimary")
	constructor(): super()

	//region Variables

	private lateinit var mFragmentViewBinding: FragmentDetailsOverviewBinding

	private var mPokemonEntryNumber: Int = -1
	private var mCombinedPokedexName: String = String()

	private var mPokemonSpecies: ModelPokemonSpecies = ModelPokemonSpecies()
	private var mSourceItem: PokedexPokemonEntry = PokedexPokemonEntry()
	private var mPokemonId: Int = -1

	private var mPokemonEvolutionChain: ModelPokemonEvolutionChain = ModelPokemonEvolutionChain()

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
		mFragmentViewBinding = FragmentDetailsOverviewBinding.inflate(layoutInflater)
		return mFragmentViewBinding.root
	}

	override fun onResume() {
		UtilityLoader.registerApiCallListener(this)
		UtilityLoader.addRequests(
			mapOf(
				ApiGetPokemonDetails() to mSourceItem.pokemonSpecies.name,
				ApiGetPokemonSpecies() to mSourceItem.pokemonSpecies.name
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
			is ApiGetPokemonDetails -> {
				addDetails()
			}
			is ApiGetPokemonSpecies -> {
				mPokemonSpecies = ApiGetPokemonSpecies.getPokemonSpecies(mSourceItem.pokemonSpecies.name)
				val flavorText = ApiGetPokemonSpecies.getPokemonFlavorText(
					mSourceItem.pokemonSpecies.name,
					UtilityApplicationSettings.getString(
						requireContext(),
						UtilityApplicationSettings.KEY_STRING_SELECTED_VERSION,
						String()
					)
				)
				mFragmentViewBinding.idTvFlavor.text = flavorText
				val evolutionChainId = ApiGetPokedex.getPokemonIdFromUrl(mPokemonSpecies.evolutionChain.url).toString()
				UtilityLoader.addRequests(
					mapOf(
						ApiGetPokemonEvolutions() to evolutionChainId
					), requireContext()
				)
			}

			is ApiGetPokemonEvolutions -> {
				val pokemonEvolutionChainId = ApiGetPokedex.getPokemonIdFromUrl(mPokemonSpecies.evolutionChain.url)
				mPokemonEvolutionChain =
					ApiGetPokemonEvolutions.getPokemonEvolutionChain(pokemonEvolutionChainId)
				if (mFragmentViewBinding.idLlEvolutions.childCount > 0) return
				addEvolutionView(mPokemonEvolutionChain)
			}

		}


	}

	override fun onFailed(failure: IApi) {}

	private fun addDetails(){

		val details = ApiGetPokemonDetails.getPokemonDetails(mSourceItem.pokemonSpecies.name)

		if(mFragmentViewBinding.idLlStats.childCount == 0) {
			details.stats.forEach {
				val newStat = ListItemStatBinding.inflate(
					layoutInflater,
					mFragmentViewBinding.idLlStats,
					true
				)
				newStat.idTvStat.text = it.stat.name
				newStat.idPbStat.progress = it.baseStat
			}
		}

	}


	private fun addEvolutionView(evolution: ModelPokemonEvolutionChain) {
		val initialCheck = ApiGetPokedex.getPokemonEntryFromName(mCombinedPokedexName, evolution.species.name)
		if(initialCheck.entryNumber > 0) {
			val evolutionView = ListItemPokemonEvolutionBinding.inflate(
				layoutInflater,
				mFragmentViewBinding.idLlEvolutions,
				true
			)
			if (evolution.evolutionDetails.isNotEmpty()) {
				evolution.evolutionDetails[0].also {
					evolutionView.idMinLevel.text = it.minLevel?.toString() ?: "?"
				}
			} else {
				evolutionView.idMinLevel.text = "⟡"
			}
			evolutionView.idTvPokemonName.text =
				evolution.species.name.replaceFirstChar { it.titlecase() }
			val evolutionSpeciesId: Int = ApiGetPokedex.getPokemonIdFromUrl(evolution.species.url)
			val evolutionIconUrl: String = ApiGetPokedex.getImageUrlFromPokemonId(evolutionSpeciesId)
			Picasso.get().load(evolutionIconUrl).fit().into(evolutionView.idIvIcon)
			if(evolutionSpeciesId != mPokemonSpecies.id) {
				evolutionView.root.setOnClickListener {
					val speciesEntry =
						ApiGetPokedex.getPokemonEntryFromName(
							mCombinedPokedexName,
							evolution.species.name
						)
					val detailsFragment = FragmentDetails()
					val argumentsBundle = Bundle()
					argumentsBundle.putInt(KEY_POKEMON_ENTRY_NUMBER, speciesEntry.entryNumber)
					argumentsBundle.putString(KEY_COMBINED_POKEDEX, mCombinedPokedexName)
					UtilityNavigationCoordinator.requestNavigation(detailsFragment, argumentsBundle)
				}
			} else {
				evolutionView.root.setBackgroundColor(Color.TRANSPARENT)
				evolutionView.root.strokeWidth = 0
				evolutionView.idIvClickable.visibility = View.INVISIBLE
			}
		}
		if (evolution.evolvesTo.isNotEmpty()) {
			for(nextEvolution in evolution.evolvesTo){
				val checkExists = ApiGetPokedex.getPokemonEntryFromName(mCombinedPokedexName, nextEvolution.species.name)
				if(checkExists.entryNumber > 0) addEvolutionView(nextEvolution)
			}
		}
	}


}