package com.example.movieapp.ui.fragments.movie

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.api.MovieApiClient
import com.example.movieapp.databinding.FragmentMovieListBinding
import com.example.movieapp.repository.movie.MovieDataSource
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.ui.activities.BaseActivity
import com.example.movieapp.ui.adaptor.movie.MovieCardAdaptor
import com.example.movieapp.utils.RetryFunctionality
import com.example.movieapp.utils.Tags
import com.example.movieapp.utils.tempTag
import com.example.movieapp.viewModel.HomePageMovieListViewModelFactory
import com.example.movieapp.viewModel.HomePageMovieListViewModel

class HomeMovieList : Fragment(R.layout.fragment_movie_list), RetryFunctionality {

    private lateinit var binding: FragmentMovieListBinding
    private lateinit var adaptor: MovieCardAdaptor
    private lateinit var viewModel: HomePageMovieListViewModel

    private val itemCount = 10

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieListBinding.bind(view)
        Log.i(Tags.TEMP_TAG.getTag(), "Home Movie List View Created")
        (activity as BaseActivity).activeFrames.add(this)

        // will change imp imp imp
        val factory =
            HomePageMovieListViewModelFactory(MovieDataRepository(MovieDataSource(MovieApiClient.movieApi())))
        viewModel = ViewModelProvider(this, factory).get(HomePageMovieListViewModel::class.java)

        viewModel.allIndexToInitial()
        initMovieList()
        nextMovieList()
        initShimmerLoading()
    }


    private fun initMovieList() {
        adaptor = MovieCardAdaptor()
        binding.rcMovieList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcMovieList.adapter = adaptor
        setMovieListObserver()
        viewModel.getTrendingMovies(viewModel.currentPage)
    }

    private fun setMovieListObserver() {
        viewModel.trendingMovies.observe(viewLifecycleOwner) {
            viewModel.listLastIndex = itemCount
            Log.i(tempTag(), it.movieList.toString())
            Log.i(Tags.TEMP_TAG.getTag(), "whyyy " + viewModel.listLastIndex.toString())
            adaptor.setList(
                it.movieList.subList(
                    viewModel.listInitialIndex,
                    viewModel.listLastIndex
                )
            )
            adaptor.notifyDataSetChanged()
            if (viewModel.currentPage == it.totalPages) {
                binding.homeMovieListNext.visibility = View.GONE
            }
        }
    }

    private fun nextMovieList() {
        binding.homeMovieListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.trendingMovies.value?.movieList?.size!!) {
                viewModel.listInitialIndex = 0
                viewModel.listLastIndex = 0
                startShimmerLoading()
                Log.i("temp tag",viewModel.currentPage.toString())
                viewModel.getTrendingMovies(++viewModel.currentPage)
            } else {
                if (viewModel.currentPage == viewModel.trendingMovies.value?.totalPages) {
                    binding.homeMovieListNext.setTextColor(Color.WHITE)
                    binding.homeMovieListNext.isClickable = false
                }
                loadItems()
            }
        }
    }


    private fun loadItems() {
        viewModel.listInitialIndex = viewModel.listLastIndex
        viewModel.listLastIndex += itemCount
        viewModel.trendingMovies.value?.movieList?.subList(
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
        Log.i(Tags.TEMP_TAG.getTag(), "Home Movie List View Destroyed")
    }

    override fun retryWhenInternetIsAvailable() {
        startShimmerLoading()
        viewModel.getTrendingMovies(viewModel.currentPage)
    }

}