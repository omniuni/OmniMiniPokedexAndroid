package com.omniimpact.mini.pokedex.utilities.view

import android.content.res.ColorStateList
import android.content.res.Configuration
import android.view.View
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.omniimpact.mini.pokedex.R
import com.squareup.picasso.Callback

class PicassoTintOnLoad : Callback {

	private val mGroundView: ImageView
	private val mImageView: ImageView

	@Suppress("ConvertSecondaryConstructorToPrimary")
	constructor(groundView: ImageView, imageView: ImageView) {
		mGroundView = groundView
		mImageView = imageView
	}

	override fun onSuccess() {
		Palette.from(mImageView.drawable.toBitmap()).generate { palette ->
			palette?.let {
				val context = mImageView.context
				val color: Int =
					when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
						Configuration.UI_MODE_NIGHT_YES -> {
							it.getDarkMutedColor(mImageView.context.getColor(R.color.md_theme_surfaceContainer))
						}

						Configuration.UI_MODE_NIGHT_NO -> {
							it.getLightMutedColor(mImageView.context.getColor(R.color.md_theme_surfaceContainer))
						}

						else -> {
							it.getMutedColor(mImageView.context.getColor(R.color.md_theme_surfaceContainer))
						}
					}
				mGroundView.imageTintList = ColorStateList.valueOf(color)
				mGroundView.visibility = View.VISIBLE
			}
		}
	}

	override fun onError(e: Exception?) {
		e?.printStackTrace()
	}

}