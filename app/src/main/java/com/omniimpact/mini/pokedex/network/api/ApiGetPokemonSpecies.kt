package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelPokemonSpecies
import com.omniimpact.mini.pokedex.models.ModelPokemonSpeciesFlavorTextEntry
import com.omniimpact.mini.pokedex.models.ModelPokemonSpeciesNameEntry
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class ApiGetPokemonSpecies : ApiBase() {

	companion object {

		private const val URL_POKEMON_SPECIES = "https://pokeapi.co/api/v2/pokemon-species/"
		private val mPokemonSpeciesMap: MutableMap<String, ModelPokemonSpecies> = mutableMapOf()

		fun getPokemonSpecies(speciesName: String): ModelPokemonSpecies {
			mPokemonSpeciesMap[speciesName]?.also {
				return it
			}
			return ModelPokemonSpecies()
		}

		fun getPokemonName(species: ModelPokemonSpecies): String{
			val nameEntry: ModelPokemonSpeciesNameEntry =  species.names.find { it.language.name == "en" } ?: ModelPokemonSpeciesNameEntry()
			return nameEntry.name
		}

		fun getPokemonFlavorText(pokemonName: String, versionName: String): String{
			val pokemonSpecies = mPokemonSpeciesMap[pokemonName] ?: return String()
			var lastFlavorTextEntry = ModelPokemonSpeciesFlavorTextEntry()
			var preferredFlavorTextEntry = ModelPokemonSpeciesFlavorTextEntry()
			pokemonSpecies.flavorTextEntries.forEach { flavorTextEntry ->
				if (flavorTextEntry.language.name.equals("en", ignoreCase = true)) {
					lastFlavorTextEntry = flavorTextEntry
				}
				if(flavorTextEntry.version.name.equals(versionName, ignoreCase = true)){
					preferredFlavorTextEntry = flavorTextEntry
				}
			}
			val flavorText = preferredFlavorTextEntry.flavorText.ifEmpty {
				lastFlavorTextEntry.flavorText
			}
			return flavorText.replace("\\s+".toRegex(), " ")
		}

	}


	override fun getBaseUrl(): String {
		return URL_POKEMON_SPECIES
	}

	override fun isLoaded(): Boolean {
		return mPokemonSpeciesMap.containsKey(getParameter())
	}

	override fun getFriendlyName(): String {
		return "Species"
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonSpeciesAdapter = moshi.adapter(ModelPokemonSpecies::class.java)
		pokemonSpeciesAdapter.fromJson(jsonResponse)?.also { species ->
			mPokemonSpeciesMap[getParameter()] = species
		}
	}

}