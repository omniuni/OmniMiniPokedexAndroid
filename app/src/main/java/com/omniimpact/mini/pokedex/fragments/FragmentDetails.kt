package com.omniimpact.mini.pokedex.fragments

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsBinding
import com.omniimpact.mini.pokedex.databinding.ListItemPokemonEvolutionBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeChipBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeLabelHeaderBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeSwChipBinding
import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.omniimpact.mini.pokedex.models.ModelPokemonDetailsType
import com.omniimpact.mini.pokedex.models.ModelPokemonDetailsTypes
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChain
import com.omniimpact.mini.pokedex.models.ModelPokemonListItem
import com.omniimpact.mini.pokedex.models.ModelPokemonSpecies
import com.omniimpact.mini.pokedex.network.UtilityLoader
import com.omniimpact.mini.pokedex.network.api.ApiGetAllPokemon
import com.omniimpact.mini.pokedex.network.api.ApiGetPokemonDetails
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue
import com.omniimpact.mini.pokedex.utilities.UtilityFragmentManager
import com.omniimpact.mini.pokedex.utilities.UtilityPokemonLoader
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso


class FragmentDetails : Fragment(), IOnApiLoadQueue {

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
		mSourceItem = ApiGetAllPokemon.getPokemonListItemById(mPokemonId)
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
			continueLoad()
			val icon: ImageView = mFragmentViewBinding.idLlBanner.idIvPokemonIcon
			val platform: ImageView = mFragmentViewBinding.idLlBanner.idIvPlatform
			Palette.from(icon.drawable.toBitmap()).generate { palette ->
				palette?.let {
					val context = requireContext()
					val color: Int =
						when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
							Configuration.UI_MODE_NIGHT_YES -> {
								it.getDarkMutedColor(requireContext().getColor(R.color.md_theme_surfaceContainer))
							}

							Configuration.UI_MODE_NIGHT_NO -> {
								it.getLightMutedColor(requireContext().getColor(R.color.md_theme_surfaceContainer))
							}

							else -> {
								it.getMutedColor(requireContext().getColor(R.color.md_theme_surfaceContainer))
							}
						}
					platform.imageTintList = ColorStateList.valueOf(color)
					platform.visibility = View.VISIBLE
				}
			}
		}

		override fun onError(e: Exception?) {
			continueLoad()
		}

	}

	private fun continueLoad(){
		UtilityLoader.registerListener(this)
		UtilityLoader.enqueue(mapOf(
			ApiGetPokemonDetails to mPokemonId.toString()
		))
	}

	// Species

	fun onSpeciesReady() {
		mPokemonSpecies = UtilityPokemonLoader.getPokemonSpecies(mPokemonId)

		mFragmentViewBinding.idIncludeDetails.idTvFlavor.text = mPokemonSpecies.defaultFlavorText

		continueLoadEvolutionChain()
	}

	fun onSpeciesFailed() {
		mFragmentViewBinding.idIncludeDetails.idCvEvolutions.visibility = View.GONE
	}

	// Evolution Chain

	private fun continueLoadEvolutionChain(){
		//UtilityPokemonLoader.loadEvolution(this, mPokemonSpecies.evolutionChain.id)
	}

	fun onEvolutionChainReady() {
		mPokemonEvolutionChain = UtilityPokemonLoader.getPokemonEvolutionChain(mPokemonSpecies.evolutionChain.id)
		updateEvolutionChainViews()
	}

	private fun updateEvolutionChainViews(){
		if(mFragmentViewBinding.idIncludeDetails.idLlEvolutions.childCount > 0) return
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
			evolution.species.iconUrl = "${ApiGetAllPokemon.URL_POKEMON_SPRITES_BASE}$evolutionSpeciesId.png"
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

	private fun onDetailsReady() {
		mPokemonDetails = ApiGetPokemonDetails.getPokemonDetails(mPokemonId)
		updateTypes()
	}

	private fun onTypeDetailsReady() {

		val shouldPopulateStrengthMajor = mFragmentViewBinding.idIncludeDetails.idLlStrengthMajor.childCount == 0
		val shouldPopulateDefenseMajor = mFragmentViewBinding.idIncludeDetails.idLlDefenseMajor.childCount == 0
		val shouldPopulateDefenseMinor = mFragmentViewBinding.idIncludeDetails.idLlDefenseMinor.childCount == 0
		val shouldPopulateDamageMinor = mFragmentViewBinding.idIncludeDetails.idLlDamageMinor.childCount == 0
		val shouldPopulateDamageNone = mFragmentViewBinding.idIncludeDetails.idLlDamageNone.childCount == 0
		val shouldPopulateWeaknessMajor = mFragmentViewBinding.idIncludeDetails.idLlWeaknessMajor.childCount == 0

		mPokemonDetails.types.forEach { typeSlot ->

			val typeDetails = ApiGetPokemonDetails.getPokemonTypeDetails(typeSlot.type.name)

			if(shouldPopulateStrengthMajor){
				populateTypeChips(
					typeSlot,
					typeDetails.damageRelations.doubleDamageTo,
					mFragmentViewBinding.idIncludeDetails.idLlStrengthMajor,
					R.drawable.ic_dmg_up,
					R.string.label_double_damage
				)
			}
			if(shouldPopulateDefenseMajor){
				populateTypeChips(
					typeSlot,
					typeDetails.damageRelations.noDamageFrom,
					mFragmentViewBinding.idIncludeDetails.idLlDefenseMajor,
					R.drawable.ic_def_full,
					R.string.label_full_defense
				)
			}
			if(shouldPopulateDefenseMinor){
				populateTypeChips(
					typeSlot,
					typeDetails.damageRelations.halfDamageFrom,
					mFragmentViewBinding.idIncludeDetails.idLlDefenseMinor,
					R.drawable.ic_def_high,
					R.string.label_double_defense
				)
			}

			if(shouldPopulateDamageMinor){
				populateTypeChips(
					typeSlot,
					typeDetails.damageRelations.halfDamageTo,
					mFragmentViewBinding.idIncludeDetails.idLlDamageMinor,
					R.drawable.ic_dmg_down,
					R.string.label_half_damage
				)
			}
			if(shouldPopulateDamageNone){
				populateTypeChips(
					typeSlot,
					typeDetails.damageRelations.noDamageTo,
					mFragmentViewBinding.idIncludeDetails.idLlDamageNone,
					R.drawable.ic_dmg_none,
					R.string.label_no_damage
				)
			}
			if(shouldPopulateWeaknessMajor){
				populateTypeChips(
					typeSlot,
					typeDetails.damageRelations.doubleDamageFrom,
					mFragmentViewBinding.idIncludeDetails.idLlWeaknessMajor,
					R.drawable.ic_dmg_double,
					R.string.label_half_defense
				)
			}

		}

	}

	private fun populateTypeChips(
		fromType: ModelPokemonDetailsTypes,
		types: List<ModelPokemonDetailsType>,
		target: LinearLayout,
		drawableResource: Int,
		headerResource: Int
	){
		if(target.childCount == 0) {
			val typeChipHeader = ListItemTypeLabelHeaderBinding.inflate(
				layoutInflater,
				target,
				true
			)
			typeChipHeader.idTvTitle.setText(headerResource)
		}
		if(types.isEmpty()){
			val typeChip = ListItemTypeSwChipBinding.inflate(
				layoutInflater,
				target,
				true
			)
			typeChip.idCvTypeChip.backgroundTintList = getColorStateListFromTypeName(String())
			typeChip.idCvTypeContributing.backgroundTintList = getColorStateListFromTypeName(fromType.type.name)
			typeChip.idTvType.text = resources.getString(R.string.label_none)
			typeChip.idIvIcon.setImageResource(drawableResource)
			return
		}
		types.forEach {type ->
			val typeChip = ListItemTypeSwChipBinding.inflate(
				layoutInflater,
				target,
				true
			)
			typeChip.idCvTypeChip.backgroundTintList = getColorStateListFromTypeName(type.name)
			typeChip.idCvTypeContributing.backgroundTintList = getColorStateListFromTypeName(fromType.type.name)
			typeChip.idTvType.text = type.name
			typeChip.idIvIcon.setImageResource(drawableResource)
		}
	}

	private fun updateTypes(){
		if(mFragmentViewBinding.idLlBanner.idLlChips.childCount > 0) return
		mPokemonDetails.types.forEach { modelPokemonDetailsTypes ->
			val typeChipView = ListItemTypeChipBinding.inflate(layoutInflater, mFragmentViewBinding.idLlBanner.idLlChips, true)
			typeChipView.idTvType.text = modelPokemonDetailsTypes.type.name
			typeChipView.idCvTypeChip.backgroundTintList = getColorStateListFromTypeName(modelPokemonDetailsTypes.type.name)
		}
	}

	private fun getColorStateListFromTypeName(name: String): ColorStateList {
		return when(name){
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

	override fun onComplete() {
		onDetailsReady()
		onTypeDetailsReady()
	}

	override fun onSuccess(success: IApi) {
	}

	override fun onFailed(failure: IApi) {
	}

}