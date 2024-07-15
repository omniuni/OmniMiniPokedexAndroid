package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelLocationArea(
    val name: String = String(),
    val names: List<ModelLocationName> = listOf()
)

@JsonClass(generateAdapter = true)
data class ModelLocationName(
    val language: ModelLocationNameLanguage = ModelLocationNameLanguage(),
    val name: String = String()
)

@JsonClass(generateAdapter = true)
data class ModelLocationNameLanguage(
    val name: String = String()
)