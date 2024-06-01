package com.omniimpact.mini.pokedex.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ModelEncounter(
	@Json(name = "location_area")
    val locationArea: ModelEncounterLocationArea = ModelEncounterLocationArea(),
	@Json(name = "version_details")
    val versionDetails: List<ModelEncounterVersionDetail> = listOf()
)

@JsonClass(generateAdapter = true)
data class ModelEncounterLocationArea(
    val name: String = String(),
    val url: String = String()
)

@JsonClass(generateAdapter = true)
data class ModelEncounterVersionDetail(
	@Json(name = "encounter_details")
    val encounterDetails: List<ModelEncounterDetail> = listOf(),
	@Json(name = "max_chance")
    val maxChance: Int = 0,
    val version: ModelEncounterVersion = ModelEncounterVersion()
)

@JsonClass(generateAdapter = true)
data class ModelEncounterVersion(
	val name: String = String(),
    val url: String = String()
)

@JsonClass(generateAdapter = true)
data class ModelEncounterDetail(
    val chance: Int = 0,
	@Json(name = "min_level")
    val minLevel: Int = 0,
	@Json(name = "max_level")
    val maxLevel: Int = 0,
    val method: ModelEncounterMethod = ModelEncounterMethod()
)

@JsonClass(generateAdapter = true)
data class ModelEncounterMethod(
    val name: String = String(),
    val url: String = String()
)