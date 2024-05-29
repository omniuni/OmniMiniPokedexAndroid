package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelPokemonType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetType : ApiBase() {

	companion object {

		private const val URL = "https://pokeapi.co/api/v2/type/"
		private val mPokemonTypesMap: MutableMap<String, ModelPokemonType> = mutableMapOf()

		fun getPokemonTypeDetails(type: String): ModelPokemonType {
			mPokemonTypesMap[type]?.also {
				return it
			}
			return ModelPokemonType()
		}

	}

	override fun getBaseUrl(): String {
		return URL
	}

	override fun isLoaded(): Boolean {
		return mPokemonTypesMap.containsKey(getParameter())
	}

	override fun getFriendlyName(): String {
		return "Type (${getParameter()})"
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val typesAdapter = moshi.adapter(ModelPokemonType::class.java)
		typesAdapter.fromJson(jsonResponse)?.also {
			mPokemonTypesMap[it.name] = it
		}
	}

}