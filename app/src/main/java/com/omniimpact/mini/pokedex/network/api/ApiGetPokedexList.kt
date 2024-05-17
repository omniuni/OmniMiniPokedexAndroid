package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelPokedexEntry
import com.omniimpact.mini.pokedex.models.ModelPokedexList
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetPokedexList : ApiBase() {

	companion object {

		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/pokedex/?limit=50"
		private var mPokedexList: MutableList<ModelPokedexEntry> = mutableListOf()

		fun getPokedexList(): List<ModelPokedexEntry> {
			return mPokedexList
		}

	}

	override fun getBaseUrl(): String {
		return URL_ENDPOINT
	}

	override fun getFriendlyName(): String {
		return "Pokédex List"
	}

	override fun isLoaded(): Boolean {
		return mPokedexList.isNotEmpty()
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val adapter = moshi.adapter(ModelPokedexList::class.java)
		val modelPokedexList = adapter.fromJson(jsonResponse) ?: ModelPokedexList()
		mPokedexList.addAll(modelPokedexList.results)
	}

}