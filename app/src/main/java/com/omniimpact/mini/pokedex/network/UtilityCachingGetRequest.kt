package com.omniimpact.mini.pokedex.network

import android.content.Context
import android.util.Log
import java.io.File
import java.net.URL

class UtilityCachingGetRequest {

	private val mContext: Context
	private val mUrl: String

	@Suppress("ConvertSecondaryConstructorToPrimary")
	constructor(context: Context, url: String){
		mContext = context
		mUrl = url
	}

	fun get(): String{
		val fileName = "${mUrl.hashCode()}.cache"
		val file = File(mContext.filesDir, fileName)
		val weekAgoInMillis = System.currentTimeMillis()-(7*24*60*60*1000)
		if(file.exists()){
			Log.d(UtilityCachingGetRequest::class.simpleName, "Found file...")
			if(file.lastModified() > weekAgoInMillis){
				Log.d(UtilityCachingGetRequest::class.simpleName, "Returning request from cache!")
				return file.readText()
			} else {
				Log.d(UtilityCachingGetRequest::class.simpleName, "The cached request is too old.")
			}
		}
		val contents = URL(mUrl).readText()
		file.writeText(contents)
		Log.d(UtilityCachingGetRequest::class.simpleName, "Wrote $fileName to local storage!")
		return contents
	}

}