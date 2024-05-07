package com.omniimpact.template.mini.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.omniimpact.template.mini.R
import com.omniimpact.template.mini.databinding.FragmentDetailsBinding
import com.omniimpact.template.mini.databinding.ListItemPokemonEvolutionBinding
import com.omniimpact.template.mini.models.ModelPokemonEvolution
import com.omniimpact.template.mini.models.ModelPokemonEvolutionChain
import com.omniimpact.template.mini.models.ModelPokemonListItem
import com.omniimpact.template.mini.models.ModelPokemonSpecies
import com.omniimpact.template.mini.utilities.UtilityPokemonLoader
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class FragmentDetails : Fragment(), UtilityPokemonLoader.IOnEvolutionChain,
	UtilityPokemonLoader.IOnSpecies {

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
			continueLoadSpecies()
		}

		override fun onError(e: Exception?) {
			startPostponedEnterTransition()
			continueLoadSpecies()
		}

	}

	private fun continueLoadSpecies(){
		UtilityPokemonLoader.loadSpecies(this, mPokemonId)
	}

	// Species

	override fun onSpeciesReady() {
		mPokemonSpecies = UtilityPokemonLoader.getPokemonSpecies(mPokemonId)
		continueLoadEvolutionChain()
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
		addEvolutionView(mPokemonEvolutionChain)
	}

	private fun addEvolutionView(evolution: ModelPokemonEvolutionChain){
		val evolutionView = ListItemPokemonEvolutionBinding.inflate(layoutInflater, mFragmentViewBinding.idIncludeDetails.idLlEvolutions, true)
		if(evolution.evolutionDetails.isNotEmpty()){
			evolution.evolutionDetails[0].also {
				evolutionView.idMinLevel.text = it.minLevel.toString()
			}
		} else {
			evolutionView.idMinLevel.text = "0"
		}
		evolutionView.idTvPokemonName.text = evolution.species.name.replaceFirstChar { it.titlecase() }
		val evolutionSpeciesId: Int =  evolution.species.url.takeLastWhile { it.isDigit() || it == '/' }.filter { it.isDigit() }.toInt()
		if(evolution.species.iconUrl.isEmpty()){
			evolution.species.iconUrl = "${UtilityPokemonLoader.URL_POKEMON_SPRITES_BASE}$evolutionSpeciesId.png"
		}
		Picasso.get().load(evolution.species.iconUrl).fit().into(evolutionView.idIvIcon)
		if(evolution.evolvesTo.isNotEmpty()){
			addEvolutionView(evolution.evolvesTo[0])
		}
	}



}