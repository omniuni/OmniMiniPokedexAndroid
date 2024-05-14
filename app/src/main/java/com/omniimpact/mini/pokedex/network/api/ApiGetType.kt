package com.omniimpact.mini.pokedex.network.api

import com.omniimpact.mini.pokedex.models.ModelDamageRelations
import com.omniimpact.mini.pokedex.models.ModelPokemonType
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object ApiGetType: ApiBase() {

	private const val URL = "https://pokeapi.co/api/v2/type/"

	private val mPokemonTypesMap: MutableMap<String, ModelPokemonType> = mutableMapOf()

	override fun getUrl(): String {
		return URL+args
	}

	override fun isLoaded(): Boolean {
		return mPokemonTypesMap.containsKey(args)
	}

	override fun getFriendlyName(): String {
		return "Type ($args)"
	}

	override fun parse(jsonResponse: String) {
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val typesAdapter = moshi.adapter(ModelPokemonType::class.java)
		typesAdapter.fromJson(jsonResponse)?.also {
			mPokemonTypesMap[it.name] = it
		}
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