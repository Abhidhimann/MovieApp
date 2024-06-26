package com.example.movieapp.ui.adapter.series

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.R
import com.example.movieapp.databinding.ListItemCardBinding
import com.example.movieapp.data.remote.model.tvSeries.SeriesItem
import com.example.movieapp.ui.activities.TvSeriesDetailsUi
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.getClassTag
import com.squareup.picasso.Picasso

class SeriesCardAdaptor : RecyclerView.Adapter<SeriesCardAdaptor.SeriesCardViewHolder>() {
    private val initialSeriesItemList = mutableListOf<SeriesItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeriesCardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return SeriesCardViewHolder(ListItemCardBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return initialSeriesItemList.size
    }

    override fun onBindViewHolder(holder: SeriesCardViewHolder, position: Int) {
        holder.bind(initialSeriesItemList[position])
        holder.itemView.setOnClickListener {
            holder.goToSeriesDetails(initialSeriesItemList[position])
        }
    }

    fun setList(newSeriesItemList: List<SeriesItem>) {
        initialSeriesItemList.clear()
        initialSeriesItemList.addAll(newSeriesItemList)
    }

    inner class SeriesCardViewHolder(private val binding: ListItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(seriesItem: SeriesItem) {
            Log.i(getClassTag(), "series is $seriesItem")
            Picasso.get().load(Api.POSTER_BASE_URL.getValue() + seriesItem.posterImg)
                .into(binding.itemCardImage)
            if (!seriesItem.posterImg.isNullOrEmpty()) {
                Picasso.get().load(Api.POSTER_BASE_URL.getValue() + seriesItem.posterImg)
                    .into(binding.itemCardImage)
            } else {
                Picasso.get().load(
                    R.drawable.image_not_found
                )
                    .into(binding.itemCardImage)
            }
            binding.itemCardTitle.text = seriesItem.title
            binding.itemCardYear.text = seriesItem.releaseDate.substringBefore("-")
            binding.itemCardRating.text = seriesItem.rating.toInt().toString()
        }

        fun goToSeriesDetails(movieItem: SeriesItem) {
            val intent = Intent(binding.root.context, TvSeriesDetailsUi::class.java)
            intent.putExtra(Constants.TV_SERIES_ID.getValue(), movieItem.id)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            binding.root.context.startActivity(intent)
        }
    }
}