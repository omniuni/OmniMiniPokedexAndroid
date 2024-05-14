package com.omniimpact.mini.pokedex.network.api

import android.content.Context
import com.omniimpact.mini.pokedex.models.ModelPokemonList
import com.omniimpact.mini.pokedex.models.ModelPokemonListItem
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ApiGetAllPokemon: ApiBase() {

	private const val URL_POKEMON_LIST = "https://pokeapi.co/api/v2/pokemon?offset=0&limit=3000"
	const val URL_POKEMON_SPRITES_BASE =
		"https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" // add [index].png


	private lateinit var mPokemonList: ModelPokemonList

	override fun getUrl(): String {
		return URL_POKEMON_LIST
	}

	override fun getFriendlyName(): String {
		return "All Pokemon"
	}

	override fun isLoaded(): Boolean {
		return this::mPokemonList.isInitialized
	}

	override fun parse(jsonResponse: String) {

			val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
			val pokemonListAdapter = moshi.adapter(ModelPokemonList::class.java)
			mPokemonList = pokemonListAdapter.fromJson(jsonResponse) ?: ModelPokemonList(arrayListOf())

			// For some reason, they don't give us the id or icon, so we're going to need to do this ourselves
			for (pokemon: ModelPokemonListItem in mPokemonList.results) {
				val id: Int =
					pokemon.url.takeLastWhile { it.isDigit() || it == '/' }.filter { it.isDigit() }.toInt()
				pokemon.id = id
				pokemon.iconUrl = "${URL_POKEMON_SPRITES_BASE}$id.png"
			}

	}

	fun getPokemonList(): ModelPokemonList{
		return mPokemonList
	}

	fun getPokemonListItemById(id: Int): ModelPokemonListItem {
		return mPokemonList.results.first {
			it.id == id
		}
	}

}