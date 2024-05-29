package com.omniimpact.mini.pokedex.network.api

import android.content.Context
import com.omniimpact.mini.pokedex.network.UtilityCachingGetRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

abstract class ApiBase : IApi {

	//region Variables

	private val loadScope = CoroutineScope(Job() + Dispatchers.IO)

	private lateinit var mContext: Context

	private var mParameter: String = String()
	private var mCallback: IOnApiLoad = object : IOnApiLoad {
		override fun onSuccess(success: IApi) {}
		override fun onFailed(failure: IApi) {}
	}

	//endregion

	//region Builder

	override fun with(context: Context): IApi {
		mContext = context
		return this
	}

	override fun load(parameter: String): IApi {
		mParameter = parameter
		return this
	}

	override fun calling(callback: IOnApiLoad): IApi {
		mCallback = callback
		return this
	}

	override fun now() {

		if (isLoaded()) {
			mCallback.onSuccess(this)
			return
		}

		loadScope.launch {
			try {
				val jsonResult: String = UtilityCachingGetRequest(mContext, getBaseUrl() + mParameter).get()
				parse(jsonResult)
				launch(Dispatchers.Main) {
					mCallback.onSuccess(this@ApiBase)
				}
			} catch (exception: Exception) {
				exception.printStackTrace()
				launch(Dispatchers.Main) {
					mCallback.onFailed(this@ApiBase)
				}
			}
		}

	}

	//endregion

	//region Utility

	protected fun getParameter(): String {
		return mParameter
	}

	override fun getUrlHash(): Int {
		return "${getBaseUrl()}$mParameter".hashCode()
	}

	//endregion

}