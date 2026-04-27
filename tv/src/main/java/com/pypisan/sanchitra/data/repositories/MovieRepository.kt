package com.pypisan.sanchitra.data.repositories
import com.pypisan.sanchitra.data.entities.MovieCategoryDetails
import com.pypisan.sanchitra.data.entities.MovieCategoryList
import com.pypisan.sanchitra.data.entities.MovieDetails
import com.pypisan.sanchitra.data.entities.MovieList
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getFeaturedMovies(): Flow<MovieList>
    fun getTrendingMovies(): Flow<MovieList>
    fun getTop10Movies(): Flow<MovieList>
    fun getNowPlayingMovies(): Flow<MovieList>
    fun getMovieCategories(): Flow<MovieCategoryList>
    suspend fun getMovieCategoryDetails(categoryId: String): MovieCategoryDetails
    suspend fun getMovieDetails(movieId: String): MovieDetails
    suspend fun searchMovies(query: String): MovieList
    fun getMoviesWithLongThumbnail(): Flow<MovieList>
    fun getMovies(): Flow<MovieList>
    fun getPopularFilmsThisWeek(): Flow<MovieList>
    fun getTVShows(): Flow<MovieList>
    fun getBingeWatchDramas(): Flow<MovieList>
    fun getFavouriteMovies(): Flow<MovieList>
}
