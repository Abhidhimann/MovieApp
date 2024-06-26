package com.example.movieapp.ui.fragments.series

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.remote.network.ApiClient
import com.example.movieapp.databinding.FragmentSeriesListBinding
import com.example.movieapp.data.repository.series.SeriesDataRepository
import com.example.movieapp.data.datasource.SeriesDataSource
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.ui.adapter.series.SeriesCardAdaptor
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.viewModel.tvSeries.BaseSeriesListViewModeFactory
import com.example.movieapp.viewModel.tvSeries.BaseSeriesListViewModel

private const val SeriesType = "seriesType"

/**
 * Imp here I'm using one fragment as 3 views with the help of argument
 * But if I want to manage fragment states ( like next 3 pages saving) while switching
 * then we have to use 3 different model and then will see what viewModel to use according to argument
 * that will be difficult to manage, so deduction: it is much much better to use 3 fragment and 3 viewModel
 * in 1-1 relationship ( maybe can use one base class as parent)
 */

class BaseSeriesList : Fragment(R.layout.fragment_series_list) {

    private lateinit var seriesType: String
    private lateinit var binding: FragmentSeriesListBinding
    private lateinit var adaptor: SeriesCardAdaptor
    private lateinit var viewModel: BaseSeriesListViewModel

    private val itemCount = 16

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSeriesListBinding.bind(view)
        arguments?.let {
            seriesType = it.getString(SeriesType).toString()
        }
        Log.i(getClassTag(), "Base Series List View Created with movie type: $seriesType")

        // will change to hilt
        val factory =
            BaseSeriesListViewModeFactory(
                SeriesDataRepository(
                    SeriesDataSource(ApiClient.tvSeriesApi()),
                    SavedItemLocalDataSource(
                        AppDatabase.getDatabase(requireContext()).savedItemDao()
                    )
                )
            )

        viewModel = ViewModelProvider(this, factory).get(BaseSeriesListViewModel::class.java)

        viewModel.allIndexToInitial()
        initSeriesList()
        nextSeriesList()
        initShimmerLoading()
    }

    private fun nextSeriesList() {
        binding.homeSeriesListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.seriesList.value?.seriesList?.size!!) {
                viewModel.listInitialIndex = 0
                viewModel.listLastIndex = 0
                startShimmerLoading()
                viewModel.processSeriesType(seriesType, ++viewModel.currentPage)
            } else {
                loadItems()
            }
        }
    }

    private fun initSeriesList() {
        adaptor = SeriesCardAdaptor()
        binding.rcSeriesList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcSeriesList.adapter = adaptor
        setSeriesListObserver()
        viewModel.processSeriesType(seriesType, viewModel.currentPage)
    }

    private fun setSeriesListObserver() {
        viewModel.seriesList.observe(viewLifecycleOwner) {
            viewModel.listInitialIndex = viewModel.listLastIndex
            viewModel.listLastIndex += itemCount
            adaptor.setList(
                it.seriesList.subList(
                    viewModel.listInitialIndex,
                    viewModel.listLastIndex
                )
            )
            adaptor.notifyDataSetChanged()
            if (viewModel.currentPage == it.totalPages) {
                binding.homeSeriesListNext.visibility = View.GONE
            }
        }
    }

    private fun loadItems() {
        viewModel.listInitialIndex = viewModel.listLastIndex
        viewModel.listLastIndex += itemCount
        viewModel.seriesList.value?.seriesList?.subList(
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

    companion object {
        @JvmStatic
        fun newInstance(seriesType: String) =
            BaseSeriesList().apply {
                arguments = Bundle().apply {
                    putString(SeriesType, seriesType)
                }
            }
    }
}