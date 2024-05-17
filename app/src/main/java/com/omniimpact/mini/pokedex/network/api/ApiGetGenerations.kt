package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.Generation
import com.omniimpact.mini.pokedex.models.ModelGenerations
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetGenerations : ApiBase() {

	companion object {

		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/generation/?limit=20"
		private var mGenerations: ModelGenerations = ModelGenerations()

		fun getGenerations(): List<Generation> {
			return mGenerations.results
		}

	}

	override fun getBaseUrl(): String {
		return URL_ENDPOINT
	}

	override fun getFriendlyName(): String {
		return "All Generations"
	}

	override fun isLoaded(): Boolean {
		return mGenerations.count > 0
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonListAdapter = moshi.adapter(ModelGenerations::class.java)
		mGenerations = pokemonListAdapter.fromJson(jsonResponse) ?: ModelGenerations()
	}

}