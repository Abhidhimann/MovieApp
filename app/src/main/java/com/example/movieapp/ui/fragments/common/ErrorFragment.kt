package com.example.movieapp.ui.fragments.common

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentErrorBinding

class ErrorFragment: Fragment(R.layout.fragment_error) {

    private lateinit var binding: FragmentErrorBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentErrorBinding.bind(view)
    }
}