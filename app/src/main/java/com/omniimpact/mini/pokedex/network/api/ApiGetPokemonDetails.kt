package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetPokemonDetails : ApiBase() {

	companion object {
		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/pokemon/"

		private val mPokemonDetailsMap: MutableMap<String, ModelPokemonDetails> = mutableMapOf()

		fun getPokemonDetails(pokemonName: String): ModelPokemonDetails {
			mPokemonDetailsMap[pokemonName]?.also {
				return it
			}
			return ModelPokemonDetails()
		}

	}

	override fun getBaseUrl(): String {
		return URL_ENDPOINT
	}

	override fun isLoaded(): Boolean {
		return mPokemonDetailsMap.containsKey(getParameter())
	}

	override fun getFriendlyName(): String {
		return "Pokemon Details (${getParameter()})"
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonDetailsAdapter = moshi.adapter(ModelPokemonDetails::class.java)
		pokemonDetailsAdapter.fromJson(jsonResponse)?.also { pokemonDetails ->
			mPokemonDetailsMap[pokemonDetails.name] = pokemonDetails
		}

	}

}