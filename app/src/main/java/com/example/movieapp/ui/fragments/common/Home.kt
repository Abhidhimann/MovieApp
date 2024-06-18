package com.example.movieapp.ui.fragments.common

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.movieapp.R
import com.example.movieapp.api.MovieApiClient
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.repository.movie.MovieDataSource
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.ui.activities.BaseActivity
import com.example.movieapp.ui.adaptor.HomeImageSliderAdaptor
import com.example.movieapp.ui.navigation.FragmentNavigation
import com.example.movieapp.utils.RetryFunctionality
import com.example.movieapp.utils.Tags
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.viewModel.HomePageViewModel
import com.example.movieapp.viewModel.HomePageViewModelFactory
import kotlin.math.min

class Home : Fragment(R.layout.fragment_home), RetryFunctionality {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var navigation: FragmentNavigation
    private lateinit var imageSliderAdaptor: HomeImageSliderAdaptor
    private lateinit var viewModel: HomePageViewModel
    private val trendingItems = 10

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(Tags.TEMP_TAG.getTag(),"Home Fragment Created")
        binding = FragmentHomeBinding.bind(view)
        (activity as BaseActivity).activeFrames.add(this)
        navigation = FragmentNavigation(childFragmentManager)

        val factory =
            HomePageViewModelFactory(MovieDataRepository(MovieDataSource(MovieApiClient.movieApi())))
        viewModel = ViewModelProvider(this, factory).get(HomePageViewModel::class.java)

        initImageSlider()
        displayMovieList()
        switchBetweenMovieAndTvSeriesList()
    }

    private fun initImageSlider(){
        viewModel.getTrendingRecommendation()
        imageSliderAdaptor = HomeImageSliderAdaptor(requireContext())
        binding.imageSlider.adapter = imageSliderAdaptor
        setImageList()
    }

    private fun setImageList(){
        viewModel.trending.observe(viewLifecycleOwner){
            imageSliderAdaptor.setList(it.recommendationList.subList(0, min(trendingItems,it.recommendationList.size)))
            imageSliderAdaptor.notifyDataSetChanged()
            Log.i(Tags.TEMP_TAG.getTag(),it.recommendationList.toString())
            // showing only 10 trending item now
        }
    }

    private fun switchBetweenMovieAndTvSeriesList(){
        binding.movieList.setOnClickListener {
           displayMovieList()
        }
        binding.tvSeriesList.setOnClickListener {
            displayTvList()
        }
    }

    private fun displayMovieList(){
        navigation.toMovieListFragment(binding.containerMovie.id)
        binding.containerTv.visibility = View.GONE
        binding.containerMovie.visibility = View.VISIBLE

        binding.movieList.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
        binding.tvSeriesList.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
    }

    private fun displayTvList(){
        navigation.toTvSeriesFragment(binding.containerTv.id)
        binding.containerMovie.visibility = View.GONE
        binding.containerTv.visibility = View.VISIBLE

        binding.tvSeriesList.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green))
        binding.movieList.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
    }

    override fun retryWhenInternetIsAvailable() {
        viewModel.getTrendingRecommendation()
    }

}