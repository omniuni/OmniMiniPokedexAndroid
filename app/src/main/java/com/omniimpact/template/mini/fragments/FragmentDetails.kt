package com.omniimpact.template.mini.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.transition.TransitionInflater
import com.omniimpact.template.mini.R
import com.omniimpact.template.mini.databinding.FragmentDetailsBinding
import com.omniimpact.template.mini.models.ModelPokemonListItem
import com.omniimpact.template.mini.utilities.UtilityPokemonLoader
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class FragmentDetails: Fragment() {

    companion object {
        const val KEY_ID = "id"
        const val KEY_TRANSITION_TARGET_IMAGE_HEADER = "image_header"
        const val KEY_TRANSITION_TARGET_TEXT_HEADER = "text_header"
        const val KEY_TRANSITION_TARGET_BANNER = "banner"
    }

    private lateinit var mFragmentViewBinding: FragmentDetailsBinding
    private var mPokemonId: Int = 0
    private lateinit var mSourceItem: ModelPokemonListItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.also {
            if(it.keySet().contains(KEY_ID)){
                mPokemonId = it.getInt(KEY_ID)
            }
        }
        mSourceItem = UtilityPokemonLoader.getPokemonListItemById(mPokemonId)
        sharedElementEnterTransition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.transitions_standard)
        postponeEnterTransition()
        setHasOptionsMenu(true)
        (requireActivity() as AppCompatActivity).supportActionBar?.also{
            it.setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                parentFragmentManager.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mFragmentViewBinding = FragmentDetailsBinding.inflate(layoutInflater)
        updateBaseViews()
        return mFragmentViewBinding.root
    }

    private fun updateBaseViews(){
        mFragmentViewBinding.idTvPokemonName.text = mSourceItem.name.replaceFirstChar { it.titlecase() }
        Picasso.get().load(mSourceItem.iconUrl).fit().centerInside().into(mFragmentViewBinding.idIvPokemonIcon, OnPicassoImageLoadedDoEnterTransition())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setTransitionName(mFragmentViewBinding.idIvPokemonIcon, KEY_TRANSITION_TARGET_IMAGE_HEADER)
        ViewCompat.setTransitionName(mFragmentViewBinding.idTvPokemonName, KEY_TRANSITION_TARGET_TEXT_HEADER)
        ViewCompat.setTransitionName(mFragmentViewBinding.idVBanner, KEY_TRANSITION_TARGET_BANNER)
    }

    inner class OnPicassoImageLoadedDoEnterTransition: Callback{
        override fun onSuccess() {
            startPostponedEnterTransition()
        }

        override fun onError(e: Exception?) {
            startPostponedEnterTransition()
        }

    }

}