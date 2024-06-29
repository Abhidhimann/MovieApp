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
import com.example.movieapp.navigation.FragmentNavigation
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

    private val itemCount = 20

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
        pagesIndexInit()
        initShimmerLoading()
        nextButtonClickListener()
        errorStateObserver()
    }

    private fun nextButtonClickListener() {
        binding.homeSeriesListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.seriesList.value?.seriesList?.size!!) {
                loadSeriesListNextPage(seriesType, viewModel.currentPage + 1)
            } else {
                loadItems()
            }
            binding.mainLayout.scrollTo(0, 0)
        }
    }

    private fun initSeriesList() {
        adaptor = SeriesCardAdaptor()
        binding.rcSeriesList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcSeriesList.adapter = adaptor
        setSeriesListObserver()
        loadSeriesListNextPage(seriesType, viewModel.currentPage + 1)
    }

    private fun setSeriesListObserver() {
        viewModel.seriesList.observe(viewLifecycleOwner) {
            viewModel.listLastIndex = itemCount
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

    private fun pagesIndexInit() {
        pagesClickListener()
        currentPageObserver()
    }

    private fun currentPageObserver() {
        viewModel.currentPageLiveValue.observe(viewLifecycleOwner) {
            if (it >= 3) {
                binding.nextPage.text = it.toString()
                binding.previousPage.text = (it - 1).toString()
            }
            when (it.toString()) {
                binding.nextPage.text.toString() -> setActivePage(
                    binding.nextPage,
                    binding.previousPage,
                    binding.initialPage
                )

                binding.previousPage.text.toString() -> setActivePage(
                    binding.previousPage,
                    binding.nextPage,
                    binding.initialPage
                )

                binding.initialPage.text.toString() -> setActivePage(
                    binding.initialPage,
                    binding.previousPage,
                    binding.nextPage
                )
            }
        }
    }

    private fun setActivePage(activeView: View, vararg inactiveViews: View) {
        activeView.setBackgroundResource(R.drawable.index_page_active)
        inactiveViews.forEach { it.setBackgroundResource(R.drawable.index_page_inactive) }
    }

    private fun pagesClickListener() {
        binding.initialPage.setOnClickListener {
            loadSeriesListNextPage(seriesType, binding.initialPage.text.toString().toInt())
            binding.nextPage.text = (3).toString()
            binding.previousPage.text = (2).toString()
        }
        binding.previousPage.setOnClickListener {
            loadSeriesListNextPage(seriesType, binding.previousPage.text.toString().toInt())
        }
        binding.nextPage.setOnClickListener {
            loadSeriesListNextPage(seriesType, binding.nextPage.text.toString().toInt())
        }
    }

    private fun loadSeriesListNextPage(seriesType: String, currentPage: Int) {
        binding.mainLayout.scrollTo(0, 0)
        startShimmerLoading()
        viewModel.listInitialIndex = 0
        viewModel.listLastIndex = 0
        viewModel.incrementCurrentPage(currentPage)
        viewModel.processSeriesType(seriesType, currentPage)
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

    private fun errorStateObserver() {
        viewModel.errorState.observe(viewLifecycleOwner) {
            if (it) {
                FragmentNavigation(childFragmentManager).toErrorActivity(requireContext())
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