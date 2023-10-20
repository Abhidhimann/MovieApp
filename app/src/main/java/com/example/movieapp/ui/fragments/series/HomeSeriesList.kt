package com.example.movieapp.ui.fragments.series


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.api.MovieApiClient
import com.example.movieapp.databinding.FragmentSeriesListBinding
import com.example.movieapp.repository.series.SeriesDataRepository
import com.example.movieapp.repository.series.SeriesDataSource
import com.example.movieapp.ui.adaptor.series.SeriesCardAdaptor
import com.example.movieapp.utils.Tags
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.viewModel.tvSeries.HomePageSeriesListViewModel
import com.example.movieapp.viewModel.tvSeries.HomePageSeriesListViewModelFactory

class HomeSeriesList : Fragment(R.layout.fragment_series_list) {
    private lateinit var binding: FragmentSeriesListBinding
    private lateinit var adaptor: SeriesCardAdaptor
    private lateinit var viewModel: HomePageSeriesListViewModel

    private val itemCount = 10

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSeriesListBinding.bind(view)
        Log.i(getClassTag(), "Series List View Created")

        // will change imp imp imp
        val factory =
            HomePageSeriesListViewModelFactory(
                SeriesDataRepository(SeriesDataSource((MovieApiClient.tvSeriesApi)))
            )
        viewModel = ViewModelProvider(this, factory).get(HomePageSeriesListViewModel::class.java)

        initSeriesList()
        nextSeriesList()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(getClassTag(), "Series List View Destroyed")
    }

    private fun nextSeriesList() {
        binding.homeSeriesListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.trendingSeries.value?.seriesList?.size!!) {
                viewModel.listInitialIndex = 0
                viewModel.listLastIndex = 0
                viewModel.getTrendingMovies(++viewModel.currentPage)
            } else {
                if (viewModel.currentPage==viewModel.trendingSeries.value?.totalPages){
                    binding.homeSeriesListNext.setTextColor(Color.WHITE)
                    binding.homeSeriesListNext.isClickable = false
                }
                loadItems()
            }
        }
    }

    private fun initSeriesList() {
        adaptor = SeriesCardAdaptor()
        binding.rcSeriesList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcSeriesList.adapter = adaptor
        viewModel.getTrendingMovies(viewModel.currentPage)

        viewModel.trendingSeries.observe(viewLifecycleOwner) {
            viewModel.listInitialIndex = viewModel.listLastIndex
            viewModel.listLastIndex += itemCount
            adaptor.setList(
                it.seriesList.subList(
                    viewModel.listInitialIndex,
                    viewModel.listLastIndex
                )
            )
            adaptor.notifyDataSetChanged()
        }
    }

    private fun loadItems() {
        viewModel.listInitialIndex = viewModel.listLastIndex
        viewModel.listLastIndex += itemCount
        viewModel.trendingSeries.value?.seriesList?.subList(
            viewModel.listInitialIndex,
            viewModel.listLastIndex
        )
            ?.let { adaptor.setList(it) }
        adaptor.notifyDataSetChanged()
    }

}