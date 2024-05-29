package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelVersionGroup
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetVersionGroup : ApiBase() {

	companion object {

		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/version-group/"
		private var mVersionGroup: MutableMap<String, ModelVersionGroup> = mutableMapOf()

		fun getVersionGroupByName(name: String): ModelVersionGroup {
			return mVersionGroup[name] ?: ModelVersionGroup()
		}

	}

	override fun getBaseUrl(): String {
		return URL_ENDPOINT
	}

	override fun getFriendlyName(): String {
		return "Version Group (${getParameter()})"
	}

	override fun isLoaded(): Boolean {
		return mVersionGroup.containsKey(getParameter())
	}

	override fun parse(jsonResponse: String) {

		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonListAdapter = moshi.adapter(ModelVersionGroup::class.java)
		val modelVersionGroup = pokemonListAdapter.fromJson(jsonResponse) ?: ModelVersionGroup()
		mVersionGroup[modelVersionGroup.name] = modelVersionGroup

	}

}