package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokemonDetails(
	val id: Int = -1,
	val name: String = String(),
	val types: List<ModelPokemonDetailsTypes> = listOf(),
	val stats: List<ModelPokemonDetailsStats> = listOf()
)

@JsonClass(generateAdapter = true)
data class ModelPokemonDetailsTypes(
	val slot: Int,
	val type: ModelPokemonDetailsType
)

@JsonClass(generateAdapter = true)
data class ModelPokemonDetailsStats(
	@Json(name = "base_stat")
	val baseStat: Int = 0,
	val effort: Int = 0,
	val stat: ModelPokemonDetailsStat
)

@JsonClass(generateAdapter = true)
data class ModelPokemonDetailsStat(
	val name: String = String(),
	val url: String = String()
)

@JsonClass(generateAdapter = true)
data class ModelPokemonDetailsType(
	var name: String = String(),
	var url: String = String(),
	@Transient
	var fromName: String = String(),
)