package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelPokemonEvolution
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChain
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetPokemonEvolutions : ApiBase() {

	companion object {

		private const val URL_POKEMON_EVOLUTIONS = "https://pokeapi.co/api/v2/evolution-chain/"
		private val mPokemonEvolutionsMap: MutableMap<Int, ModelPokemonEvolutionChain> = mutableMapOf()

		fun getPokemonEvolutionChain(evolutionId: Int): ModelPokemonEvolutionChain {
			mPokemonEvolutionsMap[evolutionId]?.also {
				return it
			}
			return ModelPokemonEvolutionChain()
		}

	}


	override fun getBaseUrl(): String {
		return URL_POKEMON_EVOLUTIONS
	}

	override fun isLoaded(): Boolean {
		return mPokemonEvolutionsMap.containsKey(getParameter().toInt())
	}

	override fun getFriendlyName(): String {
		return "Evolutions"
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonListAdapter = moshi.adapter(ModelPokemonEvolution::class.java)
		pokemonListAdapter.fromJson(jsonResponse)?.also {
			mPokemonEvolutionsMap[getParameter().toInt()] = it.chain
		}
	}

}