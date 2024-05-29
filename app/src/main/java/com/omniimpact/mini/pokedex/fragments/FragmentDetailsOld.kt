package com.omniimpact.mini.pokedex.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsBinding
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsOldBinding
import com.omniimpact.mini.pokedex.databinding.ListItemPokemonEvolutionBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeChipBinding
import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.omniimpact.mini.pokedex.models.ModelPokemonDetailsType
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
import com.omniimpact.mini.pokedex.utilities.view.UtilityDetailsView
import com.squareup.picasso.Picasso


class FragmentDetailsOld : Fragment(), IOnApiLoadQueue {

	companion object {
		const val FRAGMENT_KEY = "FragmentDetails"
		const val KEY_POKEMON_ENTRY_NUMBER = "KEY_POKEMON_ENTRY_NUMBER"
		const val KEY_COMBINED_POKEDEX = "KEY_COMBINED_POKEDEX"
	}

	//region Variables

	private lateinit var mFragmentViewBinding: FragmentDetailsOldBinding
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
				parentFragmentManager.popBackStack(FRAGMENT_KEY, FragmentManager.POP_BACK_STACK_INCLUSIVE)
			}
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mFragmentViewBinding = FragmentDetailsOldBinding.inflate(layoutInflater)
		UtilityLoader.registerApiCallListener(this)
		updateBaseViews()
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
				mFragmentViewBinding.idLlBanner.idIvPokemonIcon
			)
	}

	override fun onResume() {
		UtilityLoader.registerApiCallListener(this)
		super.onResume()
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

			is ApiGetType -> {}

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
			if(evolutionSpeciesId != mPokemonSpecies.id) {
				evolutionView.root.setOnClickListener {
					val detailsFragment = FragmentDetailsOld()
					val argumentsBundle = Bundle()
					val speciesEntry =
						ApiGetPokedex.getPokemonEntryFromName(
							mCombinedPokedexName,
							evolution.species.name
						)
					argumentsBundle.putInt(KEY_POKEMON_ENTRY_NUMBER, speciesEntry.entryNumber)
					argumentsBundle.putString(KEY_COMBINED_POKEDEX, mCombinedPokedexName)
					UtilityFragmentManager.using(parentFragmentManager).load(detailsFragment)
						.with(argumentsBundle).into(view?.parent as ViewGroup).now()
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

	private val mTypeMap: MutableMap<Int, ArrayList<ModelPokemonDetailsType>> = mutableMapOf()

	private fun updateTypeDetails(details: ModelPokemonDetails) {

		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idIncludeDetails.idRvStrengthMajor,
			R.string.label_double_damage,
			R.drawable.ic_dmg_up,
			mTypeMap
		)
		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idIncludeDetails.idRvDefenseMajor,
			R.string.label_full_defense,
			R.drawable.ic_def_full,
			mTypeMap
		)
		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idIncludeDetails.idRvDefenseHigh,
			R.string.label_double_defense,
			R.drawable.ic_def_high,
			mTypeMap
		)

		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idIncludeDetails.idRvDamageMinor,
			R.string.label_half_damage,
			R.drawable.ic_dmg_down,
			mTypeMap
		)
		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idIncludeDetails.idRvDamageNone,
			R.string.label_no_damage,
			R.drawable.ic_dmg_none,
			mTypeMap
		)
		UtilityDetailsView.setDetailsAdapter(
			requireContext(),
			details.types,
			mFragmentViewBinding.idIncludeDetails.idRvWeaknessMajor,
			R.string.label_half_defense,
			R.drawable.ic_dmg_double,
			mTypeMap
		)

	}

	//endregion

}