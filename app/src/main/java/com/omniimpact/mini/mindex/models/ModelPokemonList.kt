package com.omniimpact.mini.mindex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokemonList(
	val results: List<ModelPokemonListItem>
)