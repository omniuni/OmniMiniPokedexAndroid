package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelVersion(
	val id: Int = -1,
	val name: String = String(),
	val names: List<VersionName> = listOf()
)

@JsonClass(generateAdapter = true)
data class VersionName(
	val language: VersionLanguage,
	val name: String = String()
)

@JsonClass(generateAdapter = true)
data class VersionLanguage(
	val name: String
)