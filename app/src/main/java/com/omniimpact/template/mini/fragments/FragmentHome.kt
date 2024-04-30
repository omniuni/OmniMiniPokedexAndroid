package com.omniimpact.template.mini.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.omniimpact.template.mini.R
import com.omniimpact.template.mini.databinding.ActivityMainFragmentHomeBinding
import com.omniimpact.template.mini.models.ModelPokemonList
import com.omniimpact.template.mini.utilities.AdapterRecyclerViewPokemonList
import com.omniimpact.template.mini.utilities.UtilityPokemonLoader

class FragmentHome: Fragment(), UtilityPokemonLoader.IOnLoad, AdapterRecyclerViewPokemonList.IOnUpdate {

    private lateinit var mFragmentViewBinding: ActivityMainFragmentHomeBinding
    private lateinit var mAdapter: AdapterRecyclerViewPokemonList

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentViewBinding = ActivityMainFragmentHomeBinding.inflate(layoutInflater)
        return mFragmentViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        UtilityPokemonLoader.load(this)
    }

    override fun onPokemonLoaded() {
        val pokemonList: ModelPokemonList = UtilityPokemonLoader.getLoadedPokemonList()
        Log.d(FragmentHome::class.simpleName, "Successfully loaded ${pokemonList.results.size} pokemon.")
        updateViews()
    }

    private fun updateViews(){
        mFragmentViewBinding.idSwSort.isEnabled = false
        if(UtilityPokemonLoader.getLoadedPokemonList().results.isNotEmpty()){
            mFragmentViewBinding.idPbLoader.visibility = View.GONE
            setRecyclerViewAdapter()
        }
        if(this::mAdapter.isInitialized){
            mFragmentViewBinding.idSwSort.setOnCheckedChangeListener { _, isChecked ->
                    mAdapter.sortItemsAlphabetical(isChecked)
            }
            mFragmentViewBinding.idBClear.setOnClickListener {
                mFragmentViewBinding.idEtFilter.setText(String())
            }
            mFragmentViewBinding.idEtFilter.doOnTextChanged { text, _, _, _ -> // start, before, count
                mAdapter.setFilter(text.toString())
            }
        } else {
             mFragmentViewBinding.idTvTotalShown.text = String()
        }
    }

    private fun setRecyclerViewAdapter(){
        if(!this::mAdapter.isInitialized){
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
        if(this::mAdapter.isInitialized) {
            mFragmentViewBinding.idTvTotalShown.text = getString(
                R.string.a_of_b,
                mAdapter.getFilteredItemCount(),
                UtilityPokemonLoader.getLoadedPokemonListCount()
            )
        }
    }

}