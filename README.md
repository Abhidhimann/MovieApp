# MovieApp by Abhishek

MovieApp is a application where you can find various movies and series, see reviews, recommendations, find trendings, watch trailers and much more.

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
