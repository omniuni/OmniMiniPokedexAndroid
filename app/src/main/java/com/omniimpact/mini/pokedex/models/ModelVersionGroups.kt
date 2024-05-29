package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelVersionGroups(
	val count: Int = -1,
	val results: List<VersionGroup> = listOf()
)

@JsonClass(generateAdapter = true)
data class VersionGroup(
	val name: String,
	val url: String
)