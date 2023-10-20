package com.example.movieapp.ui.adaptor

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.example.movieapp.R
import com.example.movieapp.databinding.SliderImagesViewHolderBinding
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.Tags
import com.squareup.picasso.Picasso
import java.util.*

class SliderImagesAdaptor(private val context: Context): PagerAdapter() {

    private val initialImagesList = mutableListOf<String>()
    private lateinit var binding: SliderImagesViewHolderBinding
    private lateinit var dotsLayout: LinearLayout

    override fun getCount(): Int {
        return initialImagesList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        binding = SliderImagesViewHolderBinding.inflate(LayoutInflater.from(context), container, false)
        bind(initialImagesList[position])
        Objects.requireNonNull(container).addView(binding.root)

        updateDots(position)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

    fun bind(seriesImages: String){
        Log.i(Tags.TEMP_TAG.getTag(), Api.POSTER_BASE_URL.getValue() + seriesImages)
        Picasso.get().load(Api.POSTER_BASE_URL.getValue() + seriesImages)
            .into(binding.imagesView)
    }

    private fun updateDots(currentPosition: Int) {
        binding.dotsLayout.removeAllViews()
        val dots = arrayOfNulls<ImageView>(count)

        for (i in dots.indices) {
            dots[i] = ImageView(context)
            val inActiveDotWidthHeight = 15 // Adjust the width and height of dots
            val activeDotWidthHeight = 20
            val params = LinearLayout.LayoutParams(
                if (i == currentPosition) activeDotWidthHeight else inActiveDotWidthHeight,
                if (i == currentPosition) activeDotWidthHeight else inActiveDotWidthHeight
            )
            params.setMargins(10, 0, 10, 10)
            dots[i]?.layoutParams = params
            dots[i]?.setImageResource(
                if (i == currentPosition) R.drawable.active_dot else R.drawable.inactive_dot
            )
            binding.dotsLayout.addView(dots[i])
        }
    }

    fun setImageList(newImagesList: List<String>){
        initialImagesList.clear()
        initialImagesList.addAll(newImagesList)
    }

}