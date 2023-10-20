package com.example.movieapp.ui.fragments.movie

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.api.MovieApiClient
import com.example.movieapp.databinding.FragmentMovieListBinding
import com.example.movieapp.repository.movie.MovieDataSource
import com.example.movieapp.repository.movie.MovieDataRepository
import com.example.movieapp.ui.adaptor.movie.MovieCardAdaptor

import com.example.movieapp.utils.Tags
import com.example.movieapp.viewModel.BaseMovieListViewModeFactory
import com.example.movieapp.viewModel.BaseMovieListViewModel


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

    private val itemCount = 16

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMovieListBinding.bind(view)
        arguments?.let {
            moviesType = it.getString(MoviesType).toString()
        }
        Log.i(Tags.TEMP_TAG.getTag(), "Base Movie List View Created with movie type: $moviesType")

        // will change imp imp imp
        val factory =
            BaseMovieListViewModeFactory(MovieDataRepository(MovieDataSource(MovieApiClient.movieApi)))
        viewModel = ViewModelProvider(this, factory).get(BaseMovieListViewModel::class.java)

        viewModel.allIndexToInitial()
        initMovieList()
        nextMovieList()
    }

    private fun nextMovieList() {
        binding.homeMovieListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.moviesList.value?.movieList?.size!!) {
                viewModel.listInitialIndex = 0
                viewModel.listLastIndex = 0
                viewModel.processMoviesType(moviesType, ++viewModel.currentPage)
            } else {
                if (viewModel.currentPage==viewModel.moviesList.value?.totalPages){
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
            viewModel.listInitialIndex = viewModel.listLastIndex
            viewModel.listLastIndex += itemCount
            Log.i(Tags.TEMP_TAG.getTag(), "whyyy " + viewModel.listLastIndex.toString())
            adaptor.setList(
                it.movieList.subList(
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
        viewModel.moviesList.value?.movieList?.subList(
            viewModel.listInitialIndex,
            viewModel.listLastIndex
        )
            ?.let { adaptor.setList(it) }
        adaptor.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(Tags.TEMP_TAG.getTag(), "Base Movie List View Destroyed with movie type: $moviesType")
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