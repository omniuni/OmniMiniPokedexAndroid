package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokedexList(
	val count: Int = -1,
	val results: List<ModelPokedexEntry> = listOf()
)

@JsonClass(generateAdapter = true)
data class ModelPokedexEntry(
	val name: String = String(),
	val url: String = String()
)