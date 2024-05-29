package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelVersionGroup(
	val id: Int = -1,
	val name: String = String(),
	val generation: VersionGroupGeneration = VersionGroupGeneration(),
	val order: Int = -1,
	val versions: List<VersionGroupVersion> = listOf(),
	val pokedexes: List<VersionGroupPokedex> = listOf()
)

@JsonClass(generateAdapter = true)
data class VersionGroupGeneration(
	val name: String = String(),
	val url: String = String()
)

@JsonClass(generateAdapter = true)
data class VersionGroupVersion(
	val name: String = String(),
	val url: String = String()
)

@JsonClass(generateAdapter = true)
data class VersionGroupPokedex(
	val name: String = String(),
	val url: String = String()
)