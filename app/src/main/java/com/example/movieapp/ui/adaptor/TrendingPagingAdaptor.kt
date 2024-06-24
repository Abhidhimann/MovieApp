package com.example.movieapp.ui.adaptor

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.ListItemCardBinding
import com.example.movieapp.model.common.RecommendationItem
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.tempTag
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
                    Log.i(tempTag(), "calling are Items same")
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
        val recommendationItem = getItem(position)
        if (recommendationItem != null) {
           holder.bind(recommendationItem)
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
            Picasso.get().load(Api.POSTER_BASE_URL.getValue() + recommendationItem.posterImg)
                .into(binding.itemCardImage)
        }

    }
}