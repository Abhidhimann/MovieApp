# MovieApp by Abhishek

MovieApp is an Android application that lets users explore a wide range of Movies and TV series. It provides features like viewing detailed information about movies and series, reading reviews, discovering recommendations, watching YouTube trailers, and more.

## Tech Stacks

Common tech stacks used are:

- **Architecture** - MVVM Architecture.
- **Retrofit + OkHttp** - RESTful API and networking client.
- **Hilt** - For dependency injection.
- **Facebook Shimmer** - To add shimmer effects for enhanced UI loading.
- **Room** - Local persistence database, used for saving movies & series.
- **Paging 3** - Pagination loading for RecyclerView, used for infinite scrolling of movies & series.
- **Picasso** - Image loading library.
- **Coroutines** - Concurrency design pattern for asynchronous programming.
- **Mockito & PowerMock** - For unit testing and mocking dependencies.

## Features

- **Browse Movies and TV Series**: Discover various movies and series based on different selections.
- **Read Reviews**: Access user and critic reviews for each movie or series.
- **Get Recommendations**: Find personalized recommendations based on your current selections.
- **Trending Movies**: Stay updated with the latest trending movies and series.
- **Watch Trailers**: View YouTube trailers for upcoming and popular movies.
- **Search Functionality**: Easily find specific movies or series.
- **Infinite Scrolling**: Infinite scrolling of movies and series.

## DEMO

### 1. Basic Walkthrough & ScreenShots: 

### 2. Infinite scrolling using pagination: 

### 3. More Videos: [Click Here](https://drive.google.com/drive/folders/1MgSvUu1P8FUN5MOwWTIq4NeilNPcoKvi)

### 4. Apk File: [Movie App](https://github.com/Abhidhimann/MovieApp/blob/movie_app_with_di/app/release/app-release.apk.zip) 

Note: In some countries movie db api is not working, then please use vpn.

## Setting Up Your Environment

To build and run the project, you need to create a `credential.properties` file in the root directory of the project with your Movie DB API key.

### 1. Obtain Your API Key

1. Go to The Movie Database (TMDb) website: [TMDb API Documentation](https://developer.themoviedb.org/docs/getting-started)
2. Sign up for an account if you don’t already have one.
3. Navigate to the API section and generate a new API key.

### 2. Create `credential.properties` File

1. In the root directory of the project, create a file named `credential.properties`.
2. Add the following line to the `credential.propertiess` file:

   ```properties
   MOVIE_DB_API_KEY="your_api_key_here"
