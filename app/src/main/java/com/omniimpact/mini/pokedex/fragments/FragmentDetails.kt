package com.omniimpact.mini.pokedex.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeChipBinding
import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
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
import com.omniimpact.mini.pokedex.utilities.view.PicassoTintOnLoad
import com.omniimpact.mini.pokedex.utilities.view.UtilityDetailsView
import com.squareup.picasso.Picasso


class FragmentDetails : Fragment(), IOnApiLoadQueue {

	companion object {
		const val FRAGMENT_KEY = "FragmentDetails"
		const val KEY_POKEMON_ENTRY_NUMBER = "KEY_POKEMON_ENTRY_NUMBER"
		const val KEY_COMBINED_POKEDEX = "KEY_COMBINED_POKEDEX"
	}

	//region Variables

	private lateinit var mFragmentViewBinding: FragmentDetailsBinding

	private var mPokemonEntryNumber: Int = -1
	private var mCombinedPokedexName: String = String()

	private var mPokemonSpecies: ModelPokemonSpecies = ModelPokemonSpecies()
	private var mSourceItem: PokedexPokemonEntry = PokedexPokemonEntry()
	private var mPokemonId: Int = -1

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
		mPokemonId = ApiGetPokedex.getPokemonIdFromUrl(mSourceItem.pokemonSpecies.url)
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
		mFragmentViewBinding = FragmentDetailsBinding.inflate(layoutInflater)
		UtilityLoader.registerApiCallListener(this)
		updateBaseViews()
		UtilityLoader.addRequests(
			mapOf(
				ApiGetPokemonDetails() to mSourceItem.pokemonSpecies.name,
				ApiGetPokemonSpecies() to mSourceItem.pokemonSpecies.name
			), requireContext()
		)
		updateDetailCards()
		return mFragmentViewBinding.root
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

	override fun onComplete() {}

	override fun onSuccess(success: IApi) {
		when (success) {
			is ApiGetPokemonDetails -> {
				updateTypes(
					ApiGetPokemonDetails.getPokemonDetails(mSourceItem.pokemonSpecies.name)
				)
			}
			is ApiGetPokemonSpecies -> {
				mPokemonSpecies = ApiGetPokemonSpecies.getPokemonSpecies(mSourceItem.pokemonSpecies.name)
				mFragmentViewBinding.idLlBanner.idTvPokemonName.text = ApiGetPokemonSpecies.getPokemonName(mPokemonSpecies)
			}
			is ApiGetPokemonEvolutions -> {}
			is ApiGetType -> {}
			else -> {}
		}

	}

	override fun onFailed(failure: IApi) {
	}

	//endregion

	//region UI


	private fun updateBaseViews() {
		setHasOptionsMenu(true)
		(requireActivity() as AppCompatActivity).supportActionBar?.also {
			it.setDisplayHomeAsUpEnabled(true)
		}
		val imageUrl = ApiGetPokedex.getImageUrlFromPokemonId(mPokemonId)
		Picasso.get().load(imageUrl).fit()
			.centerInside()
			.into(
				mFragmentViewBinding.idLlBanner.idIvPokemonIcon,
				PicassoTintOnLoad(mFragmentViewBinding.idLlBanner.idIvPlatform, mFragmentViewBinding.idLlBanner.idIvPokemonIcon)
			)
	}

	private fun updateDetailCards(){
		val detailViewPager: ViewPager2 = mFragmentViewBinding.idVpDetail
		val detailPageAdapter = DetailCardPagerAdapter(requireActivity())
		detailViewPager.adapter = detailPageAdapter
	}

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

	private inner class DetailCardPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

		override fun getItemCount(): Int {
			return 5
		}

		override fun createFragment(position: Int): Fragment{
			return FragmentDetailsOverview()
		}

	}

	//endregion

}