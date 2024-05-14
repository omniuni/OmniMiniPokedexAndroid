package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelPokemonType(
	@Json(name = "damage_relations")
	val damageRelations: ModelDamageRelations,
	val name: String = String()
)

@JsonClass(generateAdapter = true)
data class ModelDamageRelations(
	@Json(name = "double_damage_from")
	val doubleDamageFrom: List<ModelPokemonDetailsType>,
	@Json(name = "double_damage_to")
	val doubleDamageTo: List<ModelPokemonDetailsType>,
	@Json(name = "half_damage_from")
	val halfDamageFrom: List<ModelPokemonDetailsType>,
	@Json(name = "half_damage_to")
	val halfDamageTo: List<ModelPokemonDetailsType>,
	@Json(name = "no_damage_from")
	val noDamageFrom: List<ModelPokemonDetailsType>,
	@Json(name = "no_damage_to")
	val noDamageTo: List<ModelPokemonDetailsType>,
)