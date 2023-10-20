package com.example.movieapp.ui.adaptor

import android.content.Intent
import android.content.Intent.*
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.movieapp.databinding.RecommendedMovieTvCardBinding
import com.example.movieapp.model.common.RecommendationItem
import com.example.movieapp.ui.activities.MovieDetailsUi
import com.example.movieapp.ui.activities.TvSeriesDetailsUi
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.Constants
import com.squareup.picasso.Picasso

class RecommendationListAdaptor :
    RecyclerView.Adapter<RecommendationListAdaptor.RecommendedMovieTvViewHolder>() {

    private val recommendedList = mutableListOf<RecommendationItem>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecommendedMovieTvViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return RecommendedMovieTvViewHolder(
            RecommendedMovieTvCardBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return recommendedList.size
    }

    override fun onBindViewHolder(holder: RecommendedMovieTvViewHolder, position: Int) {
        holder.bind(recommendedList[position])
        holder.itemView.setOnClickListener {
            holder.goToMediaTypeDetails(recommendedList[position])
        }
    }

    fun setRecommendationList(newList: List<RecommendationItem>){
        recommendedList.clear()
        recommendedList.addAll(newList)
    }

    inner class RecommendedMovieTvViewHolder(private val binding: RecommendedMovieTvCardBinding) :
        ViewHolder(binding.root) {

        fun bind(recommendationItem: RecommendationItem) {
//            Log.i(getClassTag(), recommendationItem.toString())
            Picasso.get().load(Api.POSTER_BASE_URL.getValue() + recommendationItem.posterImg)
                .into(binding.recommendedImage)
            binding.recommendedTitle.text = recommendationItem.title
            binding.recommendedYear.text = recommendationItem.postingDate.substringBefore("-")
            binding.recommendedRating.text = "- ".plus(recommendationItem.rating.toInt())
            binding.recommendedMovieTV.text = recommendationItem.mediaType.uppercase()
        }

        fun goToMediaTypeDetails(recommendationItem: RecommendationItem) {
//            Log.i(getClassTag(),recommendationItem.mediaType)
            if (recommendationItem.mediaType == Constants.MOVIE.getValue()) {
                val intent = Intent(binding.root.context, MovieDetailsUi::class.java)
                intent.putExtra(Constants.MOVIE_ID.getValue(), recommendationItem.id)
                intent.flags = FLAG_ACTIVITY_CLEAR_TOP // destroying current movie details activity
                binding.root.context.startActivity(intent)
            }else{
                val intent = Intent(binding.root.context, TvSeriesDetailsUi::class.java)
                intent.putExtra(Constants.TV_SERIES_ID.getValue(), recommendationItem.id)
                intent.flags = FLAG_ACTIVITY_CLEAR_TOP // destroying current movie details activity
                binding.root.context.startActivity(intent)
            }
        }
    }
}