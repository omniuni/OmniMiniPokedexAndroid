package com.omniimpact.mini.pokedex.network.api

interface IApi {

	fun getUrl(): String
	fun isLoaded(): Boolean
	fun load(callback: IOnApiLoadProgress, key: String = String())
	fun getFriendlyName(): String

	fun parse(jsonResponse: String)

}

interface IOnApiLoadProgress {

	fun onSuccess(success: IApi)
	fun onFailed(failure: IApi)

}

interface IOnApiLoadQueue: IOnApiLoadProgress{

	fun onComplete()

}