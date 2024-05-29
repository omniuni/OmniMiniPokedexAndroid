package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokedex(
	val count: Int = -1,
	val name: String = String(),
	val names: List<PokedexName> = listOf(),
	@Json(name = "pokemon_entries")
	val pokemonEntries: List<PokedexPokemonEntry> = listOf()
)

@JsonClass(generateAdapter = true)
data class PokedexName(
	val language: PokedexLanguage,
	val name: String = String()
)

@JsonClass(generateAdapter = true)
data class PokedexLanguage(
	val name: String = String(),
	val url: String = String()
)

@JsonClass(generateAdapter = true)
data class PokedexPokemonEntry(
	@Json(name = "entry_number")
	val entryNumber: Int = -1,
	@Json(name = "pokemon_species")
	val pokemonSpecies: PokedexPokemonSpecies = PokedexPokemonSpecies(),
)

@JsonClass(generateAdapter = true)
data class PokedexPokemonSpecies(
	val name: String = String(),
	val url: String = String()
)