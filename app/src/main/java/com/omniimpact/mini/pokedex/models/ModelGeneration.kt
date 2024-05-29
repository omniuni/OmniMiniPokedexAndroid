package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelGeneration(
	val id: Int = -1,
	val name: String = String(),
	val names: List<GenerationName> = listOf(),
	@Json(name = "pokemon_species")
	val pokemonSpecies: List<GenerationPokemonSpecies> = listOf(),
	@Json(name = "version_groups")
	val versionGroups: List<GenerationVersionGroup> = listOf(),
)

@JsonClass(generateAdapter = true)
data class GenerationPokemonSpecies(
	val name: String,
	val url: String,
	@Transient
	var id: Int = 0,
)

@JsonClass(generateAdapter = true)
data class GenerationVersionGroup(
	val name: String = String(),
	val url: String = String()
)

@JsonClass(generateAdapter = true)
data class GenerationName(
	val language: GenerationLanguage = GenerationLanguage(),
	val name: String = String()
)

@JsonClass(generateAdapter = true)
data class GenerationLanguage(
	val name: String = String()
)