package com.omniimpact.template.mini.utilities

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.omniimpact.template.mini.R

class UtilityFragmentManager {

	companion object {

		fun using(fragmentManager: FragmentManager): UtilityFragmentManager {
			return UtilityFragmentManager(fragmentManager)
		}

	}

	private val mFragmentManager: FragmentManager
	private lateinit var mFragment: Fragment
	private var mArguments: Bundle = Bundle()
	private var mAnimationsMap: Map<View, String> = mapOf()
	private var mTargetId: Int = 0

	@Suppress("ConvertSecondaryConstructorToPrimary")
	constructor(fragmentManager: FragmentManager) {
		mFragmentManager = fragmentManager
	}

	fun load(fragment: Fragment): UtilityFragmentManager {
		mFragment = fragment
		return this
	}

	fun with(arguments: Bundle, animationsMap: Map<View, String> = mapOf()): UtilityFragmentManager {
		mArguments = arguments
		mAnimationsMap = animationsMap
		return this
	}

	fun into(parentViewGroup: View): UtilityFragmentManager {
		if (!this::mFragment.isInitialized || parentViewGroup !is ViewGroup) {
			throw RuntimeException()
		}
		mTargetId = parentViewGroup.id
		return this
	}

	fun now(addToBackStack: Boolean = true, backStackKey: String = String()) {
		mFragment.arguments = mArguments
		val transaction = mFragmentManager.beginTransaction()
		mAnimationsMap.forEach { item ->
			var fromKey = item.key.hashCode().toString()
			item.key.tag?.toString()?.also {
				if (it.isNotEmpty()) fromKey = it
			}
			ViewCompat.setTransitionName(item.key, fromKey)
			transaction.addSharedElement(item.key, item.value)
		}
		transaction.replace(
			mTargetId,
			mFragment,
			String()
		)
		transaction.setCustomAnimations(
			R.anim.fade_in, //enter
			R.anim.fade_out, // exit
			R.anim.fade_in, // pop enter
			R.anim.fade_out, // pop exit
		)
		if (addToBackStack) {
			transaction.addToBackStack(backStackKey)
		}
		transaction.commit()
	}

}