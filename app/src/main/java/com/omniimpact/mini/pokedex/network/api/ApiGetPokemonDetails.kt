package com.omniimpact.mini.pokedex.network.api

import android.annotation.SuppressLint
import com.omniimpact.mini.pokedex.models.ModelDamageRelations
import com.omniimpact.mini.pokedex.models.ModelPokemonDetails
import com.omniimpact.mini.pokedex.models.ModelPokemonType
import com.omniimpact.mini.pokedex.network.UtilityCachingGetRequest
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

@SuppressLint("StaticFieldLeak")
object ApiGetPokemonDetails: ApiBase() {

	private const val URL_POKEMON_DETAILS = "https://pokeapi.co/api/v2/pokemon/"

	private val mPokemonDetailsMap: MutableMap<Int, ModelPokemonDetails> = mutableMapOf()
	private val mPokemonTypesMap: MutableMap<String, ModelPokemonType> = mutableMapOf()

	override fun getUrl(): String {
		return URL_POKEMON_DETAILS+args
	}

	override fun isLoaded(): Boolean {
		return mPokemonDetailsMap.containsKey(args.toInt())
	}

	override fun getFriendlyName(): String {
		return "Pokemon Details"
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val pokemonDetailsAdapter = moshi.adapter(ModelPokemonDetails::class.java)
		pokemonDetailsAdapter.fromJson(jsonResponse)?.also { pokemonDetails ->
			mPokemonDetailsMap[args.toInt()] = pokemonDetails
		}

		mPokemonDetailsMap[args.toInt()]?.types?.forEach { typeSlot ->
			val typeName: String = typeSlot.type.name
			if(!mPokemonTypesMap.containsKey(typeName)){
				val typesResult = UtilityCachingGetRequest(mContext, typeSlot.type.url).get()
				val pokemonTypesAdapter = moshi.adapter(ModelPokemonType::class.java)
				pokemonTypesAdapter.fromJson(typesResult)?.also {
					mPokemonTypesMap[typeName] = it
				}
			}
		}
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

	fun getPokemonTypeDetails(type: String): ModelPokemonType{
		mPokemonTypesMap[type]?.also {
			return it
		}
		return ModelPokemonType(
			damageRelations = ModelDamageRelations(
				doubleDamageFrom = listOf(),
				doubleDamageTo = listOf(),
				halfDamageFrom = listOf(),
				halfDamageTo = listOf(),
				noDamageFrom = listOf(),
				noDamageTo = listOf()
			)
		)
	}

}