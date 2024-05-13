package com.omniimpact.mini.pokedex.network

import android.annotation.SuppressLint
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

	private val mToLoad: MutableMap<IApi, String> = mutableMapOf()
	private val mListeners: MutableList<IOnApiLoadQueue> = mutableListOf()

	fun attachOutputUi(
		container: View,
		text: TextView
	){
		mContainer = container
		mText = text
		updateComplete()
	}

	fun enqueue(list: Map<IApi, String>){
		list.forEach { apiEntry ->
			mToLoad[apiEntry.key] = apiEntry.value
		}
		continueLoading()
	}

	fun registerListener(listener: IOnApiLoadQueue){
		if(!mListeners.contains(listener)) mListeners.add(listener)
	}

	fun removeListener(listener: IOnApiLoadQueue){
		if(mListeners.contains(listener)) mListeners.remove(listener)
	}

	private fun continueLoading(){
		if(mToLoad.keys.isEmpty()){
			updateComplete()
			return
		}
		val toLoadApi = mToLoad.keys.first()
		val toLoadString = mToLoad[toLoadApi] ?: String()
		try {
			if (this::mText.isInitialized) mText.text =
				mText.context.getString(R.string.loading, toLoadApi.getFriendlyName())
			if (this::mContainer.isInitialized) mContainer.visibility = View.VISIBLE
		} catch (_: Exception) {}
		toLoadApi.load(this, toLoadString)
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
		mToLoad.remove(success)
		mListeners.forEach {
			it.onSuccess(success)
		}
		continueLoading()
	}

	override fun onFailed(failure: IApi) {
		mToLoad.remove(failure)
		mListeners.forEach {
			it.onFailed(failure)
		}
		continueLoading()
	}

}