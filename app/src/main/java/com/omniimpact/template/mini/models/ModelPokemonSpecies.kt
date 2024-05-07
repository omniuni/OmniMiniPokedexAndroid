package com.omniimpact.template.mini.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokemonSpecies(
	val id: Int,
    @Json(name = "evolution_chain")
	val evolutionChain: ModelPokemonSpeciesEvolutionChain,
)

@JsonClass(generateAdapter = true)
data class ModelPokemonSpeciesEvolutionChain(
	@Transient
    var id: Int = -1,
    val url: String
)