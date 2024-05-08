package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokemonList(
	val results: List<ModelPokemonListItem>
)