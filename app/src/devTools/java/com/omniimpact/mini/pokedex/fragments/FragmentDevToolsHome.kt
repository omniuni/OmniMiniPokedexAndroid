package com.omniimpact.mini.pokedex.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omniimpact.mini.pokedex.BuildConfig
import com.omniimpact.mini.pokedex.databinding.ActivityMainDevFragmentHomeBinding
import java.io.File

class FragmentDevToolsHome : Fragment() {

	private lateinit var directory: File
	private val cacheFiles: ArrayList<File> = arrayListOf()

	private lateinit var mFragmentViewBinding: ActivityMainDevFragmentHomeBinding

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		directory = File(requireContext().filesDir.toURI())
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		mFragmentViewBinding = ActivityMainDevFragmentHomeBinding.inflate(layoutInflater)
		return mFragmentViewBinding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		mFragmentViewBinding.idTvDtVersionCode.text = BuildConfig.VERSION_CODE.toString()
		mFragmentViewBinding.idTvDtVersionName.text = BuildConfig.VERSION_NAME

		updateFiles()
		mFragmentViewBinding.idBtnClear.setOnClickListener {
			for (file in cacheFiles) {
				file.delete()
			}
			updateFiles()
		}

	}

	private fun updateFiles() {
		cacheFiles.clear()
		directory.listFiles()?.forEach { file ->
			if (file.name.endsWith("cache")) {
				cacheFiles.add(file)
			}
		}
		mFragmentViewBinding.idTvNumFiles.text = "${cacheFiles.size} files in cache."
	}

}