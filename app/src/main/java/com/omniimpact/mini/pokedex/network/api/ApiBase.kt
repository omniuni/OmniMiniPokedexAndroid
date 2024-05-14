package com.omniimpact.mini.pokedex.network.api

import android.content.Context
import com.omniimpact.mini.pokedex.network.UtilityCachingGetRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class ApiBase : IApi{

	protected var args: String = String()
	private val loadScope = CoroutineScope(Job() + Dispatchers.IO)

	override fun presetArgs(args: String) {
		this.args = args
	}

	override fun load(context: Context, callback: IOnApiLoadProgress, key: String) {
		args = key

		if(isLoaded()){
			callback.onSuccess(this)
			return
		}

		loadScope.launch {
			try {
				val jsonResult: String = UtilityCachingGetRequest(context, getUrl()).get()
				parse(jsonResult)
				launch(Dispatchers.Main) {
					callback.onSuccess(this@ApiBase)
				}
			} catch (exception: Exception) {
				exception.printStackTrace()
				launch(Dispatchers.Main) {
					callback.onFailed(this@ApiBase)
				}
			}
		}

	}

}