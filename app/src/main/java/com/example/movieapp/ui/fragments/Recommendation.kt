package com.example.movieapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapp.R
import com.example.movieapp.databinding.RecommendationLayoutBinding
import com.example.movieapp.ui.adaptor.RecommendationListAdaptor

// will see this later should can we use it
class Recommendation: Fragment(R.layout.recommendation_layout) {

    private lateinit var binding: RecommendationLayoutBinding
    private lateinit var recommendationListAdaptor: RecommendationListAdaptor
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = RecommendationLayoutBinding.bind(view)

        initRecommendationList()
    }

    private fun initRecommendationList(){
        recommendationListAdaptor = RecommendationListAdaptor()
        binding.recommendedListRC.layoutManager = LinearLayoutManager(requireContext())
        binding.recommendedListRC.adapter = recommendationListAdaptor
    }
}