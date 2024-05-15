package com.omniimpact.mini.pokedex.network.api
//
//import com.omniimpact.mini.pokedex.models.ModelVersionGroups
//import com.omniimpact.mini.pokedex.models.VersionGroup
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//
//object ApiGetVersionGroups: ApiBase() {
//
//	private const val URL_ENDPOINT = "https://pokeapi.co/api/v2/version-group/?limit=40"
//
//	private lateinit var mVersionGroups: ModelVersionGroups
//
//	override fun getUrl(): String {
//		return URL_ENDPOINT
//	}
//
//	override fun getFriendlyName(): String {
//		return "All Version Groups"
//	}
//
//	override fun isLoaded(): Boolean {
//		return this::mVersionGroups.isInitialized
//	}
//
//	override fun parse(jsonResponse: String) {
//
//			val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
//			val pokemonListAdapter = moshi.adapter(ModelVersionGroups::class.java)
//			mVersionGroups = pokemonListAdapter.fromJson(jsonResponse) ?: ModelVersionGroups()
//
//	}
//
//	fun getVersionGroups(): List<VersionGroup>{
//		if(this::mVersionGroups.isInitialized) return mVersionGroups.results
//		return listOf()
//	}
//
//}