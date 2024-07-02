package com.example.movieapp.ui.fragments.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.databinding.CommonTvSeriesCardLayoutBinding
import com.example.movieapp.ui.adapter.TrendingPagingAdaptor
import com.example.movieapp.viewModel.TrendingPagingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrendingPaging : Fragment(R.layout.common_tv_series_card_layout) {

    private lateinit var binding: CommonTvSeriesCardLayoutBinding
    private lateinit var adaptor: TrendingPagingAdaptor
    private lateinit var viewModel: TrendingPagingViewModel
    private val startingPage = 1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CommonTvSeriesCardLayoutBinding.bind(view)
        viewModel = ViewModelProvider(this)[TrendingPagingViewModel::class.java]
        initItemList()
    }


    private fun initItemList() {
        adaptor = TrendingPagingAdaptor()
        binding.rcItemList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcItemList.adapter = adaptor
        setMovieListObserver()
    }

    private fun setMovieListObserver() {
        viewModel.getRecommendationItems(startingPage).observe(viewLifecycleOwner) {
            adaptor.submitData(lifecycle, it)
        }
    }

}