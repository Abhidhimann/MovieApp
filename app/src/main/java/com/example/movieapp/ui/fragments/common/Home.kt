package com.example.movieapp.ui.fragments.common

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.movieapp.R
import com.example.movieapp.data.remote.network.ApiClient
import com.example.movieapp.databinding.FragmentHomeBinding
import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.ui.activities.BaseActivity
import com.example.movieapp.ui.adapter.HomeImageSliderAdaptor
import com.example.movieapp.navigation.FragmentNavigation
import com.example.movieapp.utils.RetryFunctionality
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
        Log.i(getClassTag(), "Home Fragment Created")
        binding = FragmentHomeBinding.bind(view)
        (activity as BaseActivity).activeFrames.add(this)
        navigation = FragmentNavigation(childFragmentManager)

        val factory =
            HomePageViewModelFactory(
                MovieDataRepository(
                    MovieDataSource(ApiClient.movieApi()), SavedItemLocalDataSource(
                        AppDatabase.getDatabase(requireContext()).savedItemDao()
                    )
                )
            )
        viewModel = ViewModelProvider(this, factory).get(HomePageViewModel::class.java)

        initImageSlider()
        initFragment()
        displayMovieList()
        movieAndSeriesSwitchListner()
        pageChangeListener()
    }

    private fun initImageSlider() {
        viewModel.getTrendingRecommendation()
        imageSliderAdaptor = HomeImageSliderAdaptor(requireContext())
        binding.imageSlider.adapter = imageSliderAdaptor
        setImageList()
    }

    private fun initFragment() {
        displayMovieList()
        displayTvSeriesList()
    }

    private fun pageChangeListener() {
        updateDots(0)
        binding.imageSlider.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
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
        binding.trendingDotsLayout.removeAllViews()
        val dots = arrayOfNulls<ImageView>(trendingItems)

        for (i in dots.indices) {
            dots[i] = ImageView(context)
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

    private fun setImageList() {
        viewModel.trending.observe(viewLifecycleOwner) {
            imageSliderAdaptor.setList(
                it.recommendationList.subList(
                    0,
                    min(trendingItems, it.recommendationList.size)
                )
            )
            imageSliderAdaptor.notifyDataSetChanged()
            Log.i(getClassTag(), it.recommendationList.toString())
            // showing only 10 trending item now
        }
    }

    private fun movieAndSeriesSwitchListner() {
        binding.movieList.setOnClickListener {
            displayMovieList()
        }
        binding.tvSeriesList.setOnClickListener {
            displayTvSeriesList()
        }
    }

    private fun displayMovieList() {
        navigation.toMovieListFragment(binding.containerMovie.id)
        binding.containerTv.visibility = View.GONE
        binding.containerMovie.visibility = View.VISIBLE

        binding.movieList.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.green
            )
        )
        binding.tvSeriesList.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
    }

    private fun displayTvSeriesList() {
        navigation.toTvSeriesFragment(binding.containerTv.id)
        binding.containerMovie.visibility = View.GONE
        binding.containerTv.visibility = View.VISIBLE

        binding.tvSeriesList.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.green
            )
        )
        binding.movieList.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.colorPrimary
            )
        )
    }

    override fun retryWhenInternetIsAvailable() {
        viewModel.getTrendingRecommendation()
    }

}