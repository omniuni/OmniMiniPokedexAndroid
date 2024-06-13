package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelEncounter
import com.omniimpact.mini.pokedex.models.ModelEncounterLocationArea
import com.omniimpact.mini.pokedex.models.ModelEncounterVersionDetail
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.lang.reflect.ParameterizedType

class ApiGetEncounters : ApiBase() {

	companion object {

		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/pokemon/"
		private const val URL_ENDPOINT_SUFFIX = "/encounters"
		private var mEncounters: MutableMap<String, List<ModelEncounter>> = mutableMapOf()

		fun getSimplifiedEncountersForPokemon(pokemonName: String, version: String): List<Pair<ModelEncounterLocationArea, ModelEncounterVersionDetail>>{
			val encounters = mEncounters[pokemonName] ?: listOf()
			val simplifiedEncounters: MutableList<Pair<ModelEncounterLocationArea, ModelEncounterVersionDetail>> = mutableListOf()
			encounters.forEach { modelEncounter: ModelEncounter ->
				modelEncounter.versionDetails.forEach {
					if(version.contains(it.version.name, ignoreCase = true)){
						simplifiedEncounters.add(Pair(modelEncounter.locationArea, it))
					}
				}
			}
			return simplifiedEncounters
		}

	}

	override fun getBaseUrl(): String {
		return URL_ENDPOINT
	}

	override fun getUrlSuffix(): String {
		return URL_ENDPOINT_SUFFIX
	}

	override fun getFriendlyName(): String {
		return "Version Group (${getParameter()})"
	}

	override fun isLoaded(): Boolean {
		return mEncounters.containsKey(getParameter())
	}

	override fun parse(jsonResponse: String) {

		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val payloadType: ParameterizedType = Types.newParameterizedType(List::class.java, ModelEncounter::class.java)
		val pokemonListAdapter: JsonAdapter<List<ModelEncounter>> = moshi.adapter(payloadType)
		val encounters = pokemonListAdapter.fromJson(jsonResponse) ?: listOf()
		mEncounters[getParameter()] = encounters

	}

}