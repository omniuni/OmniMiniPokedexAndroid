package com.omniimpact.mini.pokedex.utilities.view

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.FragmentDetailsBinding
import com.squareup.picasso.Callback

class OnPicassoImageLoadedDoEnterTransition : Callback {

	private val mContext: Context
	private val mFragmentViewBinding: FragmentDetailsBinding

	@Suppress("ConvertSecondaryConstructorToPrimary")
	constructor(context: Context, fragmentDetailsBinding: FragmentDetailsBinding) {
		mContext = context
		mFragmentViewBinding = fragmentDetailsBinding
	}

	override fun onSuccess() {
		val icon: ImageView = mFragmentViewBinding.idLlBanner.idIvPokemonIcon
		val platform: ImageView = mFragmentViewBinding.idLlBanner.idIvPlatform
		Palette.from(icon.drawable.toBitmap()).generate { palette ->
			palette?.let {
				val color: Int =
					when (mContext.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
						Configuration.UI_MODE_NIGHT_YES -> {
							it.getDarkMutedColor(mContext.getColor(R.color.md_theme_surfaceContainer))
						}

						Configuration.UI_MODE_NIGHT_NO -> {
							it.getLightMutedColor(mContext.getColor(R.color.md_theme_surfaceContainer))
						}

						else -> {
							it.getMutedColor(mContext.getColor(R.color.md_theme_surfaceContainer))
						}
					}
				platform.imageTintList = ColorStateList.valueOf(color)
				platform.visibility = View.VISIBLE
			}
		}
	}

	override fun onError(e: Exception?) {
	}

}