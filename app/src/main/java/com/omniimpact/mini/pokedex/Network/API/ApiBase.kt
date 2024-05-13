package com.omniimpact.mini.pokedex.network.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.URL

abstract class ApiBase : IApi{

	private val loadScope = CoroutineScope(Job() + Dispatchers.IO)

	override fun load(callback: IOnApiLoadProgress, key: String) {

		if(isLoaded()){
			callback.onSuccess(this)
			return
		}

		loadScope.launch {
			try {
				val jsonResult = URL(getUrl()).readText()
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