package com.omniimpact.mini.pokedex.network.api

import android.content.Context

interface IApi {

	// Load Actions
	fun with(context: Context): IApi
	fun load(parameter: String = String()): IApi
	fun calling(callback: IOnApiLoad): IApi
	fun now() // return true/false on success or failure

	// Repository-lite

	fun isLoaded(): Boolean
	fun getUrl(): String
	fun getUrlHash(): Int

	// Other
	fun getFriendlyName(): String

	// Internal
	fun parse(jsonResponse: String)

}

interface IOnApiComplete{
	fun onComplete()

}

interface IOnApiLoad {

	fun onSuccess(success: IApi)
	fun onFailed(failure: IApi)

}

interface IOnApiLoadProgress: IOnApiComplete {
	fun onApiProgress(apiCallName: String, batchTotal: Int, batchComplete: Int)
}

interface IOnApiLoadQueue: IOnApiComplete, IOnApiLoad{}