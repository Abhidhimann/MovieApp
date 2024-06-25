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
import com.example.movieapp.databinding.FragmentMovieListBinding
import com.example.movieapp.ui.adapter.movie.MovieCardAdaptor
import com.example.movieapp.utils.Tags
import com.example.movieapp.viewModel.movie.SearchMovieViewModel
import com.example.movieapp.viewModel.movie.SearchMovieViewModelFactory

private const val SEARCHED_QUERY = "searched_Query"

// Maybe will add show previous results on back button
class SearchMovieResult : Fragment(R.layout.fragment_movie_list) {

    private lateinit var binding: FragmentMovieListBinding
    private lateinit var viewModel: SearchMovieViewModel
    private lateinit var adaptor: MovieCardAdaptor
    private lateinit var query: String

    private val itemCount = 16

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieListBinding.bind(view)

        arguments?.let {
            query = it.getString(SEARCHED_QUERY).toString()
        }
        Log.i(Tags.TEMP_TAG.getTag(), "Search Movie View Created with query: $query")

        val factory =
            SearchMovieViewModelFactory(
                SearchRepository(
                    MovieDataSource(MovieApiClient.movieApi()),
                    SeriesDataSource(MovieApiClient.tvSeriesApi())
                )
            )
        viewModel = ViewModelProvider(this, factory).get(SearchMovieViewModel::class.java)

        viewModel.allIndexToInitial()
        initMovieList(query)
        nextMovieList(query)
        initShimmerLoading()
    }

    private fun nextMovieList(query: String) {
        binding.homeMovieListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.moviesList.value?.movieList?.size!!) {
                viewModel.listInitialIndex = 0
                viewModel.listLastIndex = 0
                // condition if currentPage is last page
                startShimmerLoading()
                viewModel.searchMovies(query, ++viewModel.currentPage)
            } else {
                loadItems()
            }
        }
    }

    private fun initMovieList(query: String) {
        adaptor = MovieCardAdaptor()
        binding.rcMovieList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcMovieList.adapter = adaptor
        setMovieListObserver()
        viewModel.searchMovies(query, viewModel.currentPage)
    }

    private fun setMovieListObserver() {
        viewModel.moviesList.observe(viewLifecycleOwner) {
            viewModel.listInitialIndex = viewModel.listLastIndex
            viewModel.listLastIndex += itemCount

            adaptor.setList(
                if (it.movieList.size < itemCount) {
                    it.movieList
                } else
                    it.movieList.subList(
                        viewModel.listInitialIndex,
                        viewModel.listLastIndex
                    )
            )
            adaptor.notifyDataSetChanged()
            // condition if currentPage is last page
            if (viewModel.currentPage == it.totalPages) {
                binding.homeMovieListNext.visibility = View.GONE
            }
        }
    }

    private fun loadItems() {
        viewModel.listInitialIndex = viewModel.listLastIndex
        viewModel.listLastIndex += itemCount
        viewModel.moviesList.value?.movieList?.subList(
            viewModel.listInitialIndex,
            viewModel.listLastIndex
        )
            ?.let { adaptor.setList(it) }
        adaptor.notifyDataSetChanged()
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
            SearchMovieResult().apply {
                arguments = Bundle().apply {
                    putString(SEARCHED_QUERY, query)
                }
            }
    }
}