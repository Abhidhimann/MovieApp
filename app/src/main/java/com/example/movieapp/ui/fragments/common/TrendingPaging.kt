package com.example.movieapp.ui.fragments.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.api.MovieApiClient
import com.example.movieapp.databinding.FragmentMovieListBinding
import com.example.movieapp.databinding.TempLayoutBinding
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.repository.movie.MovieDataSource
import com.example.movieapp.ui.adaptor.TrendingPagingAdaptor
import com.example.movieapp.ui.adaptor.movie.MovieCardAdaptor
import com.example.movieapp.ui.navigation.FragmentNavigation
import com.example.movieapp.viewModel.TrendingPagingViewModel
import com.example.movieapp.viewModel.TrendingPagingViewModelFactory
import com.example.movieapp.viewModel.movie.BaseMovieListViewModeFactory
import com.example.movieapp.viewModel.movie.BaseMovieListViewModel

class TrendingPaging : Fragment(R.layout.temp_layout) {

    private lateinit var binding: TempLayoutBinding
    private lateinit var adaptor: TrendingPagingAdaptor
    private lateinit var viewModel: TrendingPagingViewModel
    private val startingPage = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = TempLayoutBinding.bind(view)

        // will change imp imp imp
        val factory =
            TrendingPagingViewModelFactory(MovieDataSource(MovieApiClient.movieApi()))
        viewModel = ViewModelProvider(this, factory)[TrendingPagingViewModel::class.java]
        initMovieList()
    }


    private fun initMovieList() {
        adaptor = TrendingPagingAdaptor()
        binding.rcMovieList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcMovieList.adapter = adaptor
        setMovieListObserver()
    }

    private fun setMovieListObserver() {
        viewModel.getRecommendationItems(startingPage).observe(viewLifecycleOwner) {
            adaptor.submitData(lifecycle, it)
        }
    }

}