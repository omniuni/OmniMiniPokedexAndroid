package com.omniimpact.template.mini.utilities

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.omniimpact.template.mini.databinding.ListItemPokemonBinding
import com.omniimpact.template.mini.models.ModelPokemonListItem
import com.squareup.picasso.Picasso

class AdapterRecyclerViewPokemonList: RecyclerView.Adapter<AdapterRecyclerViewPokemonList.ViewHolder>() {

    private lateinit var mItemBinding: ListItemPokemonBinding
    private var mFilteredItems: List<ModelPokemonListItem> = listOf()

    fun updateItems(filter: String = String()){
        if(filter.isEmpty()){
            mFilteredItems = UtilityPokemonLoader.getLoadedPokemonList().results
        }
    }

    inner class ViewHolder(viewBinding: ListItemPokemonBinding): RecyclerView.ViewHolder(viewBinding.root){
        var tvName: TextView = viewBinding.idTvPokemonName
        var ivIcon: ImageView = viewBinding.idIvPokemonIcon
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
        Picasso.get().load(item.iconUrl).fit().centerInside().into(holder.ivIcon)
    }

}