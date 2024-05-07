package com.omniimpact.template.mini.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokemonEvolution(
	val chain: ModelPokemonEvolutionChain
)

@JsonClass(generateAdapter = true)
data class ModelPokemonEvolutionChain(
	@Transient
	val id: Int = -1,
	val species: ModelPokemonEvolutionChainEvolutionSpecies,
	@Json(name = "evolution_details")
	val evolutionDetails: List<ModelPokemonEvolutionChainEvolutionDetails>,
	@Json(name = "evolves_to")
	val evolvesTo: List<ModelPokemonEvolutionChain>
)

@JsonClass(generateAdapter = true)
data class ModelPokemonEvolutionChainEvolutionDetails(
	@Json(name = "min_level")
	val minLevel: Int
)

@JsonClass(generateAdapter = true)
data class ModelPokemonEvolutionChainEvolutionSpecies(
	val url: String,
	val name: String,
	@Transient
	var iconUrl: String = String(),
)
