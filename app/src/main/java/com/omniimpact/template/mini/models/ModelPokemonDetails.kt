package com.omniimpact.template.mini.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokemonDetails(
	val id: Int,
	val types: List<ModelPokemonDetailsTypes>
)

@JsonClass(generateAdapter = true)
data class ModelPokemonDetailsTypes(
	val slot: Int,
	val type: ModelPokemonDetailsType
)

@JsonClass(generateAdapter = true)
data class ModelPokemonDetailsType(
	val name: String
)