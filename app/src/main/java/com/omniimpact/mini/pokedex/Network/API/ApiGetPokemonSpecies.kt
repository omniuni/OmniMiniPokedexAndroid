package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelPokemonSpecies
import com.omniimpact.mini.pokedex.models.ModelPokemonSpeciesEvolutionChain
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ApiGetPokemonSpecies: ApiBase() {

	private const val URL_POKEMON_SPECIES = "https://pokeapi.co/api/v2/pokemon-species/"

	private val mPokemonSpeciesMap: MutableMap<Int, ModelPokemonSpecies> = mutableMapOf()

	override fun getUrl(): String {
		return URL_POKEMON_SPECIES+args
	}

	override fun isLoaded(): Boolean {
		return mPokemonSpeciesMap.containsKey(args.toInt())
	}

	override fun getFriendlyName(): String {
		return "Species"
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonSpeciesAdapter = moshi.adapter(ModelPokemonSpecies::class.java)
		pokemonSpeciesAdapter.fromJson(jsonResponse)?.also { species ->
			// Extract the evolution chain ID
			species.evolutionChain.also { evolutionChain ->
				evolutionChain.id = evolutionChain.url.takeLastWhile { it.isDigit() || it == '/' }.filter { it.isDigit() }.toInt()
			}
			// Get the default flavor text (last English flavor text)
			species.flavorTextEntries.forEach { flavorTextEntry ->
				if(flavorTextEntry.language.name.equals("en", ignoreCase = true)){
					species.defaultFlavorText = flavorTextEntry.flavorText
				}
			}
			species.defaultFlavorText = species.defaultFlavorText.replace("\\s+".toRegex(), " ")
			// Cache it
			mPokemonSpeciesMap[args.toInt()] = species
		}
	}

	fun getPokemonSpecies(speciesId: Int): ModelPokemonSpecies{
		mPokemonSpeciesMap[speciesId]?.also {
			return it
		}
		return ModelPokemonSpecies(
			id = 0,
			evolutionChain = ModelPokemonSpeciesEvolutionChain(
				url = String()
			),
			flavorTextEntries = listOf()
		)
	}

}