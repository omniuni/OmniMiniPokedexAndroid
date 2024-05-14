package com.omniimpact.mini.pokedex.network.api

import android.content.Context

interface IApi {

	fun presetArgs(args: String)
	fun getUrl(): String
	fun isLoaded(): Boolean
	fun load(context: Context, callback: IOnApiLoadProgress, key: String = String())
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