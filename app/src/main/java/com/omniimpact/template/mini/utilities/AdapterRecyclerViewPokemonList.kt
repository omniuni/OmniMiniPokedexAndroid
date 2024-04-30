package com.omniimpact.template.mini.utilities

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.omniimpact.template.mini.R
import com.omniimpact.template.mini.databinding.ListItemPokemonBinding
import com.omniimpact.template.mini.models.ModelPokemonListItem
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class AdapterRecyclerViewPokemonList: RecyclerView.Adapter<AdapterRecyclerViewPokemonList.ViewHolder>() {

    interface IOnUpdate{
        fun onListUpdated()
    }

    private lateinit var mItemBinding: ListItemPokemonBinding
    private var mFilter = String()
    private var mFilteredItems: List<ModelPokemonListItem> = listOf()
    private var mShouldSortAlphabetical: Boolean = false
    private var mUpdateCallback: IOnUpdate = object : IOnUpdate{ override fun onListUpdated() {} }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(){
        mFilteredItems = if(mFilter.isEmpty()){
            UtilityPokemonLoader.getLoadedPokemonList().results
        } else {
            UtilityPokemonLoader.getLoadedPokemonList().results.filter {
                modelPokemonListItem -> modelPokemonListItem.name.contains(mFilter, true)
            }
        }
        if(mFilteredItems.isNotEmpty() && mShouldSortAlphabetical){
            mFilteredItems = mFilteredItems.sortedWith(compareBy { it.name })
        }
        notifyDataSetChanged()
        mUpdateCallback.onListUpdated()
    }

    fun sortItemsAlphabetical(shouldSortAlphabetical: Boolean = false){
        mShouldSortAlphabetical = shouldSortAlphabetical
        updateItems()
    }

    fun setUpdateCallback(callback: IOnUpdate){
        mUpdateCallback = callback
    }

    fun setFilter(filter: String){
        mFilter = filter
        updateItems()
    }

    fun getFilteredItemCount(): Int {
        return  mFilteredItems.size
    }

    inner class ViewHolder(viewBinding: ListItemPokemonBinding): RecyclerView.ViewHolder(viewBinding.root){
        var tvName: TextView = viewBinding.idTvPokemonName
        var ivIcon: ImageView = viewBinding.idIvPokemonIcon
        var cvBackground: CardView = viewBinding.idCvBackground
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mItemBinding = ListItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(mItemBinding)
    }

    override fun getItemCount(): Int {
        return mFilteredItems.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mFilteredItems[position]
        holder.tvName.text = item.name.replaceFirstChar { it.titlecase() }
        holder.cvBackground.backgroundTintList = ColorStateList.valueOf(holder.cvBackground.context.getColor(R.color.md_theme_surfaceContainer))
        Picasso.get().load(item.iconUrl).fit().centerInside().into(holder.ivIcon, PicassoTintOnLoad(holder.cvBackground, holder.ivIcon))
    }

    inner class PicassoTintOnLoad : Callback {

        private val mCardView: CardView
        private val mImageView: ImageView

        @Suppress("ConvertSecondaryConstructorToPrimary")
        constructor(cardView: CardView, imageView: ImageView){
            mCardView = cardView
            mImageView = imageView
        }

        override fun onSuccess() {
            Palette.from(mImageView.drawable.toBitmap()).generate { palette ->
                palette?.let {
                    val context = mImageView.context
                    val color: Int = when (context.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                        Configuration.UI_MODE_NIGHT_YES -> {it.getDarkMutedColor(mImageView.context.getColor(R.color.md_theme_surfaceContainer))}
                        Configuration.UI_MODE_NIGHT_NO -> {it.getLightMutedColor(mImageView.context.getColor(R.color.md_theme_surfaceContainer))}
                        else -> {it.getMutedColor(mImageView.context.getColor(R.color.md_theme_surfaceContainer))}
                    }
                    mCardView.backgroundTintList = ColorStateList.valueOf(color)
                }
            }
        }

        override fun onError(e: Exception?) {
            e?.printStackTrace()
        }

    }

}