package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelSettings(
	var lastOpened: Long = -1,
	var strings: List<ModelSettingsString> = listOf()
)

@JsonClass(generateAdapter = true)
data class ModelSettingsString(
	val key: String = String(),
	val value: String = String()
)