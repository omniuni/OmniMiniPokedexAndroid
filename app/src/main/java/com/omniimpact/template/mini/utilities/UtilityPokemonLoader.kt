package com.omniimpact.template.mini.utilities

import com.omniimpact.template.mini.models.ModelPokemonList
import com.omniimpact.template.mini.models.ModelPokemonListItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL

object UtilityPokemonLoader {

    private val loadScope = CoroutineScope(Job() + Dispatchers.IO)
    private const val URL_POKEMON_SPRITES_BASE = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" // add [index].png
    private const val URL_POKEMON_LIST = "https://pokeapi.co/api/v2/pokemon?offset=0&limit=3000"
    private lateinit var mPokemonList: ModelPokemonList

    interface IOnLoad{
        fun onPokemonLoaded()
    }

    fun load(caller: IOnLoad) {

        loadScope.launch {
            val jsonResult = URL(URL_POKEMON_LIST).readText()

            val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val pokemonListAdapter = moshi.adapter(ModelPokemonList::class.java)
            mPokemonList = pokemonListAdapter.fromJson(jsonResult) ?: ModelPokemonList(arrayListOf())

            // For some reason, they don't give us the id or icon, so we're going to need to do this ourselves
            for (pokemon: ModelPokemonListItem in mPokemonList.results){
                val id: Int = pokemon.url.takeLastWhile { it.isDigit() || it == '/' }.filter { it.isDigit() }.toInt()
                pokemon.id = id
                pokemon.iconUrl = "$URL_POKEMON_SPRITES_BASE$id.png"
            }
            launch(Dispatchers.Main) {
                caller.onPokemonLoaded()
            }
        }

    }

    fun getLoadedPokemonList(): ModelPokemonList{
        if(!this::mPokemonList.isInitialized){
            return ModelPokemonList(arrayListOf())
        }
        return mPokemonList
    }

}