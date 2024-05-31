package com.omniimpact.mini.pokedex.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTabButtonBinding
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
import com.omniimpact.mini.pokedex.utilities.UtilityFragmentManager
import com.omniimpact.mini.pokedex.utilities.UtilityNavigationCoordinator
import com.omniimpact.mini.pokedex.utilities.view.PicassoTintOnLoad
import com.omniimpact.mini.pokedex.utilities.view.UtilityDetailsView
import com.squareup.picasso.Picasso


class FragmentDetails : Fragment(), IOnApiLoadQueue,
	UtilityNavigationCoordinator.INavigationHandler {


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
		UtilityNavigationCoordinator.registerToHandleNavigatoin(1, this)
		super.onResume()
	}

	override fun onPause() {
		UtilityLoader.deregisterApiCallListener(this)
		UtilityNavigationCoordinator.deregisterHandlingNavigation(this)
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

	private val mTabsMap: Map<Int, String> = mapOf(
		Pair(0, "Overview"),
		Pair(1, "Matches"),
		Pair(2, "Routes"),
		Pair(3, "Moves"),
		Pair(4, "Media"),
	)

	private fun updateDetailCards(){
		val detailViewPager: ViewPager2 = mFragmentViewBinding.idVpDetail
		detailViewPager.offscreenPageLimit = 1
		val detailPageAdapter = DetailCardPagerAdapter(requireActivity())
		detailViewPager.adapter = detailPageAdapter
		detailViewPager.registerOnPageChangeCallback(mvpCallback)
		mTabsMap.forEach{ tabPair ->
			val newTab = ListItemTabButtonBinding.inflate(
				layoutInflater,
				mFragmentViewBinding.idIncludeButtons.idLlTabs,
				true
			)
			newTab.idBtn.text = tabPair.value
			newTab.idBtn.setOnClickListener {
				resetTabBackgroundsExcept(tabPair.key)
				detailViewPager.currentItem = tabPair.key
				newTab.idCvBg.callOnClick()
				newTab.idCvActive.callOnClick()
			}
			newTab.idCvActive.tag = tabPair.key
		}
	}

	private fun resetTabBackgroundsExcept(skip: Int = 0){
		mTabsMap.forEach { tabPair ->
			mFragmentViewBinding.idIncludeButtons.idLlTabs.findViewWithTag<View>(tabPair.key)?.also {
				if(tabPair.key == skip){
					it.visibility = View.VISIBLE
				} else {
					it.visibility = View.GONE
				}
			}

		}
	}

	private val mvpCallback: ViewPager2.OnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
		override fun onPageSelected(position: Int) {
			super.onPageSelected(position)
			resetTabBackgroundsExcept(position)
		}
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
			return mTabsMap.size
		}

		override fun createFragment(position: Int): Fragment{
			val argumentsBundle = Bundle()
			argumentsBundle.putInt(KEY_POKEMON_ENTRY_NUMBER, mPokemonEntryNumber)
			argumentsBundle.putString(KEY_COMBINED_POKEDEX, mCombinedPokedexName)
			when(position){
				1 -> {
					val fragment = FragmentDetailsMatchups()
					fragment.arguments = argumentsBundle
					return fragment
				}
				else -> {
					val fragment = FragmentDetailsOverview()
					fragment.arguments = argumentsBundle
					return fragment
				}
			}
		}

	}

	override fun onNavigationRequested(fragment: Fragment, bundle: Bundle) {
		UtilityFragmentManager.using(parentFragmentManager).load(fragment)
			.with(bundle).into(view?.parent as ViewGroup).now()
	}

	//endregion

}