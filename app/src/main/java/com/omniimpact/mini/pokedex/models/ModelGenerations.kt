package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelGenerations(
	val count: Int = -1,
	val results: List<Generation> = listOf()
)

@JsonClass(generateAdapter = true)
data class Generation(
	val name: String,
	val url: String
)