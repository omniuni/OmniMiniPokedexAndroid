package com.omniimpact.mini.pokedex.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsBinding
import com.omniimpact.mini.pokedex.databinding.ListItemPokemonEvolutionBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeChipBinding
import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChain
import com.omniimpact.mini.pokedex.models.ModelPokemonSpecies
import com.omniimpact.mini.pokedex.models.PokedexPokemonEntry
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetPokedex
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonDetails
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonEvolutions
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonSpecies
import com.omniimpact.mini.pokedex.network.api.ApiGetType
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue
import com.omniimpact.mini.pokedex.utilities.UtilityApplicationSettings
import com.omniimpact.mini.pokedex.utilities.UtilityFragmentManager
import com.omniimpact.mini.pokedex.utilities.view.OnPicassoImageLoadedDoEnterTransition
import com.omniimpact.mini.pokedex.utilities.view.UtilityDetailsView
import com.squareup.picasso.Picasso


class FragmentDetails : Fragment(), IOnApiLoadQueue {

	companion object {
		const val KEY_POKEMON_ENTRY_NUMBER = "KEY_POKEMON_ENTRY_NUMBER"
		const val KEY_COMBINED_POKEDEX = "KEY_COMBINED_POKEDEX"
	}

	//region Variables

	private lateinit var mFragmentViewBinding: FragmentDetailsBinding
	private var mPokemonEntryNumber: Int = -1
	private var mCombinedPokedexName: String = String()
	private var mSourceItem: PokedexPokemonEntry = PokedexPokemonEntry()
	private var mPokemonEvolutionChain: ModelPokemonEvolutionChain = ModelPokemonEvolutionChain()
	private var mPokemonSpecies: ModelPokemonSpecies = ModelPokemonSpecies()

	//endregion

	//region Fragment Lifecycle

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
	}


	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			android.R.id.home -> {
				parentFragmentManager.popBackStack()
			}
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mFragmentViewBinding = FragmentDetailsBinding.inflate(layoutInflater)
		updateBaseViews()
		UtilityLoader.registerApiCallListener(this)
		UtilityLoader.addRequests(
			mapOf(
				ApiGetPokemonDetails() to mSourceItem.pokemonSpecies.name,
				ApiGetPokemonSpecies() to mSourceItem.pokemonSpecies.name
			), requireContext()
		)
		return mFragmentViewBinding.root
	}

	private fun updateBaseViews() {
		setHasOptionsMenu(true)
		(requireActivity() as AppCompatActivity).supportActionBar?.also {
			it.setDisplayHomeAsUpEnabled(true)
		}
		val pokemonId = ApiGetPokedex.getPokemonIdFromUrl(mSourceItem.pokemonSpecies.url)
		val imageUrl = ApiGetPokedex.getImageUrlFromPokemonId(pokemonId)
		Picasso.get().load(imageUrl).fit()
			.centerInside()
			.into(
				mFragmentViewBinding.idLlBanner.idIvPokemonIcon,
				OnPicassoImageLoadedDoEnterTransition(requireContext(), mFragmentViewBinding)
			)
	}

	override fun onPause() {
		UtilityLoader.deregisterApiCallListener(this)
		super.onPause()
	}

	//endregion

	//region Interfaces

	override fun onComplete() {
			updateTypeDetails(ApiGetPokemonDetails.getPokemonDetails(mSourceItem.pokemonSpecies.name))
	}

	override fun onSuccess(success: IApi) {
		when (success) {
			is ApiGetPokemonDetails -> {
				val details = ApiGetPokemonDetails.getPokemonDetails(mSourceItem.pokemonSpecies.name)
				updateTypes(details)
				details.types.forEach { typeSlot ->
					val typeName: String = typeSlot.type.name
					UtilityLoader.addRequests(
						mapOf(
							ApiGetType() to typeName
						), requireContext()
					)
				}
			}

			is ApiGetPokemonSpecies -> {
				mPokemonSpecies = ApiGetPokemonSpecies.getPokemonSpecies(mSourceItem.pokemonSpecies.name)
				mFragmentViewBinding.idLlBanner.idTvPokemonName.text = ApiGetPokemonSpecies.getPokemonName(mPokemonSpecies)
				val flavorText = ApiGetPokemonSpecies.getPokemonFlavorText(
					mSourceItem.pokemonSpecies.name,
					UtilityApplicationSettings.getString(
						requireContext(),
						UtilityApplicationSettings.KEY_STRING_SELECTED_VERSION,
						String()
					)
				)
				mFragmentViewBinding.idIncludeDetails.idTvFlavor.text = flavorText
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
				if (mFragmentViewBinding.idIncludeDetails.idLlEvolutions.childCount > 0) return
				addEvolutionView(mPokemonEvolutionChain)
			}

			else -> {

			}
		}

	}

	override fun onFailed(failure: IApi) {
	}

	//endregion

	//region UI

	// Evolution Chain

	private fun addEvolutionView(evolution: ModelPokemonEvolutionChain) {
		val initialCheck = ApiGetPokedex.getPokemonEntryFromName(mCombinedPokedexName, evolution.species.name)
		if(initialCheck.entryNumber > 0) {
			val evolutionView = ListItemPokemonEvolutionBinding.inflate(
				layoutInflater,
				mFragmentViewBinding.idIncludeDetails.idLlEvolutions,
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
			evolutionView.root.setOnClickListener {
				val detailsFragment = FragmentDetails()
				val argumentsBundle = Bundle()
				val speciesEntry =
					ApiGetPokedex.getPokemonEntryFromName(mCombinedPokedexName, evolution.species.name)
				argumentsBundle.putInt(KEY_POKEMON_ENTRY_NUMBER, speciesEntry.entryNumber)
				argumentsBundle.putString(KEY_COMBINED_POKEDEX, mCombinedPokedexName)
				UtilityFragmentManager.using(parentFragmentManager).load(detailsFragment)
					.with(argumentsBundle).into(view?.parent as ViewGroup).now()
			}
		}
		if (evolution.evolvesTo.isNotEmpty()) {
			for(nextEvolution in evolution.evolvesTo){
				val checkExists = ApiGetPokedex.getPokemonEntryFromName(mCombinedPokedexName, nextEvolution.species.name)
				if(checkExists.entryNumber > 0) addEvolutionView(nextEvolution)
			}
		}
	}

	// Details

	private fun updateTypes(details: ModelPokemonDetails) {
		if (mFragmentViewBinding.idLlBanner.idLlChips.childCount > 0) return
		details.types.forEach { modelPokemonDetailsTypes ->
			val typeChipView = ListItemTypeChipBinding.inflate(
				layoutInflater,
				mFragmentViewBinding.idLlBanner.idLlChips,
				true
			)
			typeChipView.idTvType.text = modelPokemonDetailsTypes.type.name
			typeChipView.idCvTypeChip.backgroundTintList =
				UtilityDetailsView.getColorStateListFromTypeName(
					requireContext(),
					modelPokemonDetailsTypes.type.name
				)
		}
	}

	private fun updateTypeDetails(details: ModelPokemonDetails) {

		val shouldPopulateStrengthMajor =
			mFragmentViewBinding.idIncludeDetails.idLlStrengthMajor.childCount == 0
		val shouldPopulateDefenseMajor =
			mFragmentViewBinding.idIncludeDetails.idLlDefenseMajor.childCount == 0
		val shouldPopulateDefenseMinor =
			mFragmentViewBinding.idIncludeDetails.idLlDefenseMinor.childCount == 0
		val shouldPopulateDamageMinor =
			mFragmentViewBinding.idIncludeDetails.idLlDamageMinor.childCount == 0
		val shouldPopulateDamageNone =
			mFragmentViewBinding.idIncludeDetails.idLlDamageNone.childCount == 0
		val shouldPopulateWeaknessMajor =
			mFragmentViewBinding.idIncludeDetails.idLlWeaknessMajor.childCount == 0

		details.types.forEach { typeSlot ->

			val typeDetails = ApiGetType.getPokemonTypeDetails(typeSlot.type.name)

			if (shouldPopulateStrengthMajor) {
				UtilityDetailsView.populateTypeChips(
					requireContext(),
					layoutInflater,
					typeSlot,
					typeDetails.damageRelations.doubleDamageTo,
					mFragmentViewBinding.idIncludeDetails.idLlStrengthMajor,
					R.drawable.ic_dmg_up,
					R.string.label_double_damage
				)
			}
			if (shouldPopulateDefenseMajor) {
				UtilityDetailsView.populateTypeChips(
					requireContext(),
					layoutInflater,
					typeSlot,
					typeDetails.damageRelations.noDamageFrom,
					mFragmentViewBinding.idIncludeDetails.idLlDefenseMajor,
					R.drawable.ic_def_full,
					R.string.label_full_defense
				)
			}
			if (shouldPopulateDefenseMinor) {
				UtilityDetailsView.populateTypeChips(
					requireContext(),
					layoutInflater,
					typeSlot,
					typeDetails.damageRelations.halfDamageFrom,
					mFragmentViewBinding.idIncludeDetails.idLlDefenseMinor,
					R.drawable.ic_def_high,
					R.string.label_double_defense
				)
			}

			if (shouldPopulateDamageMinor) {
				UtilityDetailsView.populateTypeChips(
					requireContext(),
					layoutInflater,
					typeSlot,
					typeDetails.damageRelations.halfDamageTo,
					mFragmentViewBinding.idIncludeDetails.idLlDamageMinor,
					R.drawable.ic_dmg_down,
					R.string.label_half_damage
				)
			}
			if (shouldPopulateDamageNone) {
				UtilityDetailsView.populateTypeChips(
					requireContext(),
					layoutInflater,
					typeSlot,
					typeDetails.damageRelations.noDamageTo,
					mFragmentViewBinding.idIncludeDetails.idLlDamageNone,
					R.drawable.ic_dmg_none,
					R.string.label_no_damage
				)
			}
			if (shouldPopulateWeaknessMajor) {
				UtilityDetailsView.populateTypeChips(
					requireContext(),
					layoutInflater,
					typeSlot,
					typeDetails.damageRelations.doubleDamageFrom,
					mFragmentViewBinding.idIncludeDetails.idLlWeaknessMajor,
					R.drawable.ic_dmg_double,
					R.string.label_half_defense
				)
			}

		}

	}

	//endregion

}