package com.example.movieapp.ui.fragments.series


import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.movieapp.R
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.remote.network.ApiClient
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

    private val itemCount = 20

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSeriesListBinding.bind(view)
        Log.i(getClassTag(), "Series List View Created")
        (activity as BaseActivity).activeFrames.add(this)

        // will change to hilt
        val factory =
            HomePageSeriesListViewModelFactory(
                SeriesDataRepository(
                    SeriesDataSource((ApiClient.tvSeriesApi())), SavedItemLocalDataSource(
                        AppDatabase.getDatabase(requireContext()).savedItemDao()
                    )
                )
            )
        viewModel = ViewModelProvider(this, factory).get(HomePageSeriesListViewModel::class.java)

        viewModel.allIndexToInitial()
        initSeriesList()
        initShimmerLoading()
        pagesIndexInit()
        nextButtonClickListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(getClassTag(), "Series List View Destroyed")
    }

    private fun nextButtonClickListener() {
        binding.homeSeriesListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.trendingSeries.value?.seriesList?.size!!) {
                loadSeriesListNextPage(viewModel.currentPage + 1)
            } else {
                loadItems()
            }
        }
    }

    private fun initSeriesList() {
        adaptor = SeriesCardAdaptor()
        binding.rcSeriesList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcSeriesList.adapter = adaptor
        setSeriesObserver()
        loadSeriesListNextPage(viewModel.currentPage)
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

    private fun pagesIndexInit() {
        pagesClickListener()
        currentPageObserver()
    }

    private fun pagesClickListener() {
        binding.initialPage.setOnClickListener {
            loadSeriesListNextPage(binding.initialPage.text.toString().toInt())
            binding.nextPage.text = (3).toString()
            binding.previousPage.text = (2).toString()
        }
        binding.previousPage.setOnClickListener {
            loadSeriesListNextPage(binding.previousPage.text.toString().toInt())
        }
        binding.nextPage.setOnClickListener {
            loadSeriesListNextPage(binding.nextPage.text.toString().toInt())
        }
    }

    private fun loadSeriesListNextPage(currentPage: Int) {
        // comment this to stop automatic scroll while pressing next button
        val toScrollHeight: Int = activity?.findViewById<ViewPager>(R.id.imageSlider)?.height.toString().toInt()
        val scrollView = activity?.findViewById<NestedScrollView>(R.id.nested_scroll_view)
        scrollView?.scrollTo(0, toScrollHeight + 30)

        startShimmerLoading()
        viewModel.incrementCurrentPage(currentPage)
        viewModel.getTrendingSeries(currentPage)
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
        loadSeriesListNextPage(viewModel.currentPage)
    }

}