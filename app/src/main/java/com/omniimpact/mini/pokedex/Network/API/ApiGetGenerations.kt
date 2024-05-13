package com.omniimpact.mini.pokedex.network.api

object ApiGetGenerations: ApiBase() {

	override fun getUrl(): String {
		return "https://pokeapi.co/api/v2/generation/"
	}

	override fun isLoaded(): Boolean {
		TODO("Not yet implemented")
	}

	override fun getFriendlyName(): String {
		return "Generations"
	}

	override fun parse(jsonResponse: String) {
		TODO("Not yet implemented")
	}


}