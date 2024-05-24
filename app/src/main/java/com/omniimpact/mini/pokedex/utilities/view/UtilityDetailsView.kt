package com.omniimpact.mini.pokedex.utilities.view

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.omniimpact.mini.pokedex.R
import com.omniimpact.mini.pokedex.databinding.ListItemTypeLabelHeaderBinding
import com.omniimpact.mini.pokedex.databinding.ListItemTypeSwChipBinding
import com.omniimpact.mini.pokedex.models.ModelPokemonDetailsType
import com.omniimpact.mini.pokedex.models.ModelPokemonDetailsTypes
import com.omniimpact.mini.pokedex.network.api.ApiGetType
import com.omniimpact.mini.pokedex.utilities.AdapterRecyclerViewTypeChips
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class UtilityDetailsView {

	companion object {

		fun getColorStateListFromTypeName(context: Context, name: String): ColorStateList {
			return when (name) {
				"normal" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_normal))
				"fire" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_fire))
				"water" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_water))
				"electric" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_electric))
				"grass" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_grass))
				"ice" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_ice))
				"fighting" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_fighting))
				"poison" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_poison))
				"ground" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_ground))
				"flying" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_flying))
				"psychic" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_psychic))
				"bug" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_bug))
				"rock" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_rock))
				"ghost" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_ghost))
				"dragon" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_dragon))
				"dark" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_dark))
				"steel" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_steel))
				"fairy" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_fairy))
				"stellar" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_stellar))
				"shadow" -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_shadow))
				else -> ColorStateList.valueOf(ContextCompat.getColor(context, R.color.type_unknown))
			}
		}

		fun populateTypeChips(
			context: Context,
			layoutInflater: LayoutInflater,
			fromType: ModelPokemonDetailsTypes,
			types: List<ModelPokemonDetailsType>,
			target: LinearLayout,
			drawableResource: Int,
			headerResource: Int
		) {
			val loadScope = CoroutineScope(Job() + Dispatchers.IO)
			if (target.childCount == 0) {
				val typeChipHeader = ListItemTypeLabelHeaderBinding.inflate(
					layoutInflater,
					target,
					true
				)
				typeChipHeader.idTvTitle.setText(headerResource)
				typeChipHeader.root.id = View.generateViewId()
			}
			if (types.isEmpty()) {
				val typeChip = ListItemTypeSwChipBinding.inflate(
					layoutInflater,
					target,
					true
				)
				typeChip.idCvTypeChip.backgroundTintList = getColorStateListFromTypeName(context, String())
				typeChip.idCvTypeContributing.backgroundTintList =
					getColorStateListFromTypeName(context, fromType.type.name)
				typeChip.idTvType.text = context.resources.getString(R.string.label_none)
				typeChip.idIvIcon.setImageResource(drawableResource)
				typeChip.root.id = View.generateViewId()
				return
			}
			loadScope.launch {
				types.forEach { type ->
					val typeChip = ListItemTypeSwChipBinding.inflate(
						layoutInflater,
						target,
						false
					)
					typeChip.idCvTypeChip.backgroundTintList = getColorStateListFromTypeName(context, type.name)
					typeChip.idCvTypeContributing.backgroundTintList =
						getColorStateListFromTypeName(context, fromType.type.name)
					typeChip.idTvType.text = type.name
					typeChip.idIvIcon.setImageResource(drawableResource)
					typeChip.root.id = View.generateViewId()
					val animation = AlphaAnimation(0f, 1f)
					animation.duration = 1000
					typeChip.root.animation = animation
					loadScope.launch(Dispatchers.Main) {
						target.addView(typeChip.root)
						typeChip.root.animate()
					}
				}
			}
		}

		fun setDetailsAdapter(
			context: Context,
			fromTypes: List<ModelPokemonDetailsTypes>,
			target: RecyclerView,
			headerResource: Int,
			drawableResource: Int,
			typeMap: MutableMap<Int, ArrayList<ModelPokemonDetailsType>>
		){

			if(target.adapter != null) return

			val loadScope = CoroutineScope(Job() + Dispatchers.IO)

			if(target.layoutManager == null) target.layoutManager = LinearLayoutManager(context)

			loadScope.launch {
				if(!typeMap.contains(headerResource)) {
					val typeArray: ArrayList<ModelPokemonDetailsType> = arrayListOf()
					for (typeSlot in fromTypes) {
						val typeDetails = ApiGetType.getPokemonTypeDetails(typeSlot.type.name)
						val typesList = when(headerResource){
							R.string.label_double_damage -> typeDetails.damageRelations.doubleDamageTo
							R.string.label_full_defense -> typeDetails.damageRelations.noDamageFrom
							R.string.label_double_defense -> typeDetails.damageRelations.halfDamageFrom
							R.string.label_half_damage -> typeDetails.damageRelations.halfDamageTo
							R.string.label_no_damage -> typeDetails.damageRelations.noDamageTo
							R.string.label_half_defense -> typeDetails.damageRelations.doubleDamageFrom
							else -> listOf()
						}
						if(typesList.isNotEmpty()) {
							for (type in typesList) {
								typeArray.add(
									ModelPokemonDetailsType(
										name = type.name,
										url = type.url,
										fromName = typeSlot.type.name
									)
								)
							}
						} else {
							typeArray.add(
								ModelPokemonDetailsType(
									name = context.getString(R.string.label_none),
									url = String(),
									fromName = typeSlot.type.name
								)
							)
						}
					}
					typeMap[headerResource] = typeArray
				}
				typeMap[headerResource]?.also {
					loadScope.launch(Dispatchers.Main){
						target.adapter = AdapterRecyclerViewTypeChips(
							context.getString(headerResource),
							it,
							drawableResource
						)
					}
				}
			}
		}


	}


}