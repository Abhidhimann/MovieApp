package com.example.movieapp.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import com.example.movieapp.R
import com.example.movieapp.databinding.ActivityMainBinding
import com.example.movieapp.ui.navigation.FragmentNavigation
import com.example.movieapp.utils.Tags

open class HomePageUi : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navigation: FragmentNavigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigation = FragmentNavigation(supportFragmentManager)

        navigation.toHomeFragment(binding.homeFrameLayout.id) // Default Menu
        initializeDrawerMenu()
        showSearchBar()

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
             // do nothing
            }

        })

    }

    private fun initializeDrawerMenu() {
        binding.toolBar.menuMain.setOnClickListener { // Toggle the state of the navigation drawer here
            if (binding.homeDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                binding.homeDrawerLayout.closeDrawer(GravityCompat.START)
            } else {
                binding.homeDrawerLayout.openDrawer(GravityCompat.START)
            }
        }

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
            }
            true
        }
    }

    private fun showSearchBar() {
        binding.toolBar.searchMain.setOnClickListener {
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

            Log.i(Tags.TEMP_TAG.getTag(), "whattt")
            if (binding.toolBar.searchEditText.visibility == View.GONE) {
                Log.i(Tags.TEMP_TAG.getTag(), "whattt 2")
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
    }

//    @SuppressLint("ResourceType")
//    private fun initializePopUpMainMenu() {
//        binding.toolBar.menuMain.setOnClickListener { view ->
//            val popUpMenu = PopupMenu(this, view)
//
//            popUpMenu.menuInflater.inflate(R.menu.menu_main, popUpMenu.menu)
//
//            popUpMenu.setOnMenuItemClickListener { menuItem ->
//                when (menuItem.itemId) {
//                    R.id.home_item -> {
//                        navigation.toHomeFragment()
//                        true
//                    }
//                    R.id.popular_movie_item -> {
//                        true
//                    }
//                    else -> false
//                }
//            }
//            popUpMenu.gravity = Gravity.TOP
//            popUpMenu.show()
//        }
//    }


}