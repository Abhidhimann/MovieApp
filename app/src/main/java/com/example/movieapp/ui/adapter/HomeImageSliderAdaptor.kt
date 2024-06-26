package com.example.movieapp.ui.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.example.movieapp.databinding.HomeTrendingListViewBinding
import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.ui.activities.MovieDetailsUi
import com.example.movieapp.ui.activities.TvSeriesDetailsUi
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.getClassTag
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
        onViewClickListener(initRecommendationList[position])
        return binding.root
    }

    private fun onViewClickListener(recommendationItem: RecommendationItem){
        binding.root.setOnClickListener {
            Log.i(getClassTag(),recommendationItem.mediaType)
            if(recommendationItem.mediaType == Constants.MOVIE.getValue()){
                val intent = Intent(context, MovieDetailsUi::class.java)
                intent.putExtra(Constants.MOVIE_ID.getValue(),recommendationItem.id)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                context.startActivity(intent)
            } else {
                val intent = Intent(binding.root.context, TvSeriesDetailsUi::class.java)
                intent.putExtra(Constants.TV_SERIES_ID.getValue(),recommendationItem.id)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                binding.root.context.startActivity(intent)
            }
        }
    }

    private fun bind(recommendationItem: RecommendationItem){
        binding.trendingTitle.text = recommendationItem.title
        binding.trendingType.text = recommendationItem.mediaType.uppercase()
        binding.trendingRating.text = "- ".plus(recommendationItem.rating.toInt())

        Log.i(getClassTag(), Api.POSTER_BASE_URL.getValue() + recommendationItem.posterImg)
        Picasso.get().load(Api.POSTER_BASE_URL.getValue() + recommendationItem.posterImg)
            .into(binding.trendingImagesView)
    }

    override fun destroyItem(container: ViewGroup, positxion: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }
}