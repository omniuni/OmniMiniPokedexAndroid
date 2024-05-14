package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelVersionGroup
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ApiGetVersionGroup : ApiBase() {

	private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/version-group/"

	private var mVersionGroup: MutableMap<Int, ModelVersionGroup> = mutableMapOf()

	override fun getUrl(): String {
		return URL_ENDPOINT + args
	}

	override fun getFriendlyName(): String {
		return "Version Group ($args)"
	}

	override fun isLoaded(): Boolean {
		return mVersionGroup.containsKey(args.toInt())
	}

	override fun parse(jsonResponse: String) {

		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonListAdapter = moshi.adapter(ModelVersionGroup::class.java)
		val modelVersionGroup = pokemonListAdapter.fromJson(jsonResponse) ?: ModelVersionGroup(0)
		mVersionGroup[args.toInt()] = modelVersionGroup

	}

}