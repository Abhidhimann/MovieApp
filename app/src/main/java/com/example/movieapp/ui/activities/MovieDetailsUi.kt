package com.example.movieapp.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.api.MovieApiClient
import com.example.movieapp.databinding.ActivityTvMovieDetailsLayoutBinding
import com.example.movieapp.repository.movie.MovieDataSource
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.ui.adaptor.ReviewAdaptor
import com.example.movieapp.ui.adaptor.RecommendationListAdaptor
import com.example.movieapp.ui.adaptor.SliderImagesAdaptor
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Tags
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.viewModel.MovieDetailsViewModel
import com.example.movieapp.viewModel.MovieDetailsViewModelFactory
import com.squareup.picasso.Picasso
import kotlin.math.min

// Don't know if this is ok will check
class MovieDetailsUi : AppCompatActivity() {

    private lateinit var binding: ActivityTvMovieDetailsLayoutBinding
    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var reviewAdapter: ReviewAdaptor
    private lateinit var sliderImagesAdaptor: SliderImagesAdaptor
    private lateinit var recommendationListAdaptor: RecommendationListAdaptor

    private val recommendedItemCount = 6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvMovieDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory =
            MovieDetailsViewModelFactory(MovieDataRepository(MovieDataSource(MovieApiClient.movieApi)))
        viewModel = ViewModelProvider(this, factory).get(MovieDetailsViewModel::class.java)

        Log.i(Tags.MOVIE_DETAILS_UI.getTag(), "Movie Details Created")

        val movieId = intent.getLongExtra(Constants.MOVIE_ID.getValue(), -999)
        if (movieId != (-999).toLong()) {
            viewModel.getMovieDetails(movieId)
        } else {
            // show error page
        }

        initMovieImagesRecycleView()
        initReviewRecycleView()
        initRecommendationList()
        initMovieDetailsBinding()
        nextRecommendationList()
    }

    private fun initMovieDetailsBinding() {
        viewModel.movieDetails.observe(this) {
            Log.i(Tags.TEMP_TAG.getTag(), it.toString())
            Picasso.get().load(Api.POSTER_BASE_URL.getValue() + it.posterImg)
                .into(binding.movieImage)
            binding.movieTitle.text = it.title
            binding.movieGenres.text = it.genres.joinToString(", ") { genre -> genre.name }
            binding.movieLength.text = it.length.toString().plus(" min")
            binding.movieReleasedDate.text = it.releaseDate
            binding.movieOriginalTitle.text = it.originalTitle
            binding.movieOverview.text = it.movieOverview
            binding.movieRating.text = String.format("%.2f", it.rating)
            binding.movieTotalVotes.text = "/".plus(it.totalVotes).plus(" rated")
            if(it.getMovieImages().isEmpty()){
                binding.movieImagesViewPager.visibility = View.GONE
            }else{
                sliderImagesAdaptor.setImageList(it.getMovieImages())
                sliderImagesAdaptor.notifyDataSetChanged()
            }

            // fun
            if (it.getReviews().isEmpty()) {
                binding.movieReviewTitle.text = resources.getText(R.string.no_reviews)
            } else {
                reviewAdapter.setReviewList(it.getReviews())
                reviewAdapter.notifyDataSetChanged()
            }

            // fun
            if (it.getRecommendationList().isEmpty()) {
                binding.movieRecommendation.text = resources.getText(R.string.no_recommendation)
                binding.recommendedNext.visibility = View.GONE
            } else {
                viewModel.listInitialIndex = viewModel.listLastIndex
                viewModel.listLastIndex += recommendedItemCount
                recommendationListAdaptor.setRecommendationList(
                    it.getRecommendationList().subList(
                        viewModel.listInitialIndex,
                        viewModel.listLastIndex
                    )
                )
                recommendationListAdaptor.notifyDataSetChanged()
            }
        }
    }

    private fun initReviewRecycleView() {
        reviewAdapter = ReviewAdaptor()
        binding.movieReviewRv.layoutManager = LinearLayoutManager(this)
        binding.movieReviewRv.adapter = reviewAdapter
    }

    private fun initMovieImagesRecycleView() {
        sliderImagesAdaptor = SliderImagesAdaptor(this)
        binding.movieImagesViewPager.adapter = sliderImagesAdaptor
    }

    private fun initRecommendationList() {
        recommendationListAdaptor = RecommendationListAdaptor()
        binding.recommendedListRC.layoutManager = GridLayoutManager(this, 2)
        binding.recommendedListRC.adapter = recommendationListAdaptor
    }

    private fun nextRecommendationList() {
        binding.recommendedNext.setOnClickListener {
            loadRecommendation()
        }
    }

    private fun loadRecommendation() {
        viewModel.listInitialIndex = viewModel.listLastIndex
        viewModel.listLastIndex += recommendedItemCount
        val recommendationList = viewModel.movieDetails.value?.getRecommendationList()

        recommendationList?.subList(
            viewModel.listInitialIndex,
            min(viewModel.listLastIndex, recommendationList.size)
        )
            ?.let { recommendationListAdaptor.setRecommendationList(it) }
        recommendationListAdaptor.notifyDataSetChanged()

        if( viewModel.listLastIndex > recommendationList!!.size){
            binding.recommendedNext.isClickable = false
            binding.recommendedNext.setTextColor(Color.WHITE)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(getClassTag(), "Movie Details Destroyed")
    }


}