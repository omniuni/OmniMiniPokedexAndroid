package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelVersions
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ApiGetVersions: ApiBase() {

	private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/version/?limit=100"

	private lateinit var mVersions: ModelVersions

	override fun getUrl(): String {
		return URL_ENDPOINT
	}

	override fun getFriendlyName(): String {
		return "All Version Groups"
	}

	override fun isLoaded(): Boolean {
		return this::mVersions.isInitialized
	}

	override fun parse(jsonResponse: String) {

			val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
			val pokemonListAdapter = moshi.adapter(ModelVersions::class.java)
		mVersions = pokemonListAdapter.fromJson(jsonResponse) ?: ModelVersions(0)

	}

	fun getNumberOfVersions(): Int{
		return mVersions.count
	}

}