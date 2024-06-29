package com.example.movieapp.ui.fragments.movie

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import com.example.movieapp.R
import com.example.movieapp.data.remote.network.ApiClient
import com.example.movieapp.databinding.FragmentMovieListBinding
import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.ui.activities.BaseActivity
import com.example.movieapp.ui.adapter.movie.MovieCardAdaptor
import com.example.movieapp.utils.RetryFunctionality
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.viewModel.HomePageMovieListViewModelFactory
import com.example.movieapp.viewModel.HomePageMovieListViewModel

class HomeMovieList : Fragment(R.layout.fragment_movie_list), RetryFunctionality {

    private lateinit var binding: FragmentMovieListBinding
    private lateinit var adaptor: MovieCardAdaptor
    private lateinit var viewModel: HomePageMovieListViewModel

    private val itemCount = 20

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieListBinding.bind(view)
        Log.i(getClassTag(), "Home Movie List View Created")
        (activity as BaseActivity).activeFrames.add(this)

        // will change hilt
        val factory =
            HomePageMovieListViewModelFactory(
                MovieDataRepository(
                    MovieDataSource(ApiClient.movieApi()), SavedItemLocalDataSource(
                        AppDatabase.getDatabase(requireContext()).savedItemDao()
                    )
                )
            )
        viewModel = ViewModelProvider(this, factory).get(HomePageMovieListViewModel::class.java)
        viewModel.allIndexToInitial()
        initMovieList()
        initShimmerLoading()
        pagesIndexInit()
        nextButtonClickListener()
    }


    private fun initMovieList() {
        adaptor = MovieCardAdaptor()
        binding.rcMovieList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcMovieList.adapter = adaptor
        setMovieListObserver()
        loadMovieListNextPage(viewModel.currentPage)
    }

    private fun setMovieListObserver() {
        viewModel.trendingMovies.observe(viewLifecycleOwner) {
            viewModel.listLastIndex = itemCount
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

    private fun nextButtonClickListener() {
        binding.homeMovieListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.trendingMovies.value?.movieList?.size!!) {
                loadMovieListNextPage(viewModel.currentPage + 1)
            } else {
                if (viewModel.currentPage == viewModel.trendingMovies.value?.totalPages) {
                    binding.homeMovieListNext.setTextColor(Color.WHITE)
                    binding.homeMovieListNext.isClickable = false
                }
                loadItems()
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
            loadMovieListNextPage(binding.initialPage.text.toString().toInt())
            binding.nextPage.text = (3).toString()
            binding.previousPage.text = (2).toString()
        }
        binding.previousPage.setOnClickListener {
            loadMovieListNextPage(binding.previousPage.text.toString().toInt())
        }
        binding.nextPage.setOnClickListener {
            loadMovieListNextPage(binding.nextPage.text.toString().toInt())
        }
    }

    private fun loadMovieListNextPage(currentPage: Int) {
        // comment this to stop automatic scroll while pressing next button
        val toScrollHeight: Int = activity?.findViewById<ViewPager>(R.id.imageSlider)?.height.toString().toInt()
        val scrollView = activity?.findViewById<NestedScrollView>(R.id.nested_scroll_view)
        scrollView?.scrollTo(0, toScrollHeight + 30)

        startShimmerLoading()
        viewModel.incrementCurrentPage(currentPage)
        viewModel.getTrendingMovies(currentPage)
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

    override fun retryWhenInternetIsAvailable() {
        startShimmerLoading()
        loadMovieListNextPage(viewModel.currentPage)
    }
}