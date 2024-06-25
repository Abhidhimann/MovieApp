package com.example.movieapp.ui.adapter.movie

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.databinding.ListItemCardBinding
import com.example.movieapp.data.remote.model.movies.MovieItem
import com.example.movieapp.ui.activities.MovieDetailsUi
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.Constants
import com.squareup.picasso.Picasso

// Although Movie and Series Card adaptor could be combine, but they are kind of main components
// in future difference could be big so made different
class MovieCardAdaptor : RecyclerView.Adapter<MovieCardAdaptor.MovieCardViewHolder>() {

    private val initialMovieItemList = mutableListOf<MovieItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieCardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return MovieCardViewHolder(ListItemCardBinding.inflate(layoutInflater, parent, false))
    }

    override fun getItemCount(): Int {
        return initialMovieItemList.size
    }

    override fun onBindViewHolder(holder: MovieCardViewHolder, position: Int) {
        holder.bind(initialMovieItemList[position])
        holder.itemView.setOnClickListener {
            holder.goToMovieDetails(initialMovieItemList[position])
        }
    }

    fun setList(newMovieItemList: List<MovieItem>) {
        initialMovieItemList.clear()
        initialMovieItemList.addAll(newMovieItemList)
    }

    inner class MovieCardViewHolder(private val binding: ListItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(movieItem: MovieItem) {
//            Log.i("Movie item bind", movieItem.toString())
            Picasso.get().load(Api.POSTER_BASE_URL.getValue() + movieItem.posterImg)
                .into(binding.itemCardImage)
            binding.itemCardTitle.text = movieItem.title
            binding.itemCardYear.text = movieItem.releaseDate.substringBefore("-")
            binding.itemCardRating.text = movieItem.rating.toInt().toString()
        }

        fun goToMovieDetails(movieItem: MovieItem){
            val intent = Intent(binding.root.context, MovieDetailsUi::class.java)
            intent.putExtra(Constants.MOVIE_ID.getValue(),movieItem.id)
            binding.root.context.startActivity(intent)
        }
    }
}