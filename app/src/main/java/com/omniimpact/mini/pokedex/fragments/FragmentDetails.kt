package com.omniimpact.mini.pokedex.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsBinding
import com.omniimpact.mini.pokedex.databinding.ListItemPokemonEvolutionBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeChipBinding
import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChain
import com.omniimpact.mini.pokedex.models.ModelPokemonListItem
import com.omniimpact.mini.pokedex.models.ModelPokemonSpecies
import com.omniimpact.mini.pokedex.utilities.UtilityFragmentManager
import com.omniimpact.mini.pokedex.utilities.UtilityPokemonLoader
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class FragmentDetails : Fragment(), UtilityPokemonLoader.IOnEvolutionChain,
	UtilityPokemonLoader.IOnSpecies, UtilityPokemonLoader.IOnDetails {

	companion object {
		const val KEY_ID = "id"
		const val KEY_TRANSITION_TARGET_IMAGE_HEADER = "image_header"
		const val KEY_TRANSITION_TARGET_TEXT_HEADER = "text_header"
		const val KEY_TRANSITION_TARGET_BANNER = "banner"
	}

	private lateinit var mFragmentViewBinding: FragmentDetailsBinding
	private var mPokemonId: Int = 0
	private lateinit var mSourceItem: ModelPokemonListItem

	private lateinit var mPokemonSpecies: ModelPokemonSpecies
	private lateinit var mPokemonDetails: ModelPokemonDetails
	private lateinit var mPokemonEvolutionChain: ModelPokemonEvolutionChain

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.also {
			if (it.keySet().contains(KEY_ID)) {
				mPokemonId = it.getInt(KEY_ID)
			}
		}
		mSourceItem = UtilityPokemonLoader.getPokemonListItemById(mPokemonId)
		sharedElementEnterTransition =
			TransitionInflater.from(requireContext()).inflateTransition(R.transition.transitions_standard)
		postponeEnterTransition()
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
		return mFragmentViewBinding.root
	}

	private fun updateBaseViews() {
		setHasOptionsMenu(true)
		(requireActivity() as AppCompatActivity).supportActionBar?.also {
			it.setDisplayHomeAsUpEnabled(true)
		}
		mFragmentViewBinding.idLlBanner.idTvPokemonName.text = mSourceItem.name.replaceFirstChar { it.titlecase() }
		Picasso.get().load(mSourceItem.iconUrl).fit().centerInside()
			.into(mFragmentViewBinding.idLlBanner.idIvPokemonIcon, OnPicassoImageLoadedDoEnterTransition())
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		ViewCompat.setTransitionName(
			mFragmentViewBinding.idLlBanner.idIvPokemonIcon,
			KEY_TRANSITION_TARGET_IMAGE_HEADER
		)
		ViewCompat.setTransitionName(
			mFragmentViewBinding.idLlBanner.idTvPokemonName,
			KEY_TRANSITION_TARGET_TEXT_HEADER
		)
		ViewCompat.setTransitionName(mFragmentViewBinding.idLlBanner.root, KEY_TRANSITION_TARGET_BANNER)
	}

	inner class OnPicassoImageLoadedDoEnterTransition : Callback {
		override fun onSuccess() {
			startPostponedEnterTransition()
			continueLoad()
		}

		override fun onError(e: Exception?) {
			startPostponedEnterTransition()
			continueLoad()
		}

	}

	private fun continueLoad(){
		UtilityPokemonLoader.loadSpecies(this, mPokemonId)
		UtilityPokemonLoader.loadDetails(this, mPokemonId)
	}

	// Species

	override fun onSpeciesReady() {
		mPokemonSpecies = UtilityPokemonLoader.getPokemonSpecies(mPokemonId)

		mFragmentViewBinding.idIncludeDetails.idTvFlavor.text = mPokemonSpecies.defaultFlavorText

		continueLoadEvolutionChain()
	}

	override fun onSpeciesFailed() {
		mFragmentViewBinding.idIncludeDetails.idCvEvolutions.visibility = View.GONE
	}

	// Evolution Chain

	private fun continueLoadEvolutionChain(){
		UtilityPokemonLoader.loadEvolution(this, mPokemonSpecies.evolutionChain.id)
	}

	override fun onEvolutionChainReady() {
		mPokemonEvolutionChain = UtilityPokemonLoader.getPokemonEvolutionChain(mPokemonSpecies.evolutionChain.id)
		updateEvolutionChainViews()
	}

	private fun updateEvolutionChainViews(){
		mFragmentViewBinding.idIncludeDetails.idLlEvolutions.removeAllViews()
		addEvolutionView(mPokemonEvolutionChain)
	}

	private fun addEvolutionView(evolution: ModelPokemonEvolutionChain){
		val evolutionView = ListItemPokemonEvolutionBinding.inflate(layoutInflater, mFragmentViewBinding.idIncludeDetails.idLlEvolutions, true)
		if(evolution.evolutionDetails.isNotEmpty()){
			evolution.evolutionDetails[0].also {
				evolutionView.idMinLevel.text = it.minLevel?.toString() ?: "?"
			}
		} else {
			evolutionView.idMinLevel.text = "⟡"
		}
		evolutionView.idTvPokemonName.text = evolution.species.name.replaceFirstChar { it.titlecase() }
		val evolutionSpeciesId: Int =  evolution.species.url.takeLastWhile { it.isDigit() || it == '/' }.filter { it.isDigit() }.toInt()
		if(evolution.species.iconUrl.isEmpty()){
			evolution.species.iconUrl = "${UtilityPokemonLoader.URL_POKEMON_SPRITES_BASE}$evolutionSpeciesId.png"
		}
		Picasso.get().load(evolution.species.iconUrl).fit().into(evolutionView.idIvIcon)
		evolutionView.root.setOnClickListener {
			val detailsFragment = FragmentDetails()
			val argumentsBundle = Bundle()
			argumentsBundle.putInt(KEY_ID, evolutionSpeciesId)
			UtilityFragmentManager.using(parentFragmentManager).load(detailsFragment)
				.with(argumentsBundle).into(view?.parent as ViewGroup).now()
		}
		if(evolution.evolvesTo.isNotEmpty()){
			addEvolutionView(evolution.evolvesTo[0])
		}
	}

	// Details

	override fun onDetailsReady() {
		mPokemonDetails = UtilityPokemonLoader.getPokemonDetails(mPokemonId)
		updateTypes()
	}

	private fun updateTypes(){
		mFragmentViewBinding.idLlBanner.idLlChips.removeAllViews()
		mPokemonDetails.types.forEach { modelPokemonDetailsTypes ->
			val typeChipView = ListItemTypeChipBinding.inflate(layoutInflater, mFragmentViewBinding.idLlBanner.idLlChips, true)
			typeChipView.idTvType.text = modelPokemonDetailsTypes.type.name
			typeChipView.idCvTypeChip.backgroundTintList = when(modelPokemonDetailsTypes.type.name){
				"normal" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_normal))
				"fire" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_fire))
				"water" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_water))
				"electric" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_electric))
				"grass" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_grass))
				"ice" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_ice))
				"fighting" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_fighting))
				"poison" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_poison))
				"ground" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_ground))
				"flying" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_flying))
				"psychic" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_psychic))
				"bug" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_bug))
				"rock" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_rock))
				"ghost" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_ghost))
				"dragon" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_dragon))
				"dark" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_dark))
				"steel" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_steel))
				"fairy" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_fairy))
				"stellar" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_stellar))
				"shadow" -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_shadow))
				else -> ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.type_unknown))
			}
		}

	}


}