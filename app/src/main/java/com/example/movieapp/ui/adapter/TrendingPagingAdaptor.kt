package com.example.movieapp.ui.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.databinding.ListItemCardBinding
import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.ui.activities.MovieDetailsUi
import com.example.movieapp.ui.activities.TvSeriesDetailsUi
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.Constants
import com.squareup.picasso.Picasso

class TrendingPagingAdaptor :
    PagingDataAdapter<RecommendationItem, TrendingPagingAdaptor.CardViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<RecommendationItem> =
            object : DiffUtil.ItemCallback<RecommendationItem>() {
                override fun areItemsTheSame(
                    oldItem: RecommendationItem,
                    newItem: RecommendationItem
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: RecommendationItem,
                    newItem: RecommendationItem
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val recommendationItem = getItem(position) ?: return
        holder.bind(recommendationItem)
        holder.itemView.setOnClickListener {
            holder.goToMediaTypeDetails(recommendationItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CardViewHolder(ListItemCardBinding.inflate(layoutInflater, parent, false))
    }

    class CardViewHolder(private val binding: ListItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recommendationItem: RecommendationItem) {
            Log.i("recommendation item bind", recommendationItem.toString())
            binding.itemCardTitle.text = recommendationItem.title
            binding.itemCardYear.text = recommendationItem.postingDate?.substringBefore("-")
            binding.itemCardRating.text = recommendationItem.rating.toInt().toString()
            if (!recommendationItem.posterImg.isNullOrEmpty()) {
                Picasso.get().load(Api.POSTER_BASE_URL.getValue() + recommendationItem.posterImg)
                    .into(binding.itemCardImage)
            } else {
                Picasso.get().load(
                    R.drawable.image_not_found
                ).into(binding.itemCardImage)
            }
        }

        fun goToMediaTypeDetails(recommendationItem: RecommendationItem) {
            if (recommendationItem.mediaType == Constants.MOVIE.getValue()) {
                val intent = Intent(binding.root.context, MovieDetailsUi::class.java)
                intent.putExtra(Constants.MOVIE_ID.getValue(), recommendationItem.id)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP // destroying current movie details activity
                binding.root.context.startActivity(intent)
            } else {
                val intent = Intent(binding.root.context, TvSeriesDetailsUi::class.java)
                intent.putExtra(Constants.TV_SERIES_ID.getValue(), recommendationItem.id)
                intent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP // destroying current movie details activity
                binding.root.context.startActivity(intent)
            }
        }

    }
}