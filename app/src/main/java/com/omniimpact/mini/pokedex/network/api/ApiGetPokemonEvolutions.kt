package com.omniimpact.mini.pokedex.network.api
//
//import com.omniimpact.mini.pokedex.models.ModelPokemonEvolution
//import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChain
//import com.omniimpact.mini.pokedex.models.ModelPokemonEvolutionChainEvolutionSpecies
//import com.squareup.moshi.Moshi
//import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
//
//object ApiGetPokemonEvolutions: ApiBase() {
//
//	private const val URL_POKEMON_EVOLUTIONS = "https://pokeapi.co/api/v2/evolution-chain/"
//
//	private val mPokemonEvolutionsMap: MutableMap<Int, ModelPokemonEvolutionChain> = mutableMapOf()
//
//	override fun getUrl(): String {
//		return URL_POKEMON_EVOLUTIONS+args
//	}
//
//	override fun isLoaded(): Boolean {
//		return mPokemonEvolutionsMap.containsKey(args.toInt())
//	}
//
//	override fun getFriendlyName(): String {
//		return "Evolutions"
//	}
//
//	override fun parse(jsonResponse: String) {
//		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
//		val pokemonListAdapter = moshi.adapter(ModelPokemonEvolution::class.java)
//		pokemonListAdapter.fromJson(jsonResponse)?.also {
//			mPokemonEvolutionsMap[args.toInt()] = it.chain
//		}
//	}
//
//	fun getPokemonEvolutionChain(evolutionId: Int): ModelPokemonEvolutionChain{
//		mPokemonEvolutionsMap[evolutionId]?.also {
//			return it
//		}
//		return ModelPokemonEvolutionChain(
//			id = 0,
//			species = ModelPokemonEvolutionChainEvolutionSpecies(
//				name = String(),
//				url = String(),
//				iconUrl = String()
//			),
//			evolutionDetails = listOf(),
//			evolvesTo = listOf()
//		)
//	}
//
//}