package com.omniimpact.mini.pokedex.utilities

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.ListItemPokemonBinding
import com.omniimpact.mini.pokedex.models.PokedexPokemonEntry
import com.omniimpact.mini.pokedex.network.api.ApiGetPokedex
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class AdapterRecyclerViewPokemonList() :
	RecyclerView.Adapter<AdapterRecyclerViewPokemonList.ViewHolder>() {


	constructor(
		pokemonSpeciesMap: MutableMap<Int, PokedexPokemonEntry>,
		versionGroupName: String
	) : this() {
		mVersionGroupName = versionGroupName
		mSpeciesMap = pokemonSpeciesMap
	}

	interface IOnListActions {
		fun onListUpdated()
		fun onItemClicked(item: PokedexPokemonEntry, imageView: ImageView, textView: TextView)
	}

	private var mVersionGroupName: String = String()
	private var mSpeciesMap: MutableMap<Int, PokedexPokemonEntry> = mutableMapOf()
	private var mSpeciesMapFiltered: Array<PokedexPokemonEntry> = arrayOf()
	private lateinit var mItemBinding: ListItemPokemonBinding
	private var mFilter = String()
	private var mShouldSortAlphabetical: Boolean = false
	private lateinit var mUpdateCallback: IOnListActions

	@SuppressLint("NotifyDataSetChanged")
	fun updateItems() {
		mSpeciesMapFiltered = if (mFilter.isEmpty()) {
			mSpeciesMap.values.toTypedArray()
		} else {
			mSpeciesMap.filter {
				it.value.pokemonSpecies.name.contains(mFilter, ignoreCase = true)
			}.values.toTypedArray()
		}
		if (mSpeciesMapFiltered.isNotEmpty() && mShouldSortAlphabetical) {
			mSpeciesMapFiltered =
				mSpeciesMapFiltered.sortedWith(compareBy { it.pokemonSpecies.name }).toTypedArray()
		}
		notifyDataSetChanged()
		if (this::mUpdateCallback.isInitialized) mUpdateCallback.onListUpdated()
	}

	fun sortItemsAlphabetical(shouldSortAlphabetical: Boolean = false) {
		mShouldSortAlphabetical = shouldSortAlphabetical
		updateItems()
	}

	fun setUpdateCallback(callback: IOnListActions) {
		mUpdateCallback = callback
	}

	fun setFilter(filter: String) {
		mFilter = filter
		updateItems()
	}

	fun getFilteredItemCount(): Int {
		return mSpeciesMapFiltered.size
	}

	inner class ViewHolder(viewBinding: ListItemPokemonBinding) :
		RecyclerView.ViewHolder(viewBinding.root) {
		var tvName: TextView = viewBinding.idTvPokemonName
		var ivIcon: ImageView = viewBinding.idIvPokemonIcon
		var cvBackground: CardView = viewBinding.idCvBackground
		var ivPlatform: ImageView = viewBinding.idIvPokemonGround
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		mItemBinding =
			ListItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
		return ViewHolder(mItemBinding)
	}

	override fun getItemCount(): Int {
		return mSpeciesMapFiltered.size
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val item: PokedexPokemonEntry = mSpeciesMapFiltered[position]
		val pokemonId = ApiGetPokedex.getPokemonIdFromUrl(item.pokemonSpecies.url)
		val iconUrl = ApiGetPokedex.getImageUrlFromPokemonId(pokemonId)
		Log.d(AdapterRecyclerViewPokemonList::class.simpleName, "IconURL: $iconUrl")
		holder.tvName.text = item.pokemonSpecies.name.replaceFirstChar { it.titlecase() }
		holder.tvName.tag = item.pokemonSpecies.name
		holder.cvBackground.backgroundTintList =
			ColorStateList.valueOf(holder.cvBackground.context.getColor(R.color.md_theme_surfaceContainer))
		Picasso.get().load(iconUrl).fit().centerInside()
			.into(holder.ivIcon, PicassoTintOnLoad(holder.ivPlatform, holder.ivIcon))
		holder.ivIcon.tag = iconUrl
		holder.cvBackground.setOnClickListener {
			mUpdateCallback.onItemClicked(item, holder.ivIcon, holder.tvName)
		}
		holder.ivPlatform.visibility = View.INVISIBLE
	}

	inner class PicassoTintOnLoad : Callback {

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

}