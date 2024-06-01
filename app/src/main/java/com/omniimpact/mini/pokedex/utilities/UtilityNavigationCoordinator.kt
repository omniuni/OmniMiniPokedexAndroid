package com.omniimpact.mini.pokedex.utilities

import android.os.Bundle
import androidx.fragment.app.Fragment

class UtilityNavigationCoordinator {

	companion object {

		private val mNavigationHandlersMap: MutableMap<Int, INavigationHandler> = mutableMapOf()

		fun registerToHandleNavigatoin(priority: Int, handler: INavigationHandler){
			mNavigationHandlersMap[priority] = handler
		}

		fun deregisterHandlingNavigation(handler: INavigationHandler){
			val itemsToRemove = mNavigationHandlersMap.filter { it.value == handler }
			itemsToRemove.forEach {
				mNavigationHandlersMap.remove(it.key)
			}
		}

		fun requestNavigation(fragment: Fragment, bundle: Bundle = Bundle()){
			if(mNavigationHandlersMap.isEmpty()) return
			mNavigationHandlersMap.keys.minOf { it }.also { highestPriorityKey ->
				try {
					mNavigationHandlersMap[highestPriorityKey]?.onNavigationRequested(fragment, bundle)
				} catch (e: Exception) {
					mNavigationHandlersMap.remove(highestPriorityKey)
					requestNavigation(fragment)
				}
			}
		}

	}

	interface INavigationHandler{
		fun onNavigationRequested(fragment: Fragment, bundle: Bundle = Bundle())
	}
}