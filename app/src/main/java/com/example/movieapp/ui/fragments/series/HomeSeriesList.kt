package com.example.movieapp.ui.fragments.series


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.remote.network.MovieApiClient
import com.example.movieapp.databinding.FragmentSeriesListBinding
import com.example.movieapp.data.repository.series.SeriesDataRepository
import com.example.movieapp.data.datasource.SeriesDataSource
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.ui.activities.BaseActivity
import com.example.movieapp.ui.adapter.series.SeriesCardAdaptor
import com.example.movieapp.utils.RetryFunctionality
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.viewModel.tvSeries.HomePageSeriesListViewModel
import com.example.movieapp.viewModel.tvSeries.HomePageSeriesListViewModelFactory

class HomeSeriesList : Fragment(R.layout.fragment_series_list), RetryFunctionality {
    private lateinit var binding: FragmentSeriesListBinding
    private lateinit var adaptor: SeriesCardAdaptor
    private lateinit var viewModel: HomePageSeriesListViewModel

    private val itemCount = 10

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSeriesListBinding.bind(view)
        Log.i(getClassTag(), "Series List View Created")
        (activity as BaseActivity).activeFrames.add(this)

        // will change imp imp imp
        val factory =
            HomePageSeriesListViewModelFactory(
                SeriesDataRepository(
                    SeriesDataSource((MovieApiClient.tvSeriesApi())), SavedItemLocalDataSource(
                        AppDatabase.getDatabase(requireContext()).savedItemDao()
                    )
                )
            )
        viewModel = ViewModelProvider(this, factory).get(HomePageSeriesListViewModel::class.java)

        viewModel.allIndexToInitial()
        initSeriesList()
        nextSeriesList()
        initShimmerLoading()
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
                startShimmerLoading()
                viewModel.getTrendingMovies(++viewModel.currentPage)
            } else {
                loadItems()
            }
        }
    }

    private fun initSeriesList() {
        adaptor = SeriesCardAdaptor()
        binding.rcSeriesList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcSeriesList.adapter = adaptor
        viewModel.getTrendingMovies(viewModel.currentPage)
        setSeriesObserver()
    }

    private fun setSeriesObserver() {
        viewModel.trendingSeries.observe(viewLifecycleOwner) {
            viewModel.listLastIndex = itemCount
            adaptor.setList(
                it.seriesList.subList(
                    viewModel.listInitialIndex,
                    viewModel.listLastIndex
                )
            )
            adaptor.notifyDataSetChanged()
            // condition if we can go next
            if (viewModel.currentPage == it.totalPages) {
                binding.homeSeriesListNext.visibility = View.GONE
            }
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

    override fun retryWhenInternetIsAvailable() {
        viewModel.getTrendingMovies(viewModel.currentPage)
    }

}