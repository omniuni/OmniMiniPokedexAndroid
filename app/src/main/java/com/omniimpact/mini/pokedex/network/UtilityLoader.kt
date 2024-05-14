package com.omniimpact.mini.pokedex.network

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import android.widget.TextView
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadProgress
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue

@SuppressLint("StaticFieldLeak")
object UtilityLoader: IOnApiLoadProgress {

	private lateinit var mContainer: View
	private lateinit var mText: TextView

	private val mToLoad: MutableMap<String, Pair<IApi, String>> = mutableMapOf()
	private val mListeners: MutableList<IOnApiLoadQueue> = mutableListOf()

	private var mLoadInProgress = false

	fun attachOutputUi(
		container: View,
		text: TextView
	){
		mContainer = container
		mText = text
		updateComplete()
	}

	fun enqueue(map: Map<IApi, String>){
		map.forEach { apiEntry ->
			val id = apiEntry.key.getUrl()+apiEntry.value
			Log.d(UtilityLoader::class.simpleName, "Check ID: $id")
			if(mToLoad.containsKey(id)) return
			Log.d(UtilityLoader::class.simpleName, "Enqueue ID: $id")
			mToLoad[id] = Pair(apiEntry.key, apiEntry.value)
		}
		if(!mLoadInProgress) continueLoading()
	}

	fun registerListener(listener: IOnApiLoadQueue){
		if(!mListeners.contains(listener)) mListeners.add(listener)
	}

	fun removeListener(listener: IOnApiLoadQueue){
		if(mListeners.contains(listener)) mListeners.remove(listener)
	}

	private fun continueLoading(){
		mLoadInProgress = true
		if(mToLoad.keys.isEmpty()){
			updateComplete()
			mLoadInProgress = false
			return
		}
		val toLoadPairKey = mToLoad.keys.first()
		Log.d(UtilityLoader::class.simpleName, "Loading: $toLoadPairKey")
		val toLoadPair = mToLoad[toLoadPairKey] ?: return
		toLoadPair.first.load(this, toLoadPair.second)
		try {
			if (this::mText.isInitialized) mText.text =
				mText.context.getString(R.string.loading, toLoadPair.first.getFriendlyName())
			if (this::mContainer.isInitialized) mContainer.visibility = View.VISIBLE
		} catch (_: Exception) {}
	}

	private fun updateComplete(){
		try {
			if (this::mText.isInitialized) mText.text = String()
			if (this::mContainer.isInitialized) mContainer.visibility = View.GONE
		} catch (_: Exception) {}
		mListeners.forEach {
			try {
				it.onComplete()
			} catch (_: Exception){
				mListeners.remove(it)
			}
		}
	}

	override fun onSuccess(success: IApi) {
		Log.d(UtilityLoader::class.simpleName, "Remove: ${success.getUrl()}")
		mToLoad.remove(success.getUrl())
		Log.d(UtilityLoader::class.simpleName, "Success: ${success.getUrl()}")
		mListeners.forEach {
			it.onSuccess(success)
		}
		continueLoading()
	}

	override fun onFailed(failure: IApi) {
		Log.d(UtilityLoader::class.simpleName, "Remove: ${failure.getUrl()}")
		mToLoad.remove(failure.getUrl())
		Log.d(UtilityLoader::class.simpleName, "Failure: ${failure.getUrl()}")
		mListeners.forEach {
			it.onFailed(failure)
		}
		continueLoading()
	}

}