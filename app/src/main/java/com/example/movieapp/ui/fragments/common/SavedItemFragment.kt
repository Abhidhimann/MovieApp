package com.example.movieapp.ui.fragments.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapp.R
import com.example.movieapp.data.datasource.SavedItemLocalDataSource
import com.example.movieapp.data.local.database.AppDatabase
import com.example.movieapp.data.repository.common.SavedItemRepository
import com.example.movieapp.databinding.CommonTvSeriesCardLayoutBinding
import com.example.movieapp.ui.adapter.RecommendationListAdaptor
import com.example.movieapp.viewModel.SavedItemsViewModel
import com.example.movieapp.viewModel.SavedItemsViewModelFactory


class SavedItemFragment : Fragment(R.layout.common_tv_series_card_layout) {
    private lateinit var binding: CommonTvSeriesCardLayoutBinding
    private lateinit var viewModel: SavedItemsViewModel
    private lateinit var adaptor: RecommendationListAdaptor

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = CommonTvSeriesCardLayoutBinding.bind(view)

        // change this imp
        val factory =
            SavedItemsViewModelFactory(
                SavedItemRepository(
                    SavedItemLocalDataSource(
                        AppDatabase.getDatabase(requireContext()).savedItemDao()
                    )
                )
            )
        viewModel = ViewModelProvider(
            this, factory
        )[SavedItemsViewModel::class.java]

        initItemList()
    }

    private fun initItemList() {
        adaptor = RecommendationListAdaptor()
        binding.rcItemList.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcItemList.adapter = adaptor
        setMovieListObserver()
    }

    private fun setMovieListObserver() {
        viewModel.getAllItems().observe(viewLifecycleOwner) {
            if (it.isNullOrEmpty()) {
                binding.noSavedItem.visibility = View.VISIBLE
                return@observe
            }
            binding.noSavedItem.visibility = View.GONE
            adaptor.setRecommendationList(it)
            adaptor.notifyDataSetChanged()
        }
    }
}