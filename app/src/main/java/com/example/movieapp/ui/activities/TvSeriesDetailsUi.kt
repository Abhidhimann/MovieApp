package com.example.movieapp.ui.activities

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.api.MovieApiClient
import com.example.movieapp.databinding.ActivityTvMovieDetailsLayoutBinding
import com.example.movieapp.model.tvSeries.TvSeasonDetails
import com.example.movieapp.repository.series.SeriesDataRepository
import com.example.movieapp.repository.series.SeriesDataSource
import com.example.movieapp.ui.adaptor.RecommendationListAdaptor
import com.example.movieapp.ui.adaptor.ReviewAdaptor
import com.example.movieapp.ui.adaptor.SliderImagesAdaptor
import com.example.movieapp.ui.adaptor.series.EpisodeDetailsCardAdaptor
import com.example.movieapp.utils.Api
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.Tags
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.utils.tempTag
import com.example.movieapp.viewModel.tvSeries.SeriesDetailsViewModel
import com.example.movieapp.viewModel.tvSeries.SeriesDetailsViewModelFactory
import com.google.android.flexbox.FlexboxLayoutManager
import com.squareup.picasso.Picasso
import kotlin.math.min


class TvSeriesDetailsUi : BaseActivity() {

    private lateinit var binding: ActivityTvMovieDetailsLayoutBinding
    private lateinit var viewModel: SeriesDetailsViewModel
    private lateinit var sliderImagesAdaptor: SliderImagesAdaptor
    private lateinit var reviewAdaptor: ReviewAdaptor
    private lateinit var recommendationListAdaptor: RecommendationListAdaptor
    private lateinit var seriesEpisodeListViewAdaptor: EpisodeDetailsCardAdaptor
    private lateinit var tvSeasonsAdaptor: ArrayAdapter<TvSeasonDetails>
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
                    )
                )
            )
        viewModel = ViewModelProvider(this, factory).get(SeriesDetailsViewModel::class.java)

        Log.i(getClassTag(), "Series Details activity Created")

        val seriesId = intent.getLongExtra(Constants.TV_SERIES_ID.getValue(), -999)
        if (seriesId != (-999).toLong()) {
            viewModel.getSeriesDetails(seriesId)
        } else {
            // show error page
        }

        initMovieImagesPage()
        initReviewRecycleView()
        initRecommendationList()
        initMovieDetailsBinding()
        nextRecommendationList()
        initShimmerLoading()
        seasonDetailsMoreArrowListener()
    }

    private fun initMovieDetailsBinding() {
        viewModel.seriesDetails.observe(this) { it ->
            Log.i(Tags.TEMP_TAG.getTag(), it.toString())
            Picasso.get().load(Api.POSTER_BASE_URL.getValue() + it.posterImg)
                .into(binding.movieImage)
            binding.movieTitle.text = it.title
            binding.movieGenres.text = it.genres.joinToString(", ") { genre -> genre.name }
            binding.movieLength.text = it.length.toString().plus(" min")
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
                    textView.setPadding(8,8,20,8)
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

    private fun seasonDetailsMoreArrowListener(){
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