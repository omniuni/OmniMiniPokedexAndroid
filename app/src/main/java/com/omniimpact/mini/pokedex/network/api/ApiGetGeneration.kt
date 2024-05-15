package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelGeneration
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetGeneration : ApiBase() {

	companion object {

		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/generation/"
		private var mMapGenerations: MutableMap<String, ModelGeneration> = mutableMapOf()

		fun getGenerationByName(name: String): ModelGeneration {
			return mMapGenerations[name] ?: ModelGeneration()
		}

		fun getGenerationNameInEnglish(modelGeneration: ModelGeneration): String{
			var name = String()
			for(genName in modelGeneration.names){
				if(genName.language.name == "en"){
					name = genName.name
				}
			}
			return name
		}

	}

	override fun getUrl(): String {
		return URL_ENDPOINT
	}

	override fun getFriendlyName(): String {
		return "Generation (${getParameter()})"
	}

	override fun isLoaded(): Boolean {
		return mMapGenerations.containsKey(getParameter())
	}

	override fun parse(jsonResponse: String) {

		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonListAdapter = moshi.adapter(ModelGeneration::class.java)
		val modelGeneration = pokemonListAdapter.fromJson(jsonResponse) ?: ModelGeneration()
		mMapGenerations[modelGeneration.name] = modelGeneration

	}



}