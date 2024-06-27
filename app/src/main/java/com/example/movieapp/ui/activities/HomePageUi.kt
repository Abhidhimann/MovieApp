package com.example.movieapp.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.navigation.FragmentNavigation
import com.example.movieapp.utils.Constants
import com.example.movieapp.utils.getClassTag

class HomePageUi : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigation: FragmentNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigation = FragmentNavigation(supportFragmentManager)
        Log.i(getClassTag(), "Home page ui activity created")

        if (savedInstanceState==null) {
            navigation.toHomeFragment(binding.homeFrameLayout.id) // Default Menu
        }
        initializeDrawerMenu()
        searchBarTextChangeListener()
        searchClickListener()
        searchBarImeActionListener()
        initErrorPage()
    }

    private fun searchBarTextChangeListener() {
        binding.toolBar.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.toolBar.searchMain.setColorFilter(
                    ContextCompat.getColor(
                        this@HomePageUi,
                        if (!p0.isNullOrEmpty()) R.color.green else R.color.white
                    )
                )
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }

    private fun initializeDrawerMenu() {
        binding.toolBar.menuMain.setOnClickListener {
            if (binding.homeDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.homeDrawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.homeDrawerLayout.openDrawer(GravityCompat.START)
            }
        }
        initDrawerNavigation()
    }

    private fun initDrawerNavigation() {
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.home_item -> {
                    navigation.toHomeFragment(binding.homeFrameLayout.id)
                }

                R.id.popular_movie_item -> {
                    navigation.toBasePopularMoviesFragment(binding.homeFrameLayout.id)
                }

                R.id.imdb_movie_item -> {
                    navigation.toBaseImdbMoviesFragment(binding.homeFrameLayout.id)
                }

                R.id.upcoming_movie_item -> {
                    navigation.toBaseUpcomingMoviesFragment(binding.homeFrameLayout.id)
                }

                R.id.popular_series_item -> {
                    navigation.toBasePopularSeriesFragment(binding.homeFrameLayout.id)
                }

                R.id.imdb_series_item -> {
                    navigation.toBaseImdbSeriesFragment(binding.homeFrameLayout.id)
                }

                R.id.airing_series_item -> {
                    navigation.toBaseAiringSeriesFragment(binding.homeFrameLayout.id)
                }

                R.id.paging_recommendation -> {
                    navigation.toTrendingPagingFragment(binding.homeFrameLayout.id)
                }

                R.id.saved_item -> {
                    navigation.toSavedItemFragment(binding.homeFrameLayout.id)
                }
            }
            true
        }
    }

    private fun searchClickListener() {
        binding.toolBar.searchMain.setOnClickListener {
            searchQuery()
        }
    }

    private fun searchBarImeActionListener(){
        binding.toolBar.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchQuery()
            }
            false
        }
    }

    private fun searchQuery(){
        searchBarQuerySubmit()
        searchBarCancelButtonInit()
    }

    private fun searchBarQuerySubmit() {
        if (binding.toolBar.searchEditText.visibility == View.VISIBLE) {
            if (binding.toolBar.searchEditText.text.toString().length < 3) {
                binding.toolBar.searchEditText.error = "Enter at-least 3 digits"
            } else {
                navigation.toSearchMovieFragment(
                    binding.toolBar.searchEditText.text.toString(),
                    binding.homeFrameLayout.id
                )
            }
        }

    }

    private fun searchBarCancelButtonInit() {
        if (binding.toolBar.searchEditText.visibility == View.GONE) {
            binding.toolBar.searchEditText.visibility = View.VISIBLE
            binding.toolBar.searchTempSpace.visibility = View.GONE
            binding.toolBar.cancelSearchEditText.visibility = View.VISIBLE
            binding.toolBar.searchMain.setColorFilter(
                ContextCompat.getColor(
                    this,
                    R.color.white
                )
            )
        }


        binding.toolBar.cancelSearchEditText.setOnClickListener {
            if (binding.toolBar.cancelSearchEditText.visibility == View.VISIBLE) {
                binding.toolBar.searchEditText.visibility = View.GONE
                binding.toolBar.searchEditText.setText("")
                binding.toolBar.searchTempSpace.visibility = View.VISIBLE
                binding.toolBar.cancelSearchEditText.visibility = View.GONE
            }
        }

    }

    private fun initErrorPage() {
        if (intent.getBooleanExtra(Constants.ERROR_PAGE.getValue(), false)) {
            navigation.toErrorFragment(binding.homeFrameLayout.id)
        }
    }
}