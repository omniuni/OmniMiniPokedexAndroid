package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelVersion
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ApiGetVersion : ApiBase() {

	private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/version/"

	private var mVersions: MutableMap<Int, ModelVersion> = mutableMapOf()

	override fun getUrl(): String {
		return URL_ENDPOINT + args
	}

	override fun getFriendlyName(): String {
		return "Version ($args)"
	}

	override fun isLoaded(): Boolean {
		return mVersions.containsKey(args.toInt())
	}

	override fun parse(jsonResponse: String) {

		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val adapter = moshi.adapter(ModelVersion::class.java)
		val modelVersion: ModelVersion = adapter.fromJson(jsonResponse) ?: ModelVersion(0)
		mVersions[args.toInt()] = modelVersion

	}

}