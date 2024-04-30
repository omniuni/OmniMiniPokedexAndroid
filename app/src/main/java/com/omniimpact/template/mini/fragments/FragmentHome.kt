package com.omniimpact.template.mini.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.omniimpact.template.mini.databinding.ActivityMainFragmentHomeBinding
import com.omniimpact.template.mini.models.ModelPokemonList
import com.omniimpact.template.mini.utilities.AdapterRecyclerViewPokemonList
import com.omniimpact.template.mini.utilities.UtilityPokemonLoader

class FragmentHome: Fragment(), UtilityPokemonLoader.IOnLoad {

    private lateinit var mFragmentViewBinding: ActivityMainFragmentHomeBinding
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
        if(UtilityPokemonLoader.getLoadedPokemonList().results.isNotEmpty()){
            mFragmentViewBinding.idPbLoader.visibility = View.GONE
            setRecyclerViewAdapter()
        }
    }

    private fun setRecyclerViewAdapter(){
        val twoColumnsLayout: LayoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = AdapterRecyclerViewPokemonList()
        mFragmentViewBinding.idRvPokemon.layoutManager = twoColumnsLayout
        mFragmentViewBinding.idRvPokemon.adapter = adapter
        adapter.updateItems()
    }

}