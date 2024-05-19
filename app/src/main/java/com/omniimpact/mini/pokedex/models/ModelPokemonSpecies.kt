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
	val names: List<ModelPokemonSpeciesNameEntry> = listOf()
)

@JsonClass(generateAdapter = true)
data class ModelPokemonSpeciesNameEntry(
	val name: String = String(),
	val language: ModelPokemonSpeciesFlavorTextEntryLanguage = ModelPokemonSpeciesFlavorTextEntryLanguage()
)

@JsonClass(generateAdapter = true)
data class ModelPokemonSpeciesFlavorTextEntry(
	@Json(name = "flavor_text")
	val flavorText: String = String(),
	val language: ModelPokemonSpeciesFlavorTextEntryLanguage = ModelPokemonSpeciesFlavorTextEntryLanguage(),
	val version: ModelPokemonSpeciesFlavorTextEntryVersion = ModelPokemonSpeciesFlavorTextEntryVersion()
)

@JsonClass(generateAdapter = true)
data class ModelPokemonSpeciesFlavorTextEntryLanguage(
	val name: String = String()
)

@JsonClass(generateAdapter = true)
data class ModelPokemonSpeciesFlavorTextEntryVersion(
	val name: String = String()
)

@JsonClass(generateAdapter = true)
data class ModelPokemonSpeciesEvolutionChain(
	val url: String = String()
)