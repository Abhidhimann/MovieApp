package com.example.movieapp.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.example.movieapp.databinding.SliderImagesViewHolderBinding
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.getClassTag
import com.squareup.picasso.Picasso
import java.util.*

class SliderImagesAdaptor(private val context: Context) : PagerAdapter() {

    private val initialImagesList = mutableListOf<String>()
    private lateinit var binding: SliderImagesViewHolderBinding

    override fun getCount(): Int {
        return initialImagesList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        binding =
            SliderImagesViewHolderBinding.inflate(LayoutInflater.from(context), container, false)
        bind(initialImagesList[position])
        Objects.requireNonNull(container).addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

    fun bind(seriesImages: String) {
        Log.i(getClassTag(), Api.POSTER_BASE_URL.getValue() + seriesImages)
        Picasso.get().load(Api.POSTER_BASE_URL.getValue() + seriesImages)
            .into(binding.imagesView)
    }

    fun setImageList(newImagesList: List<String>) {
        initialImagesList.clear()
        initialImagesList.addAll(newImagesList)
    }

}