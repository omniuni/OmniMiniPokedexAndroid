package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokemonListItem(
	val name: String,
	val url: String,
	@Transient
	var id: Int = 0,
	@Transient
	var iconUrl: String = String()
)
