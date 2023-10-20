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
import com.example.movieapp.viewModel.tvSeries.BaseSeriesListViewModeFactory
import com.example.movieapp.viewModel.tvSeries.BaseSeriesListViewModel

private const val SeriesType = "seriesType"

/**
 * Imp here I'm using one fragment as 3 with the help of argument
 * But if I want to manage fragment state ( like next(3) page will be saved) will switching
 * then we have to use 3 different model and then will see what viewModel to use according to argument
 * that will be difficult to manage, so deduction: it is much much better to use 3 fragment and 3 viewModel
 * in 1-1 relationship ( maybe can use one base class as parent)
 */

class BaseSeriesList: Fragment(R.layout.fragment_series_list) {

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

        // will change imp imp imp
        val factory =
            BaseSeriesListViewModeFactory(SeriesDataRepository(SeriesDataSource(MovieApiClient.tvSeriesApi)))
        viewModel = ViewModelProvider(this, factory).get(BaseSeriesListViewModel::class.java)

        viewModel.allIndexToInitial()
        initSeriesList()
        nextSeriesList()
    }

    private fun nextSeriesList() {
        binding.homeSeriesListNext.setOnClickListener {
            if (viewModel.listLastIndex + itemCount > viewModel.seriesList.value?.seriesList?.size!!) {
                viewModel.listInitialIndex = 0
                viewModel.listLastIndex = 0
                viewModel.processSeriesType(seriesType, ++viewModel.currentPage)
            } else {
                if (viewModel.currentPage==viewModel.seriesList.value?.totalPages){
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
        setSeriesListObserver()
        viewModel.processSeriesType(seriesType, viewModel.currentPage)
    }

    private fun setSeriesListObserver() {
        viewModel.seriesList.observe(viewLifecycleOwner) {
            viewModel.listInitialIndex = viewModel.listLastIndex
            viewModel.listLastIndex += itemCount
            Log.i(Tags.TEMP_TAG.getTag(), "whyyy " + viewModel.listLastIndex.toString())
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
        viewModel.seriesList.value?.seriesList?.subList(
            viewModel.listInitialIndex,
            viewModel.listLastIndex
        )
            ?.let { adaptor.setList(it) }
        adaptor.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(getClassTag(), "Base Series List View Destroyed with movie type: $seriesType")
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param seriesType Parameter 1.
         * @return A new instance of fragment BaseMovieList.
         */
        @JvmStatic
        fun newInstance(seriesType: String) =
            BaseSeriesList().apply {
                arguments = Bundle().apply {
                    putString(SeriesType, seriesType)
                }
            }
    }
}