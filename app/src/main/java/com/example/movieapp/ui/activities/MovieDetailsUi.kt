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
import com.example.movieapp.data.remote.network.MovieApiClient
import com.example.movieapp.databinding.ActivityTvMovieDetailsLayoutBinding
import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.ui.adapter.ReviewAdaptor
import com.example.movieapp.ui.adapter.RecommendationListAdaptor
import com.example.movieapp.ui.adapter.SliderImagesAdaptor
import com.example.movieapp.utils.CommonUtil
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Tags
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.utils.tempTag
import com.example.movieapp.viewModel.movie.MovieDetailsViewModel
import com.example.movieapp.viewModel.movie.MovieDetailsViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlin.math.min

// Don't know if this is ok will check
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
        setContentView(binding.root)

        val factory =
            MovieDetailsViewModelFactory(
                MovieDataRepository(
                    MovieDataSource(MovieApiClient.movieApi()), SavedItemLocalDataSource(
                        AppDatabase.getDatabase(this).savedItemDao()
                    )
                )
            )
        viewModel = ViewModelProvider(this, factory).get(MovieDetailsViewModel::class.java)

        Log.i(Tags.MOVIE_DETAILS_UI.getTag(), "Movie Details Created")

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
        setMovieDetailsData()
        savedMovieObserver()
        nextRecommendationList()
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

    private fun setMovieDetailsData() {
        viewModel.movieDetails.observe(this) {
            Log.i(Tags.TEMP_TAG.getTag(), it.toString())
            binding.videoWebView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    it.getYouTubeTrailer()?.trailerKey.let { key ->
                        if (key != null) {
                            youTubePlayer.cueVideo(key!!, 0f)
                        }
                    }
                }
            })
            binding.movieTitle.text = it.title
            binding.movieGenres.text = it.genres.joinToString(", ") { genre -> genre.name }
            binding.movieLength.text = it.length.toString().plus(" min")
            binding.movieReleasedDate.text = it.releaseDate
            binding.movieOriginalTitle.text = it.originalTitle
            binding.movieOverview.text = it.movieOverview
            binding.movieRating.text = String.format("%.2f", it.rating)
            binding.movieTotalVotes.text = "/".plus(it.totalVotes).plus(" rated")
            if (it.getMovieImages().isEmpty()) {
                binding.movieImagesViewPager.visibility = View.GONE
            } else {
                sliderImagesAdaptor.setImageList(it.getMovieImages())
                sliderImagesAdaptor.notifyDataSetChanged()
                pageItemSize = it.getMovieImages().size
                pageChangeListener()
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
                        min(viewModel.listLastIndex, it.getRecommendationList().size)
                    )
                )
                recommendationListAdaptor.notifyDataSetChanged()
            }
        }
    }

    private fun pageChangeListener() {
        updateDots(0)
        Log.i(tempTag(), "Coming here with $pageItemSize")
        binding.movieImagesViewPager.addOnPageChangeListener(object :
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