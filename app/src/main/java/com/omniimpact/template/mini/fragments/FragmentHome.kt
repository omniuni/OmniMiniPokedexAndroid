package com.omniimpact.template.mini.fragments

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentHomeBinding
import com.omniimpact.template.mini.models.ModelPokemonList
import com.omniimpact.template.mini.models.ModelPokemonListItem
import com.omniimpact.template.mini.utilities.AdapterRecyclerViewPokemonList
import com.omniimpact.template.mini.utilities.UtilityFragmentManager
import com.omniimpact.template.mini.utilities.UtilityPokemonLoader

class FragmentHome : Fragment(), UtilityPokemonLoader.IOnLoad,
	AdapterRecyclerViewPokemonList.IOnListActions {

	companion object {
		private var mIsOptionsShown = false
	}

	private lateinit var mFragmentViewBinding: FragmentHomeBinding
	private lateinit var mAdapter: AdapterRecyclerViewPokemonList
	private lateinit var mRecyclerViewScrollState: Parcelable

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setHasOptionsMenu(true)
	}

	override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
		menuInflater.inflate(R.menu.menu_home, menu)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mFragmentViewBinding = FragmentHomeBinding.inflate(layoutInflater)
		updateMenu()
		return mFragmentViewBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		UtilityPokemonLoader.load(this)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			R.id.toggle_search_sort -> {
				mIsOptionsShown = !mIsOptionsShown
				updateMenu()
			}
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onPause() {
		if (this::mAdapter.isInitialized) {
			mFragmentViewBinding.idRvPokemon.layoutManager?.onSaveInstanceState()?.also {
				mRecyclerViewScrollState = it
			}
		}
		super.onPause()
	}

	private fun updateMenu() {
		(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
		mFragmentViewBinding.idCvToolbar.visibility = if (mIsOptionsShown) {
			View.VISIBLE
		} else {
			View.GONE
		}
	}

	override fun onPokemonLoaded() {
		val pokemonList: ModelPokemonList = UtilityPokemonLoader.getLoadedPokemonList()
		Log.d(
			FragmentHome::class.simpleName,
			"Successfully loaded ${pokemonList.results.size} pokemon."
		)
		updateViews()
	}

	private fun updateViews() {
		mFragmentViewBinding.idSwSort.isEnabled = false
		if (UtilityPokemonLoader.getLoadedPokemonList().results.isNotEmpty()) {
			mFragmentViewBinding.idPbLoader.visibility = View.GONE
			setRecyclerViewAdapter()
		}
		if (this::mAdapter.isInitialized) {
			mFragmentViewBinding.idSwSort.setOnCheckedChangeListener { _, isChecked ->
				mAdapter.sortItemsAlphabetical(isChecked)
			}
			mFragmentViewBinding.idBClear.setOnClickListener {
				mFragmentViewBinding.idEtFilter.setText(String())
			}
			mFragmentViewBinding.idEtFilter.doOnTextChanged { text, _, _, _ -> // start, before, count
				mAdapter.setFilter(text.toString())
			}
			mAdapter.setFilter(mFragmentViewBinding.idEtFilter.text.toString())
		} else {
			mFragmentViewBinding.idTvTotalShown.text = String()
		}
		if (this::mRecyclerViewScrollState.isInitialized) {
			mFragmentViewBinding.idRvPokemon.layoutManager?.onRestoreInstanceState(
				mRecyclerViewScrollState
			)
		}
	}

	private fun setRecyclerViewAdapter() {
		if (!this::mAdapter.isInitialized) {
			mAdapter = AdapterRecyclerViewPokemonList()
		}
		val twoColumnsLayout: LayoutManager = GridLayoutManager(requireContext(), 2)
		mFragmentViewBinding.idRvPokemon.layoutManager = twoColumnsLayout
		mFragmentViewBinding.idRvPokemon.adapter = mAdapter
		mAdapter.setUpdateCallback(this)
		mAdapter.updateItems()
		mFragmentViewBinding.idSwSort.isEnabled = true
	}

	override fun onListUpdated() {
		if (this::mAdapter.isInitialized) {
			mFragmentViewBinding.idTvTotalShown.text = getString(
				R.string.a_of_b,
				mAdapter.getFilteredItemCount(),
				UtilityPokemonLoader.getLoadedPokemonListCount()
			)
		}
	}

	override fun onItemClicked(item: ModelPokemonListItem, imageView: ImageView, textView: TextView) {
		val detailsFragment = FragmentDetails()
		val argumentsBundle = Bundle()
		argumentsBundle.putInt(FragmentDetails.KEY_ID, item.id)
		val animationsMap: Map<View, String> = mapOf(
			imageView to FragmentDetails.KEY_TRANSITION_TARGET_IMAGE_HEADER,
			textView to FragmentDetails.KEY_TRANSITION_TARGET_TEXT_HEADER
		)
		UtilityFragmentManager.using(parentFragmentManager).load(detailsFragment)
			.with(argumentsBundle, animationsMap).into(view?.parent as ViewGroup).now()
	}

}