package com.omniimpact.mini.pokedex.utilities

import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolution
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChain
import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChainEvolutionSpecies
import com.omniimpact.mini.pokedex.models.ModelPokemonList
import com.omniimpact.mini.pokedex.models.ModelPokemonListItem
import com.omniimpact.mini.pokedex.models.ModelPokemonSpecies
import com.omniimpact.mini.pokedex.models.ModelPokemonSpeciesEvolutionChain
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
	const val URL_POKEMON_SPRITES_BASE =
		"https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" // add [index].png
	private const val URL_POKEMON_LIST = "https://pokeapi.co/api/v2/pokemon?offset=0&limit=3000"
	private const val URL_POKEMON_SPECIES = "https://pokeapi.co/api/v2/pokemon-species/"
	private const val URL_POKEMON_EVOLUTIONS = "https://pokeapi.co/api/v2/evolution-chain/"
	private const val URL_POKEMON_DETAILS = "https://pokeapi.co/api/v2/pokemon/"
	private lateinit var mPokemonList: ModelPokemonList
	private val mPokemonSpeciesMap: MutableMap<Int, ModelPokemonSpecies> = mutableMapOf()
	private val mPokemonEvolutionsMap: MutableMap<Int, ModelPokemonEvolutionChain> = mutableMapOf()
	private val mPokemonDetailsMap: MutableMap<Int, ModelPokemonDetails> = mutableMapOf()

	interface IOnLoad {
		fun onPokemonLoaded()
	}

	interface IOnDetails {
		fun onDetailsReady()
	}

	interface IOnSpecies {
		fun onSpeciesReady()
		fun onSpeciesFailed()
	}

	interface IOnEvolutionChain {
		fun onEvolutionChainReady()
	}

	fun load(caller: IOnLoad) {

		// No need to re-load if we already have it
		if (this::mPokemonList.isInitialized) {
			caller.onPokemonLoaded()
			return
		}

		loadScope.launch {
			val jsonResult = URL(URL_POKEMON_LIST).readText()

			val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
			val pokemonListAdapter = moshi.adapter(ModelPokemonList::class.java)
			mPokemonList = pokemonListAdapter.fromJson(jsonResult) ?: ModelPokemonList(arrayListOf())

			// For some reason, they don't give us the id or icon, so we're going to need to do this ourselves
			for (pokemon: ModelPokemonListItem in mPokemonList.results) {
				val id: Int =
					pokemon.url.takeLastWhile { it.isDigit() || it == '/' }.filter { it.isDigit() }.toInt()
				pokemon.id = id
				pokemon.iconUrl = "$URL_POKEMON_SPRITES_BASE$id.png"
			}
			launch(Dispatchers.Main) {
				caller.onPokemonLoaded()
			}
		}

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

	fun loadDetails(caller: IOnDetails, pokemonId: Int){

		if(mPokemonDetailsMap.containsKey(pokemonId)){
			caller.onDetailsReady()
		}

		loadScope.launch {
			val jsonResult = URL(URL_POKEMON_DETAILS+pokemonId).readText()
			val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
			val pokemonDetailsAdapter = moshi.adapter(ModelPokemonDetails::class.java)
			pokemonDetailsAdapter.fromJson(jsonResult)?.also { pokemonDetails ->


				mPokemonDetailsMap[pokemonId] = pokemonDetails
				launch(Dispatchers.Main) {
					caller.onDetailsReady()
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

	fun getLoadedPokemonList(): ModelPokemonList {
		if (!this::mPokemonList.isInitialized) {
			return ModelPokemonList(arrayListOf())
		}
		return mPokemonList
	}

	fun getPokemonListItemById(id: Int): ModelPokemonListItem {
		return mPokemonList.results.first {
			it.id == id
		}
	}

	fun getLoadedPokemonListCount(): Int {
		return mPokemonList.results.size
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

	fun getPokemonDetails(pokemonId: Int): ModelPokemonDetails{
		mPokemonDetailsMap[pokemonId]?.also {
			return it
		}
		return ModelPokemonDetails(
			id = 0,
			types = listOf()
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