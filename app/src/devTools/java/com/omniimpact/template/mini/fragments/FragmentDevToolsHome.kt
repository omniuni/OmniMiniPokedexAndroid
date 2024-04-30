package com.omniimpact.template.mini.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omniimpact.template.mini.BuildConfig
import com.omniimpact.template.mini.databinding.ActivityMainDevFragmentHomeBinding

class FragmentDevToolsHome: Fragment() {

    private lateinit var mFragmentViewBinding: ActivityMainDevFragmentHomeBinding
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
    }

}