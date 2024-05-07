package com.omniimpact.template.mini.utilities

import android.view.Display.Mode
import com.omniimpact.template.mini.models.ModelPokemonEvolution
import com.omniimpact.template.mini.models.ModelPokemonEvolutionChain
import com.omniimpact.template.mini.models.ModelPokemonEvolutionChainEvolutionDetails
import com.omniimpact.template.mini.models.ModelPokemonEvolutionChainEvolutionSpecies
import com.omniimpact.template.mini.models.ModelPokemonList
import com.omniimpact.template.mini.models.ModelPokemonListItem
import com.omniimpact.template.mini.models.ModelPokemonSpecies
import com.omniimpact.template.mini.models.ModelPokemonSpeciesEvolutionChain
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL

object UtilityPokemonLoader {

	private val loadScope = CoroutineScope(Job() + Dispatchers.IO)
	const val URL_POKEMON_SPRITES_BASE =
		"https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/" // add [index].png
	private const val URL_POKEMON_LIST = "https://pokeapi.co/api/v2/pokemon?offset=0&limit=3000"
	private const val URL_POKEMON_SPECIES = "https://pokeapi.co/api/v2/pokemon-species/"
	private const val URL_POKEMON_EVOLUTIONS = "https://pokeapi.co/api/v2/evolution-chain/"
	private lateinit var mPokemonList: ModelPokemonList
	private val mPokemonSpeciesMap: MutableMap<Int, ModelPokemonSpecies> = mutableMapOf()
	private val mPokemonEvolutionsMap: MutableMap<Int, ModelPokemonEvolutionChain> = mutableMapOf()

	interface IOnLoad {
		fun onPokemonLoaded()
	}

	interface IOnSpecies {
		fun onSpeciesReady()
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
			val jsonResult = URL(URL_POKEMON_SPECIES+pokemonId).readText()

			val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
			val pokemonSpeciesAdapter = moshi.adapter(ModelPokemonSpecies::class.java)
			pokemonSpeciesAdapter.fromJson(jsonResult)?.also { species ->
				species.evolutionChain.also { evolutionChain ->
					evolutionChain.id = evolutionChain.url.takeLastWhile { it.isDigit() || it == '/' }.filter { it.isDigit() }.toInt()
				}
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
			)
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