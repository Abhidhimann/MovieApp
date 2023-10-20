package com.example.movieapp.ui.adaptor

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.example.movieapp.R
import com.example.movieapp.databinding.HomeTrendingListViewBinding
import com.example.movieapp.model.common.RecommendationItem
import com.example.movieapp.ui.activities.MovieDetailsUi
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Tags
import com.squareup.picasso.Picasso
import java.util.*

class HomeImageSliderAdaptor(private val context: Context): PagerAdapter() {

    private val initRecommendationList = mutableListOf<RecommendationItem>()

    private lateinit var binding: HomeTrendingListViewBinding

    fun setList(newList: List<RecommendationItem>) {
        initRecommendationList.clear()
        initRecommendationList.addAll(newList)
    }

    override fun getCount(): Int {
        return initRecommendationList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        binding = HomeTrendingListViewBinding.inflate(LayoutInflater.from(context), container, false)

        bind(initRecommendationList[position])

        Objects.requireNonNull(container).addView(binding.root)
        updateDots(position)
        onViewClickListener(initRecommendationList[position])
        return binding.root
    }

    private fun onViewClickListener(recommendationItem: RecommendationItem){
        binding.root.setOnClickListener {
            if(recommendationItem.mediaType == Constants.MOVIE.getValue()){
                Log.i(Tags.TEMP_TAG.getTag(),recommendationItem.mediaType)
                val intent = Intent(context, MovieDetailsUi::class.java)
                intent.putExtra(Constants.MOVIE_ID.getValue(),recommendationItem.id)
                context.startActivity(intent)
            }
        }
    }


    private fun updateDots(currentPosition: Int) {
        binding.trendingDotsLayout.removeAllViews()
        val dots = arrayOfNulls<ImageView>(count)

        for (i in dots.indices) {
            dots[i] = ImageView(context)
            val inActiveDotWidthHeight = 15 // Adjust the width and height of dots
            val activeDotWidthHeight = 20
            val params = LinearLayout.LayoutParams(
                if (i == currentPosition) activeDotWidthHeight else inActiveDotWidthHeight,
                if (i == currentPosition) activeDotWidthHeight else inActiveDotWidthHeight
            )
            params.setMargins(10, 5, 10, 0)
            dots[i]?.layoutParams = params
            dots[i]?.setImageResource(
                if (i == currentPosition) R.drawable.active_dot else R.drawable.inactive_dot
            )
            binding.trendingDotsLayout.addView(dots[i])
        }
    }

    private fun bind(recommendationItem: RecommendationItem){
        binding.trendingTitle.text = recommendationItem.title
        binding.trendingType.text = recommendationItem.mediaType.uppercase()
        binding.trendingRating.text = "- ".plus(recommendationItem.rating.toInt())

        Log.i(Tags.TEMP_TAG.getTag(), Api.POSTER_BASE_URL.getValue() + recommendationItem.posterImg)
        Picasso.get().load(Api.POSTER_BASE_URL.getValue() + recommendationItem.posterImg)
            .into(binding.trendingImagesView)
    }

    override fun destroyItem(container: ViewGroup, positxion: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}