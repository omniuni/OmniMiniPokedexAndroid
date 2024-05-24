package com.omniimpact.mini.pokedex.utilities

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.omniimpact.mini.pokedex.databinding.ListItemTypeLabelHeaderBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeSwChipBinding
import com.omniimpact.mini.pokedex.models.ModelPokemonDetailsType
import com.omniimpact.mini.pokedex.utilities.view.UtilityDetailsView

class AdapterRecyclerViewTypeChips() :
	RecyclerView.Adapter<ViewHolder>() {

	companion object {
		const val ITEM_VIEW_TYPE_HEADER = 0
		const val ITEM_VIEW_TYPE_CHIP = 1
	}

	constructor(
		header: String,
		types: ArrayList<ModelPokemonDetailsType>,
		icon: Int
	) : this() {
		mHeader = header
		mTypes = types
		mIcon = icon
	}

	private var mHeader: String = String()
	private var mTypes: ArrayList<ModelPokemonDetailsType> = arrayListOf()
	private var mIcon: Int = 0
	private lateinit var mItemBinding: ListItemTypeSwChipBinding

	inner class ViewHolderChip(viewBinding: ListItemTypeSwChipBinding) :
		RecyclerView.ViewHolder(viewBinding.root) {
		var tvName: TextView = viewBinding.idTvType
		var cvContributing: CardView = viewBinding.idCvTypeContributing
		var cvBackground: CardView = viewBinding.idCvTypeChip
		var ivIcon: ImageView = viewBinding.idIvIcon
	}

	inner class ViewHolderHeader(viewBinding: ListItemTypeLabelHeaderBinding) :
		RecyclerView.ViewHolder(viewBinding.root) {
		var tvTitle: TextView = viewBinding.idTvTitle
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		when (viewType) {
			ITEM_VIEW_TYPE_HEADER -> return ViewHolderHeader(
				ListItemTypeLabelHeaderBinding.inflate(
					LayoutInflater.from(parent.context),
					parent,
					false
				)
			)
			else -> return ViewHolderChip(
				ListItemTypeSwChipBinding.inflate(
					LayoutInflater.from(parent.context),
					parent,
					false
				)
			)
		}
	}

	override fun getItemCount(): Int {
		return mTypes.size+1
	}

	override fun getItemViewType(position: Int): Int {
		return if(position == 0) {
			ITEM_VIEW_TYPE_HEADER
		} else {
			ITEM_VIEW_TYPE_CHIP
		}
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		when(holder.itemViewType) {
			ITEM_VIEW_TYPE_HEADER -> {
				(holder as ViewHolderHeader).also {
					holder.tvTitle.text = mHeader
				}
			}
			ITEM_VIEW_TYPE_CHIP -> {
				(holder as ViewHolderChip).also {
					val typeItem: ModelPokemonDetailsType = mTypes[position-1]
					val context = holder.cvBackground.context
					val colorFrom =
						UtilityDetailsView.getColorStateListFromTypeName(context, typeItem.fromName)
					val color = UtilityDetailsView.getColorStateListFromTypeName(context, typeItem.name)
					holder.cvBackground.backgroundTintList = color
					holder.cvContributing . backgroundTintList = colorFrom
					holder.tvName.text = typeItem.name
					holder.ivIcon.setImageResource (mIcon)
				}
			}
		}

	}


}