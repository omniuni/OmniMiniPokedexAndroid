package com.omniimpact.mini.pokedex.network

import android.content.Context
import com.omniimpact.mini.pokedex.network.api.IApi
import com.omniimpact.mini.pokedex.network.api.IOnApiLoad
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadProgress
import com.omniimpact.mini.pokedex.network.api.IOnApiLoadQueue

object UtilityLoader : IOnApiLoad {

	private val mToLoad: MutableMap<Int, IApi> = mutableMapOf()
	private val mListenersToProgress: MutableList<IOnApiLoadProgress> = mutableListOf()
	private val mListenersToApiCalls: MutableList<IOnApiLoadQueue> = mutableListOf()

	private var mQueueSessionTotal: Int = 0
	private var mQueueSessionCompleted: Int = 0

	fun registerProgressListener(listener: IOnApiLoadProgress) {
		if (!mListenersToProgress.contains(listener)) mListenersToProgress.add(listener)
	}

	fun deregisterProgressListener(listener: IOnApiLoadProgress) {
		if (mListenersToProgress.contains(listener)) mListenersToProgress.remove(listener)
	}

	fun registerApiCallListener(listener: IOnApiLoadQueue) {
		if (!mListenersToApiCalls.contains(listener)) mListenersToApiCalls.add(listener)
	}

	fun deregisterApiCallListener(listener: IOnApiLoadQueue) {
		if (mListenersToApiCalls.contains(listener)) mListenersToApiCalls.remove(listener)
	}

	fun addRequests(requestMap: Map<IApi, String>, context: Context) {
		val beginProcessingQueue = mQueueSessionTotal == 0
		requestMap.forEach { (api, parameter) ->
			val apiRequest = api.with(context).load(parameter)
			mToLoad[apiRequest.getUrlHash()] = apiRequest
			mQueueSessionTotal++
		}
		if (beginProcessingQueue) processNextItem()
	}

	private fun processNextItem() {


		// Pull the next item to process
		val nextToProcess: IApi = mToLoad.entries.first().value
		notifyProgressListeners(nextToProcess.getFriendlyName())
		nextToProcess.calling(this).now()

	}

	private fun notifyProgressListeners(description: String) {
		mListenersToProgress.forEach {
			it.onApiProgress(description, mQueueSessionTotal, mQueueSessionCompleted)
		}
	}

	override fun onSuccess(success: IApi) {
		mListenersToApiCalls.forEach {
			it.onSuccess(success)
		}
		mToLoad.remove(success.getUrlHash())
		checkIfComplete()
	}

	override fun onFailed(failure: IApi) {
		mListenersToApiCalls.forEach {
			it.onFailed(failure)
		}
		mToLoad.remove(failure.getUrlHash())
		checkIfComplete()
	}

	private fun checkIfComplete() {
		mQueueSessionCompleted++
		// If we have emptied the queue, notify and reset
		if (mToLoad.isEmpty()) {
			mListenersToProgress.forEach {
				it.onComplete()
			}
			mListenersToApiCalls.forEach {
				it.onComplete()
			}
			mQueueSessionTotal = 0
			mQueueSessionCompleted = 0
			return
		}
		processNextItem()
	}

}