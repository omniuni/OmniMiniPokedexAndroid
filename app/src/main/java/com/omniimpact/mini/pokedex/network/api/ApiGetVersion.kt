package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelVersion
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetVersion : ApiBase() {

	companion object {

		private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/version/"
		private var mVersions: MutableMap<String, ModelVersion> = mutableMapOf()

		fun getVersionByName(name: String): ModelVersion {
			return mVersions[name] ?: ModelVersion()
		}

		fun getVersionNameInEnglish(version: ModelVersion): String {
			var versionNameEn = String()
			version.names.forEach {
				if (it.language.name == "en") versionNameEn = it.name
			}
			return versionNameEn
		}

	}

	override fun getBaseUrl(): String {
		return URL_ENDPOINT
	}

	override fun getFriendlyName(): String {
		return "Version (${getParameter()})"
	}

	override fun isLoaded(): Boolean {
		return mVersions.containsKey(getParameter())
	}

	override fun parse(jsonResponse: String) {

		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val adapter = moshi.adapter(ModelVersion::class.java)
		val modelVersion: ModelVersion = adapter.fromJson(jsonResponse) ?: ModelVersion(0)



		mVersions[modelVersion.name] = modelVersion

	}


}