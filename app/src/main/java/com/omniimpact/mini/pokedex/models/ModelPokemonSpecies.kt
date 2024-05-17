package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokemonSpecies(
	val id: Int = -1,
	@Json(name = "evolution_chain")
	val evolutionChain: ModelPokemonSpeciesEvolutionChain = ModelPokemonSpeciesEvolutionChain(),
	@Json(name = "flavor_text_entries")
	val flavorTextEntries: List<ModelPokemonSpeciesFlavorTextEntry> = listOf(),
	@Transient
	var defaultFlavorText: String = String()
)

@JsonClass(generateAdapter = true)
data class ModelPokemonSpeciesFlavorTextEntry(
	@Json(name = "flavor_text")
	val flavorText: String,
	val language: ModelPokemonSpeciesFlavorTextEntryLanguage
)

@JsonClass(generateAdapter = true)
data class ModelPokemonSpeciesFlavorTextEntryLanguage(
	val name: String
)

@JsonClass(generateAdapter = true)
data class ModelPokemonSpeciesEvolutionChain(
	@Transient
	var id: Int = -1,
	val url: String = String()
)