package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ApiGetPokemonDetails: ApiBase() {

	private const val URL_POKEMON_DETAILS = "https://pokeapi.co/api/v2/pokemon/"

	private val mPokemonDetailsMap: MutableMap<Int, ModelPokemonDetails> = mutableMapOf()

	override fun getUrl(): String {
		return URL_POKEMON_DETAILS+args
	}

	override fun isLoaded(): Boolean {
		return mPokemonDetailsMap.containsKey(args.toInt())
	}

	override fun getFriendlyName(): String {
		return "Pokemon Details"
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonDetailsAdapter = moshi.adapter(ModelPokemonDetails::class.java)
		pokemonDetailsAdapter.fromJson(jsonResponse)?.also { pokemonDetails ->
			mPokemonDetailsMap[args.toInt()] = pokemonDetails
		}


	}

	fun getPokemonDetails(pokemonId: Int): ModelPokemonDetails{
		mPokemonDetailsMap[pokemonId]?.also {
			return it
		}
		return ModelPokemonDetails(
			id = 0,
			types = listOf()
		)
	}

}