package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelVersions(
	val count: Int = 0,
	val results: List<VersionResult> = listOf()
)

@JsonClass(generateAdapter = true)
data class VersionResult(
	val name: String = String(),
	val url: String = String()
)