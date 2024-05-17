package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelPokedex
import com.omniimpact.mini.pokedex.models.PokedexPokemonEntry
import com.omniimpact.mini.pokedex.models.VersionGroupPokedex
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetPokedex : ApiBase() {

	companion object {

		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/pokedex/"
		private const val URL_POKEMON_SPRITES_BASE =
			"https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" // add [index].png
		private var mPokedexMap: MutableMap<String, ModelPokedex> = mutableMapOf()
		private var mCombinedPokedexLists: MutableMap<String, MutableMap<Int, PokedexPokemonEntry>> =
			mutableMapOf()

		fun getPokemonInPokedexList(pokedexList: List<VersionGroupPokedex>): MutableMap<Int, PokedexPokemonEntry> {
			val combinedKey = getCombinedPokedexKey(pokedexList)
			if (mCombinedPokedexLists.containsKey(combinedKey)) return mCombinedPokedexLists[combinedKey]
				?: mutableMapOf()
			val newPokemonMap: MutableMap<Int, PokedexPokemonEntry> = hashMapOf()
			for (pokedex in pokedexList) {
				mPokedexMap[pokedex.name]?.also { modelPokedex ->
					for (pokemonEntry in modelPokedex.pokemonEntries) {
						if (!newPokemonMap.containsKey(pokemonEntry.entryNumber)) {
							newPokemonMap[pokemonEntry.entryNumber] = pokemonEntry
						}
					}
				}
			}
			mCombinedPokedexLists[combinedKey] = newPokemonMap
			return newPokemonMap
		}

		fun getCombinedPokedexKey(pokedexList: List<VersionGroupPokedex>): String {
			var combinedKey = String()
			for (pokedex in pokedexList) {
				combinedKey += pokedex.name
			}
			return combinedKey
		}

		fun getImageUrlFromPokemonId(pokemonId: Int): String {
			return "$URL_POKEMON_SPRITES_BASE$pokemonId.png"
		}

		fun getPokedexPokemonEntry(
			pokedexCombinedKey: String,
			pokemonEntryId: Int
		): PokedexPokemonEntry {
			return mCombinedPokedexLists[pokedexCombinedKey]?.get(pokemonEntryId) ?: PokedexPokemonEntry()
		}

	}

	override fun getBaseUrl(): String {
		return URL_ENDPOINT
	}

	override fun getFriendlyName(): String {
		return "Pokédex (${getParameter()})"
	}

	override fun isLoaded(): Boolean {
		return mPokedexMap.containsKey(getParameter())
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val adapter = moshi.adapter(ModelPokedex::class.java)
		val modelPokedex: ModelPokedex = adapter.fromJson(jsonResponse) ?: ModelPokedex()
		mPokedexMap[modelPokedex.name] = modelPokedex
	}

}