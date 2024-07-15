package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelLocationArea
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetLocationArea : ApiBase() {

	companion object {

		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/location-area/"
		private var mLocationAreas: MutableMap<String, ModelLocationArea> = mutableMapOf()

		fun getEnglishNameByKey(name: String): String {
			return mLocationAreas[name]?.names.also { modelLocationNames ->
				modelLocationNames?.find { it.language.name == "en" }
			}?.first()?.name ?: String()
		}

	}

	override fun getBaseUrl(): String {
		return URL_ENDPOINT
	}

	override fun getFriendlyName(): String {
		return "Location (${getParameter()})"
	}

	override fun isLoaded(): Boolean {
		return mLocationAreas.containsKey(getParameter())
	}

	override fun parse(jsonResponse: String) {

		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val adapter = moshi.adapter(ModelLocationArea::class.java)
		val modelVersion: ModelLocationArea = adapter.fromJson(jsonResponse) ?: ModelLocationArea()

		mLocationAreas[modelVersion.name] = modelVersion

	}


}