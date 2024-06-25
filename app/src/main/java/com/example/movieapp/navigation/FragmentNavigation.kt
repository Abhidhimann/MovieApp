package com.example.movieapp.navigation

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.movieapp.ui.activities.HomePageUi
import com.example.movieapp.ui.fragments.common.ErrorFragment
import com.example.movieapp.ui.fragments.common.Home
import com.example.movieapp.ui.fragments.common.SavedItemFragment
import com.example.movieapp.ui.fragments.common.TrendingPaging
import com.example.movieapp.ui.fragments.movie.HomeMovieList
import com.example.movieapp.ui.fragments.series.HomeSeriesList
import com.example.movieapp.ui.fragments.movie.BaseMovieList
import com.example.movieapp.ui.fragments.movie.SearchResult
import com.example.movieapp.ui.fragments.series.BaseSeriesList
import com.example.movieapp.utils.Constants

class FragmentNavigation(private val fragmentManager: FragmentManager) {

    fun toHomeFragment(containerId: Int) {
        navigateToFragmentMain(homeFragment, containerId)
    }

    fun toSearchMovieFragment(query: String, containerId: Int) {
        navigateToFragmentMain(SearchResult.newInstance(query), containerId)
    }

    fun toBasePopularMoviesFragment(containerId: Int) {
        navigateToFragmentMain(basePopularMoviesList, containerId)
    }

    fun toBaseImdbMoviesFragment(containerId: Int) {
        navigateToFragmentMain(baseImdbMoviesList, containerId)
    }

    fun toBaseUpcomingMoviesFragment(containerId: Int) {
        navigateToFragmentMain(baseUpcomingMoviesList, containerId)
    }
    fun toBasePopularSeriesFragment(containerId: Int) {
        navigateToFragmentMain(basePopularSeriesList, containerId)
    }

    fun toBaseImdbSeriesFragment(containerId: Int) {
        navigateToFragmentMain(baseImdbSeriesList, containerId)
    }

    fun toBaseAiringSeriesFragment(containerId: Int) {
        navigateToFragmentMain(baseAiringSeriesList, containerId)
    }

    fun toTrendingPagingFragment(containerId: Int) {
        navigateToFragmentMain(trendingPaging, containerId)
    }
    fun toSavedItemFragment(containerId: Int) {
        navigateToFragmentMain(savedItemFragment, containerId)
    }

    fun toMovieListFragment(containerId: Int) {
        navigateToFragmentMain(homeMovieList, containerId)
    }

    fun toTvSeriesFragment(containerId: Int) {
        navigateToFragmentMain(homeSeriesList, containerId)
    }

    fun toErrorActivity(context: Context){
        val intent = Intent(context, HomePageUi::class.java)
        intent.putExtra(Constants.ERROR_PAGE.getValue(),true)
        context.startActivity(intent)
    }

    fun toErrorFragment(containerId: Int){

        navigateToFragmentMain(errorFragment, containerId )
    }


    private fun navigateToFragmentMain(fragment: Fragment, containerId: Int) {
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(containerId, fragment)
        // here not adding to backstack, don't want last state to be saved
        transaction.commit()
    }
    // if you want to add to backstack then before replace you have to give
    // login if fragment is already there then show it

//    private fun navigateToFragmentOnHomePage(fragment: Fragment, containerId: Int) {
//        fragmentManager.beginTransaction().replace(containerId, fragment).commit()
//    }

    companion object {
        val homeFragment = Home()
        val homeMovieList = HomeMovieList()
        val homeSeriesList = HomeSeriesList()
        val basePopularMoviesList =
            BaseMovieList.newInstance(Constants.MoviesType.POPULAR_MOVIES.value)
        val baseImdbMoviesList =
            BaseMovieList.newInstance(Constants.MoviesType.IMDB_RATED_MOVIES.value)
        val baseUpcomingMoviesList =
            BaseMovieList.newInstance(Constants.MoviesType.UPCOMING_MOVIES.value)

        val basePopularSeriesList =
            BaseSeriesList.newInstance(Constants.SeriesType.POPULAR_SERIES.value)
        val baseImdbSeriesList =
            BaseSeriesList.newInstance(Constants.SeriesType.IMDB_RATED_SERIES.value)
        val baseAiringSeriesList =
            BaseSeriesList.newInstance(Constants.SeriesType.AIRING_SERIES.value)
        val trendingPaging = TrendingPaging()
        val savedItemFragment = SavedItemFragment()

        val errorFragment = ErrorFragment()
    }
}