package com.example.movieapp.ui.fragments.movie

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.data.remote.network.ApiClient
import com.example.movieapp.databinding.FragmentMovieListBinding
import com.example.movieapp.data.datasource.MovieDataSource
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.data.repository.movie.MovieDataRepository
import com.example.movieapp.ui.adapter.movie.MovieCardAdaptor
import com.example.movieapp.navigation.FragmentNavigation
import com.example.movieapp.utils.getClassTag
import com.example.movieapp.viewModel.movie.BaseMovieListViewModeFactory
import com.example.movieapp.viewModel.movie.BaseMovieListViewModel


private const val MoviesType = "movieType"

/**
 * Imp here I'm using one fragment as 3 with the help of argument
 * But if I want to manage fragment state ( like next(3) page will be saved) will switching
 * then we have to use 3 different model and then will see what viewModel to use according to argument
 * that will be difficult to manage, so then it will be better to use 3 fragment ( maybe can use one base class as parent)
 */
class BaseMovieList : Fragment(R.layout.fragment_movie_list) {

    private lateinit var moviesType: String
    private lateinit var binding: FragmentMovieListBinding
    private lateinit var adaptor: MovieCardAdaptor
    private lateinit var viewModel: BaseMovieListViewModel

    private val itemCount = 20

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieListBinding.bind(view)
        arguments?.let {
            moviesType = it.getString(MoviesType).toString()
        }
        Log.i(getClassTag(), "Base Movie List View Created with movie type: $moviesType")

        // will change hilt
        val factory =
            BaseMovieListViewModeFactory(
                MovieDataRepository(
                    MovieDataSource(ApiClient.movieApi()), SavedItemLocalDataSource(
                        AppDatabase.getDatabase(requireContext()).savedItemDao()
                    )
                )
            )
        viewModel = ViewModelProvider(
            this, factory
        ).get(BaseMovieListViewModel::class.java)

        viewModel.allIndexToInitial()
        initMovieList()
        initShimmerLoading()
        nextButtonClickListener()
        errorStateObserver()
    }

    private fun nextButtonClickListener() {
        binding.homeMovieListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.moviesList.value?.movieList?.size!!) {
                viewModel.listInitialIndex = 0
                viewModel.listLastIndex = 0
                startShimmerLoading()
                viewModel.processMoviesType(moviesType, ++viewModel.currentPage)
            } else {
                if (viewModel.currentPage == viewModel.moviesList.value?.totalPages) {
                    binding.homeMovieListNext.setTextColor(Color.WHITE)
                    binding.homeMovieListNext.isClickable = false
                }
                loadItems()
            }
        }
    }

    private fun initMovieList() {
        adaptor = MovieCardAdaptor()
        binding.rcMovieList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcMovieList.adapter = adaptor
        setMovieListObserver()
        viewModel.processMoviesType(moviesType, viewModel.currentPage)
    }

    private fun setMovieListObserver() {
        viewModel.moviesList.observe(viewLifecycleOwner) {
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

    private fun loadItems() {
        viewModel.listInitialIndex = viewModel.listLastIndex
        viewModel.listLastIndex += itemCount
        viewModel.moviesList.value?.movieList?.subList(
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


    private fun errorStateObserver() {
        viewModel.errorState.observe(viewLifecycleOwner) {
            if (it) {
                FragmentNavigation(childFragmentManager).toErrorActivity(requireContext())
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param moviesType Parameter 1.
         * @return A new instance of fragment BaseMovieList.
         */
        @JvmStatic
        fun newInstance(moviesType: String) =
            BaseMovieList().apply {
                arguments = Bundle().apply {
                    putString(MoviesType, moviesType)
                }
            }
    }
}