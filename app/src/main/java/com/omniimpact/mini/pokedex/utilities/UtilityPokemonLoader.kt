package com.omniimpact.mini.pokedex.utilities

import com.omniimpact.mini.pokedex.models.ModelDamageRelations
import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolution
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChain
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChainEvolutionSpecies
import com.omniimpact.mini.pokedex.models.ModelPokemonSpecies
import com.omniimpact.mini.pokedex.models.ModelPokemonSpeciesEvolutionChain
import com.omniimpact.mini.pokedex.models.ModelPokemonType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.FileNotFoundException
import java.net.URL

object UtilityPokemonLoader {

	private val loadScope = CoroutineScope(Job() + Dispatchers.IO)

	private const val URL_POKEMON_SPECIES = "https://pokeapi.co/api/v2/pokemon-species/"
	private const val URL_POKEMON_EVOLUTIONS = "https://pokeapi.co/api/v2/evolution-chain/"

	private val mPokemonSpeciesMap: MutableMap<Int, ModelPokemonSpecies> = mutableMapOf()
	private val mPokemonEvolutionsMap: MutableMap<Int, ModelPokemonEvolutionChain> = mutableMapOf()



	interface IOnDetails {
		fun onDetailsReady()
		fun onTypeDetailsReady()
	}

	interface IOnSpecies {
		fun onSpeciesReady()
		fun onSpeciesFailed()
	}

	interface IOnEvolutionChain {
		fun onEvolutionChainReady()
	}

	fun loadSpecies(caller: IOnSpecies, pokemonId: Int){

		if(mPokemonSpeciesMap.containsKey(pokemonId)){
			caller.onSpeciesReady()
		}

		loadScope.launch {
			val jsonResult: String
			try {
				jsonResult = URL(URL_POKEMON_SPECIES + pokemonId).readText()
			} catch (e: FileNotFoundException) {
				launch(Dispatchers.Main) {
					caller.onSpeciesFailed()
				}
				return@launch
			}
			val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
			val pokemonSpeciesAdapter = moshi.adapter(ModelPokemonSpecies::class.java)
			pokemonSpeciesAdapter.fromJson(jsonResult)?.also { species ->

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
				mPokemonSpeciesMap[pokemonId] = species
				launch(Dispatchers.Main) {
					caller.onSpeciesReady()
				}
			}

		}

	}


	fun loadEvolution(caller: IOnEvolutionChain, evolutionId: Int){

		if(mPokemonEvolutionsMap.containsKey(evolutionId)){
			caller.onEvolutionChainReady()
		}

		loadScope.launch {
			val jsonResult = URL(URL_POKEMON_EVOLUTIONS+evolutionId).readText()

			val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
			val pokemonListAdapter = moshi.adapter(ModelPokemonEvolution::class.java)
			pokemonListAdapter.fromJson(jsonResult)?.also {
				mPokemonEvolutionsMap[evolutionId] = it.chain
				launch(Dispatchers.Main) {
					caller.onEvolutionChainReady()
				}
			}

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

	fun getPokemonEvolutionChain(evolutionId: Int): ModelPokemonEvolutionChain{
		mPokemonEvolutionsMap[evolutionId]?.also {
			return it
		}
		return ModelPokemonEvolutionChain(
			id = 0,
			species = ModelPokemonEvolutionChainEvolutionSpecies(
				name = String(),
				url = String(),
				iconUrl = String()
			),
			evolutionDetails = listOf(),
			evolvesTo = listOf()
		)
	}

}