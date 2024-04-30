package com.omniimpact.template.mini.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.omniimpact.template.mini.databinding.ActivityMainFragmentHomeBinding

class FragmentHome: Fragment() {

    private lateinit var mFragmentViewBinding: ActivityMainFragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentViewBinding = ActivityMainFragmentHomeBinding.inflate(layoutInflater)
        return mFragmentViewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentViewBinding.idBtnExit.setOnClickListener {
            requireActivity().finish()
        }
    }

}