package com.example.movieapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.movieapp.R
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.remote.network.MovieApiClient
import com.example.movieapp.databinding.ActivityTvMovieDetailsLayoutBinding
import com.example.movieapp.data.remote.model.tvSeries.TvSeasonDetails
import com.example.movieapp.data.repository.series.SeriesDataRepository
import com.example.movieapp.data.datasource.SeriesDataSource
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.ui.adapter.RecommendationListAdaptor
import com.example.movieapp.ui.adapter.ReviewAdaptor
import com.example.movieapp.ui.adapter.SliderImagesAdaptor
import com.example.movieapp.ui.adapter.series.EpisodeDetailsCardAdaptor
import com.example.movieapp.utils.CommonUtil
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Tags
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.utils.tempTag
import com.example.movieapp.viewModel.tvSeries.SeriesDetailsViewModel
import com.example.movieapp.viewModel.tvSeries.SeriesDetailsViewModelFactory
import com.google.android.flexbox.FlexboxLayoutManager
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import kotlin.math.min


class TvSeriesDetailsUi : BaseActivity() {

    private lateinit var binding: ActivityTvMovieDetailsLayoutBinding
    private lateinit var viewModel: SeriesDetailsViewModel
    private lateinit var sliderImagesAdaptor: SliderImagesAdaptor
    private lateinit var reviewAdaptor: ReviewAdaptor
    private lateinit var recommendationListAdaptor: RecommendationListAdaptor
    private lateinit var seriesEpisodeListViewAdaptor: EpisodeDetailsCardAdaptor
    private lateinit var tvSeasonsAdaptor: ArrayAdapter<TvSeasonDetails>
    private var pageItemSize = -1
    private var tvSeasonsSelectedItemPos = -1

    private val recommendedItemCount = 6
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTvMovieDetailsLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val factory =
            SeriesDetailsViewModelFactory(
                SeriesDataRepository(
                    SeriesDataSource(
                        MovieApiClient.tvSeriesApi(
                        )
                    ),
                    SavedItemLocalDataSource(
                        AppDatabase.getDatabase(this).savedItemDao()
                    )
                )
            )
        viewModel = ViewModelProvider(this, factory).get(SeriesDetailsViewModel::class.java)

        Log.i(getClassTag(), "Series Details activity Created")

        val seriesId = intent.getLongExtra(Constants.TV_SERIES_ID.getValue(), -999)
        if (seriesId != (-999).toLong()) {
            viewModel.getSeriesDetails(seriesId)
            viewModel.isSeriesSaved(seriesId)
        } else {
            // show error page
        }

        initMovieImagesPage()
        initReviewRecycleView()
        initRecommendationList()
        setSeriesDetailsData()
        savedSeriesObserver()
        nextRecommendationList()
        initShimmerLoading()
        seasonDetailsMoreArrowListener()
        lifecycle.addObserver(binding.videoWebView)
        saveItemListener()
        unSaveItemListener()
    }

    private fun savedSeriesObserver() {
        viewModel.isSeriesSaved.observe(this) {
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
            viewModel.seriesDetails.value?.let { seriesDetails ->
                viewModel.saveSeries(seriesDetails)
                CommonUtil.shortToast(this, getString(R.string.series_saved))
            }
        }
    }

    private fun unSaveItemListener() {
        binding.unSaveItem.setOnClickListener {
            viewModel.seriesDetails.value?.let { seriesDetails ->
                viewModel.deleteSeries(seriesDetails)
                CommonUtil.shortToast(this, getString(R.string.remove_from_items))
            }
        }
    }

    private fun setSeriesDetailsData() {
        viewModel.seriesDetails.observe(this) { it ->
            Log.i(Tags.TEMP_TAG.getTag(), it.toString())
            binding.videoWebView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                    it.getYouTubeTrailer()?.trailerKey.let { key ->
                        if (key != null) {
                            youTubePlayer.cueVideo(key, 0f)
                        }
                    }
                }
            })
            binding.movieTitle.text = it.title
            binding.movieGenres.text = it.genres.joinToString(", ") { genre -> genre.name }
            binding.movieLength.text = it.runTimeDetails.averageRunTime.toString().plus(" min / ep")
            binding.movieReleasedDate.text = it.releaseDate
            binding.movieOriginalTitle.text = it.originalTitle
            binding.movieOverview.text = it.seriesOverview
            binding.movieRating.text = String.format("%.2f", it.rating)
            binding.movieTotalVotes.text = "/".plus(it.totalVotes).plus(" rated")
            if (it.getSeriesImages().isEmpty()) {
                binding.movieImagesViewPager.visibility = View.GONE
            } else {
                sliderImagesAdaptor.setImageList(it.getSeriesImages())
                sliderImagesAdaptor.notifyDataSetChanged()
                pageItemSize = it.getSeriesImages().size
                pageChangeListener()
            }

            if (it.getReviews().isEmpty()) {
                binding.movieReviewTitle.text = resources.getText(R.string.no_reviews)
            } else {
                reviewAdaptor.setReviewList(it.getReviews())
                reviewAdaptor.notifyDataSetChanged()
            }

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
            if (it.seasons.isNotEmpty()) {
                initSeriesSeasonSpinner(it.seasons)
                episodeDetailsListViewInit()
            }
        }
    }

    private fun initSeriesSeasonSpinner(tvSeasons: List<TvSeasonDetails>) {
        Log.i(tempTag(), "tv seasons are $tvSeasons")
        tvSeasonsAdaptor = object :
            ArrayAdapter<TvSeasonDetails>(this, R.layout.custom_spinner_item, tvSeasons) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: View = convertView ?: layoutInflater.inflate(
                    R.layout.custom_spinner_item,
                    parent,
                    false
                )
                getItem(position)?.let {
                    // can make a fun like bind(view, data)
                    val textView = view.findViewById<TextView>(R.id.seasonTitle)
                    textView.text = it.name
                }
                return view
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val view: View = convertView ?: layoutInflater.inflate(
                    R.layout.custom_spinner_item,
                    parent,
                    false
                )
                getItem(position)?.let {
                    // can make a fun like bind(view, data)
                    val textView = view.findViewById<TextView>(R.id.seasonTitle)
                    textView.text = it.name
                    textView.setPadding(8, 8, 20, 8)
                    if (tvSeasonsSelectedItemPos == position) {
                        textView.setBackgroundResource(R.color.green)
                    }
                }
                return view
            }
        }
        binding.seasonsSpinner.adapter = tvSeasonsAdaptor
        binding.seasonsSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                    val seasonDetails: TvSeasonDetails =
                        p0?.getItemAtPosition(p2) as TvSeasonDetails
                    Log.i(tempTag(), "epidsoes are ${seasonDetails.episodes}")
                    binding.seasonEpisodeDetails.visibility = View.VISIBLE
                    tvSeasonsSelectedItemPos = p2
                    seriesEpisodeListViewAdaptor.setList((1..seasonDetails.episodes).toList())
                    seriesEpisodeListViewAdaptor.notifyDataSetChanged()
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
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

    private fun seasonDetailsMoreArrowListener() {
        binding.seasonArrowMore.setOnClickListener {
            binding.seasonsSpinner.performClick()
        }
    }

    private fun episodeDetailsListViewInit() {
        seriesEpisodeListViewAdaptor = EpisodeDetailsCardAdaptor()
        binding.seasonEpisodeDetails.layoutManager = FlexboxLayoutManager(this)
        binding.seasonEpisodeDetails.adapter = seriesEpisodeListViewAdaptor
    }

    private fun initReviewRecycleView() {
        reviewAdaptor = ReviewAdaptor()
        binding.movieReviewRv.layoutManager = LinearLayoutManager(this)
        binding.movieReviewRv.adapter = reviewAdaptor
    }

    private fun initMovieImagesPage() {
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
        val recommendationList = viewModel.seriesDetails.value?.getRecommendationList()

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

    private fun initSeasonsSpinner() {

    }

    private fun startShimmerLoading() {
        binding.detailsShimmerContainer.visibility = View.VISIBLE
        binding.mainLayout.visibility = View.GONE
        binding.detailsShimmerContainer.startShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(getClassTag(), "Series Details Activity Destroyed")
    }
}