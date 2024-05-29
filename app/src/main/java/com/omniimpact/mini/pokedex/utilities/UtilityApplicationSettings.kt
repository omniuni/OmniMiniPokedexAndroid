package com.omniimpact.mini.pokedex.utilities

import android.content.Context
import com.omniimpact.mini.pokedex.models.ModelSettings
import com.omniimpact.mini.pokedex.models.ModelSettingsString
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.File

object UtilityApplicationSettings {

	private const val FILE_NAME_SETTINGS = "settings.json"

	const val KEY_STRING_SELECTED_VERSION = "KEY_STRING_SELECTED_VERSION"

	private var settings: ModelSettings = ModelSettings()

	private fun loadIfNecessary(context: Context){
		if(settings.lastOpened < 0){
			val file = File(context.filesDir, FILE_NAME_SETTINGS)
			if(!file.exists()) return
			val fileContents = file.readText()
			val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
			val adapter = moshi.adapter(ModelSettings::class.java)
			adapter.fromJson(fileContents)?.also { settings = it }
		}
		settings.lastOpened = System.currentTimeMillis()
	}

	fun getString(context: Context, key: String, default: String): String{
		loadIfNecessary(context)
		return settings.strings.find { it.key == key }?.value ?: default
	}

	fun putString(context: Context, key: String, value: String){
		loadIfNecessary(context)
		val existingString: ModelSettingsString =
			settings.strings.find { it.key == key } ?: ModelSettingsString()
		val newStrings = ArrayList<ModelSettingsString>(settings.strings)
		newStrings.remove(existingString)
		newStrings.add(
			ModelSettingsString(
				key = key,
				value = value
			)
		)
		settings.strings = newStrings
		saveSettings(context)
	}

	private fun saveSettings(context: Context){
		val moshi: Moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
		val adapter = moshi.adapter(ModelSettings::class.java)
		val settingsJson = adapter.toJson(settings)
		val file = File(context.filesDir, FILE_NAME_SETTINGS)
		file.writeText(settingsJson)
	}

}