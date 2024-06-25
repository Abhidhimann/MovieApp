package com.example.movieapp.ui.fragments.movie

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SeriesDataSource
import com.example.movieapp.data.remote.network.MovieApiClient
import com.example.movieapp.data.repository.common.SearchRepository
import com.example.movieapp.databinding.SearchResultLayoutBinding
import com.example.movieapp.ui.adapter.movie.MovieCardAdaptor
import com.example.movieapp.ui.adapter.series.SeriesCardAdaptor
import com.example.movieapp.utils.Tags
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.viewModel.movie.SearchMovieViewModel
import com.example.movieapp.viewModel.movie.SearchMovieViewModelFactory

private const val SEARCHED_QUERY = "searched_Query"

// Maybe will add show previous results on back button
class SearchResult : Fragment(R.layout.search_result_layout) {

    private lateinit var binding: SearchResultLayoutBinding
    private lateinit var viewModel: SearchMovieViewModel
    private lateinit var movieCardAdaptor: MovieCardAdaptor
    private lateinit var seriesCardAdaptor: SeriesCardAdaptor
    private lateinit var query: String

    private val itemCount = 8

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = SearchResultLayoutBinding.bind(view)

        arguments?.let {
            query = it.getString(SEARCHED_QUERY).toString()
        }
        Log.i(getClassTag(), "Search View Created with query: $query")

        val factory =
            SearchMovieViewModelFactory(
                SearchRepository(
                    MovieDataSource(MovieApiClient.movieApi()),
                    SeriesDataSource(MovieApiClient.tvSeriesApi())
                )
            )
        viewModel = ViewModelProvider(this, factory).get(SearchMovieViewModel::class.java)

        viewModel.allMovieIndexToInitial()
        viewModel.allSeriesIndexToInitial()
        initMovieList(query)
        nextMovieList(query)
        initSeriesList(query)
        nextSeriesList(query)
        initShimmerLoading()
    }

    private fun nextMovieList(query: String) {
        binding.movieListNext.setOnClickListener {
            if (viewModel.movieListLastIndex + itemCount > viewModel.moviesList.value?.movieList?.size!!) {
                viewModel.movieListInitialIndex = 0
                viewModel.movieListLastIndex = 0
                // condition if currentPage is last page
                startShimmerLoading()
                viewModel.searchMovies(query, ++viewModel.currentMoviePage)
            } else {
                loadMovieItems()
            }
        }
    }

    private fun initMovieList(query: String) {
        movieCardAdaptor = MovieCardAdaptor()
        binding.rcMovieList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcMovieList.adapter = movieCardAdaptor
        setMovieListObserver()
        viewModel.searchMovies(query, viewModel.currentMoviePage)
    }

    private fun initSeriesList(query: String) {
        seriesCardAdaptor = SeriesCardAdaptor()
        binding.rcSeriesList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcSeriesList.adapter = seriesCardAdaptor
        setSeriesListObserver()
        viewModel.searchSeries(query, viewModel.currentSeriesPage)
    }

    private fun setMovieListObserver() {
        viewModel.moviesList.observe(viewLifecycleOwner) {
            if (it.movieList.isNullOrEmpty()){
                binding.movieTitle.visibility = View.GONE
                binding.movieListNext.visibility = View.GONE
                binding.divider.visibility = View.GONE
                return@observe
            }
            viewModel.movieListInitialIndex = viewModel.movieListLastIndex
            viewModel.movieListLastIndex += itemCount

            movieCardAdaptor.setList(
                if (it.movieList.size < itemCount) {
                    it.movieList
                } else
                    it.movieList.subList(
                        viewModel.movieListInitialIndex,
                        viewModel.movieListLastIndex
                    )
            )
            movieCardAdaptor.notifyDataSetChanged()
            // condition if currentPage is last page
            if (viewModel.currentMoviePage == it.totalPages) {
                binding.movieListNext.visibility = View.GONE
            }
        }
    }

    private fun setSeriesListObserver() {
        viewModel.seriesList.observe(viewLifecycleOwner) {
            if (it.seriesList.isNullOrEmpty()){
                binding.seriesTitle.visibility = View.GONE
                binding.seriesListNext.visibility = View.GONE
                binding.divider.visibility = View.GONE
                return@observe
            }
            viewModel.seriesListInitialIndex = viewModel.seriesListLastIndex
            viewModel.seriesListLastIndex += itemCount

            seriesCardAdaptor.setList(
                if (it.seriesList.size < itemCount) {
                    it.seriesList
                } else
                    it.seriesList.subList(
                        viewModel.seriesListInitialIndex,
                        viewModel.seriesListLastIndex
                    )
            )
            seriesCardAdaptor.notifyDataSetChanged()
            // condition if currentPage is last page
            if (viewModel.currentSeriesPage == it.totalPages) {
                binding.seriesListNext.visibility = View.GONE
            }
        }
    }

    private fun nextSeriesList(query: String) {
        binding.seriesListNext.setOnClickListener {
            if (viewModel.seriesListLastIndex + itemCount > viewModel.seriesList.value?.seriesList?.size!!) {
                viewModel.seriesListInitialIndex = 0
                viewModel.seriesListLastIndex = 0
                // condition if currentPage is last page
//                startShimmerLoading()
                viewModel.searchSeries(query, ++viewModel.currentSeriesPage)
            } else {
                loadSeriesItems()
            }
        }
    }

    private fun loadMovieItems() {
        viewModel.movieListInitialIndex = viewModel.movieListLastIndex
        viewModel.movieListLastIndex += itemCount
        viewModel.moviesList.value?.movieList?.subList(
            viewModel.movieListInitialIndex,
            viewModel.movieListLastIndex
        )
            ?.let { movieCardAdaptor.setList(it) }
        movieCardAdaptor.notifyDataSetChanged()
    }

    private fun loadSeriesItems() {
        viewModel.seriesListInitialIndex = viewModel.seriesListLastIndex
        viewModel.seriesListLastIndex += itemCount
        viewModel.seriesList.value?.seriesList?.subList(
            viewModel.seriesListInitialIndex,
            viewModel.seriesListLastIndex
        )
            ?.let { seriesCardAdaptor.setList(it) }
        seriesCardAdaptor.notifyDataSetChanged()
    }

    private fun initShimmerLoading() {
        startShimmerLoading()
        viewModel.loadingState.observe(viewLifecycleOwner) {
            if (it == false) {
                binding.shimmerListView.stopShimmer()
                binding.shimmerListView.visibility = View.GONE
                binding.mainLayout.visibility = View.VISIBLE
            }
        }
    }

    private fun startShimmerLoading() {
        binding.shimmerListView.visibility = View.VISIBLE
        binding.mainLayout.visibility = View.GONE
        binding.shimmerListView.startShimmer()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(Tags.TEMP_TAG.getTag(), "Search Movie Result fragment destroyed")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param query Parameter 1.
         * @return A new instance of fragment BaseMovieList.
         */
        @JvmStatic
        fun newInstance(query: String) =
            SearchResult().apply {
                arguments = Bundle().apply {
                    putString(SEARCHED_QUERY, query)
                }
            }
    }
}