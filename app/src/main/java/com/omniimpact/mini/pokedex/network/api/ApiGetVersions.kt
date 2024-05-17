package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelVersions
import com.omniimpact.mini.pokedex.models.VersionResult
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetVersions : ApiBase() {

	companion object {

		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/version/?limit=100"
		private var mVersions: ModelVersions = ModelVersions()

		fun getVersions(): List<VersionResult> {
			return mVersions.results
		}

	}

	override fun getBaseUrl(): String {
		return URL_ENDPOINT
	}

	override fun getFriendlyName(): String {
		return "All Version Groups"
	}

	override fun isLoaded(): Boolean {
		return mVersions.count > 0
	}

	override fun parse(jsonResponse: String) {

		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonListAdapter = moshi.adapter(ModelVersions::class.java)
		mVersions = pokemonListAdapter.fromJson(jsonResponse) ?: ModelVersions()

	}


}