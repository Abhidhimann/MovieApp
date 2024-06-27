package com.example.movieapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.movieapp.R
import com.example.movieapp.data.remote.network.ApiClient
import com.example.movieapp.databinding.ActivityTvMovieDetailsLayoutBinding
import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.data.remote.model.common.RecommendationItem
import com.example.movieapp.data.remote.model.common.Review
import com.example.movieapp.data.remote.model.common.Trailer
import com.example.movieapp.data.remote.model.movies.MovieDetails
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.ui.adapter.ReviewAdaptor
import com.example.movieapp.ui.adapter.RecommendationListAdaptor
import com.example.movieapp.ui.adapter.SliderImagesAdaptor
import com.example.movieapp.utils.CommonUtil
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.viewModel.movie.MovieDetailsViewModel
import com.example.movieapp.viewModel.movie.MovieDetailsViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlin.math.min

class MovieDetailsUi : BaseActivity() {

    private lateinit var binding: ActivityTvMovieDetailsLayoutBinding
    private lateinit var viewModel: MovieDetailsViewModel
    private lateinit var reviewAdapter: ReviewAdaptor
    private lateinit var sliderImagesAdaptor: SliderImagesAdaptor
    private lateinit var recommendationListAdaptor: RecommendationListAdaptor
    private var pageItemSize = -1
    private val recommendedItemCount = 6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvMovieDetailsLayoutBinding.inflate(layoutInflater)
        Log.i(getClassTag(), "Movie Details Ui activity created")
        setContentView(binding.root)

        val factory =
            MovieDetailsViewModelFactory(
                MovieDataRepository(
                    MovieDataSource(ApiClient.movieApi()), SavedItemLocalDataSource(
                        AppDatabase.getDatabase(this).savedItemDao()
                    )
                )
            )
        viewModel = ViewModelProvider(this, factory).get(MovieDetailsViewModel::class.java)

        val movieId = intent.getLongExtra(Constants.MOVIE_ID.getValue(), -999)
        if (movieId != (-999).toLong()) {
            viewModel.getMovieDetails(movieId)
            viewModel.isMovieSaved(movieId)
        } else {
            // show error page
        }

        initView()
        initMovieImagesRecycleView()
        initReviewRecycleView()
        initRecommendationList()
        viewModel.allIndexToInitial()
        setMovieDetailsData()
        savedMovieObserver()
        nextRecommendationButtonClickListener()
        initShimmerLoading()
        lifecycle.addObserver(binding.videoWebView)
        saveItemListener()
        unSaveItemListener()
    }

    private fun savedMovieObserver() {
        viewModel.isMovieSaved.observe(this) {
            if (it) {
                binding.saveItem.visibility = View.GONE
                binding.unSaveItem.visibility = View.VISIBLE
            } else {
                binding.saveItem.visibility = View.VISIBLE
                binding.unSaveItem.visibility = View.GONE
            }
        }
    }

    private fun saveItemListener() {
        binding.saveItem.setOnClickListener {
            viewModel.movieDetails.value?.let { movieDetails ->
                viewModel.saveMovie(movieDetails)
                CommonUtil.shortToast(this, getString(R.string.movie_saved))
            }
        }
    }

    private fun unSaveItemListener() {
        binding.unSaveItem.setOnClickListener {
            viewModel.movieDetails.value?.let { movieDetails ->
                viewModel.deleteMovie(movieDetails)
                CommonUtil.shortToast(this, getString(R.string.remove_from_items))
            }
        }
    }

    private fun initView() {
        binding.tvSeason.visibility = View.GONE
    }

    // can put this in start or in onResume(little bad) if activity load is slow
    private fun setMovieDetailsData() {
        viewModel.movieDetails.observe(this) { movieDetails->
            Log.i(getClassTag(), movieDetails.toString())
            setMovieTextData(movieDetails)
            setPagerData(movieDetails.getMovieImages())
            setMovieReviews(movieDetails.getReviews())
            setVideoView(movieDetails.getYouTubeTrailer())
            setRecommendations(movieDetails.getRecommendationList())
        }
    }

    private fun setVideoView(trailer: Trailer?) {
        binding.videoWebView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
            override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                trailer?.trailerKey.let { key ->
                    if (key != null) {
                        youTubePlayer.cueVideo(key, 0f)
                    }
                }
            }
        })
    }

    private fun setMovieTextData(movieDetails: MovieDetails) {
        binding.title.text = movieDetails.title
        binding.genres.text = movieDetails.genres.joinToString(", ") { genre -> genre.name }
        binding.length.text = movieDetails.length.toString().plus(" min")
        binding.releasedDate.text = movieDetails.releaseDate
        binding.originalTitle.text = movieDetails.originalTitle
        binding.overview.text = movieDetails.movieOverview
        binding.rating.text = String.format("%.2f", movieDetails.rating)
        binding.totalVotes.text = "/".plus(movieDetails.totalVotes).plus(" rated")
    }

    private fun setPagerData(movieImages: List<String>) {
        if (movieImages.isEmpty()) {
            binding.imagesViewPager.visibility = View.GONE
        } else {
            sliderImagesAdaptor.setImageList(movieImages)
            sliderImagesAdaptor.notifyDataSetChanged()
            pageItemSize = movieImages.size
            pageChangeListener()
        }
    }

    private fun setMovieReviews(movieReviews: List<Review>) {
        if (movieReviews.isEmpty()) {
            binding.reviewsTitle.text = resources.getText(R.string.no_reviews)
        } else {
            reviewAdapter.setReviewList(movieReviews)
            reviewAdapter.notifyDataSetChanged()
        }
    }

    private fun setRecommendations(recommendations: List<RecommendationItem>) {
        if (recommendations.isEmpty()) {
            binding.recommendations.text = resources.getText(R.string.no_recommendation)
            binding.recommendedNext.visibility = View.GONE
        } else {
            viewModel.listInitialIndex = viewModel.listLastIndex
            viewModel.listLastIndex += recommendedItemCount
            recommendationListAdaptor.setRecommendationList(
                recommendations.subList(
                    viewModel.listInitialIndex,
                    min(viewModel.listLastIndex, recommendations.size)
                )
            )
            recommendationListAdaptor.notifyDataSetChanged()
        }
    }

    private fun pageChangeListener() {
        updateDots(0)
        binding.imagesViewPager.addOnPageChangeListener(object :
            ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                updateDots(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
    }

    private fun updateDots(currentPosition: Int) {
        if (pageItemSize == -1) return
        binding.trendingDotsLayout.removeAllViews()
        val dots = arrayOfNulls<ImageView>(pageItemSize)

        for (i in dots.indices) {
            dots[i] = ImageView(this)
            val inActiveDotWidthHeight = 15
            val activeDotWidthHeight = 20
            val params = LinearLayout.LayoutParams(
                if (i == currentPosition) activeDotWidthHeight else inActiveDotWidthHeight,
                if (i == currentPosition) activeDotWidthHeight else inActiveDotWidthHeight
            )
            params.setMargins(10, 0, 10, 0)
            dots[i]?.layoutParams = params
            dots[i]?.setImageResource(
                if (i == currentPosition) R.drawable.active_dot else R.drawable.inactive_dot
            )
            binding.trendingDotsLayout.addView(dots[i])
        }
    }

    private fun initReviewRecycleView() {
        reviewAdapter = ReviewAdaptor()
        binding.reviewRv.layoutManager = LinearLayoutManager(this)
        binding.reviewRv.adapter = reviewAdapter
    }

    private fun initMovieImagesRecycleView() {
        sliderImagesAdaptor = SliderImagesAdaptor(this)
        binding.imagesViewPager.adapter = sliderImagesAdaptor
    }

    private fun initRecommendationList() {
        recommendationListAdaptor = RecommendationListAdaptor()
        binding.recommendedListRC.layoutManager = GridLayoutManager(this, 2)
        binding.recommendedListRC.adapter = recommendationListAdaptor
    }

    private fun nextRecommendationButtonClickListener() {
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
        if (viewModel.listLastIndex > recommendationList!!.size) {
            binding.recommendedNext.visibility = View.GONE
        }
    }

    private fun initShimmerLoading() {
        startShimmerLoading()
        viewModel.loadingState.observe(this) {
            if (it == false) {
                binding.detailsShimmerContainer.stopShimmer()
                binding.detailsShimmerContainer.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun startShimmerLoading() {
        binding.detailsShimmerContainer.visibility = View.VISIBLE
        binding.mainLayout.visibility = View.GONE
        binding.detailsShimmerContainer.startShimmer()
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.videoWebView.release()
        Log.i(getClassTag(), "Movie Details Destroyed")
    }
}