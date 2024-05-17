package com.omniimpact.mini.pokedex.models

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
	val species: ModelPokemonEvolutionChainEvolutionSpecies = ModelPokemonEvolutionChainEvolutionSpecies(),
	@Json(name = "evolution_details")
	val evolutionDetails: List<ModelPokemonEvolutionChainEvolutionDetails> = listOf(),
	@Json(name = "evolves_to")
	val evolvesTo: List<ModelPokemonEvolutionChain> = listOf()
)

@JsonClass(generateAdapter = true)
data class ModelPokemonEvolutionChainEvolutionDetails(
	@Json(name = "min_level")
	val minLevel: Int? = 0
)

@JsonClass(generateAdapter = true)
data class ModelPokemonEvolutionChainEvolutionSpecies(
	val url: String = String(),
	val name: String = String(),
	@Transient
	var iconUrl: String = String(),
)
