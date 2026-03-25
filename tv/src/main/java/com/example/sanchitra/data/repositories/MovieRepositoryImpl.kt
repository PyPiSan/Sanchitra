package com.example.sanchitra.data.repositories
import com.example.sanchitra.data.entities.MovieCategoryDetails
import com.example.sanchitra.data.entities.MovieDetails
import com.example.sanchitra.data.entities.ThumbnailType
import com.example.sanchitra.data.util.StringConstants
import com.example.sanchitra.data.entities.MovieList
import com.example.sanchitra.data.entities.MovieReviewsAndRatings
import com.example.sanchitra.data.util.StringConstants.Movie.Reviewer.DefaultCount
import com.example.sanchitra.data.util.StringConstants.Movie.Reviewer.DefaultRating
import com.example.sanchitra.data.util.StringConstants.Movie.Reviewer.FreshTomatoes
import com.example.sanchitra.data.util.StringConstants.Movie.Reviewer.ReviewerName
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val movieDataSource: MovieDataSource,
    private val tvDataSource: TvDataSource,
    private val movieCastDataSource: MovieCastDataSource,
    private val movieCategoryDataSource: MovieCategoryDataSource,
) : MovieRepository {

    override fun getFeaturedMovies() = flow {
        val list = movieDataSource.getFeaturedMovieList()
        emit(list)
    }

    override fun getTrendingMovies(): Flow<MovieList> = flow {
        val list = movieDataSource.getTrendingMovieList()
        emit(list)
    }

    override fun getTop10Movies(): Flow<MovieList> = flow {
        val list = movieDataSource.getTop10MovieList()
        emit(list)
    }

    override fun getNowPlayingMovies(): Flow<MovieList> = flow {
        val list = movieDataSource.getNowPlayingMovieList()
        emit(list)
    }

    override fun getMovieCategories() = flow {
        val list = movieCategoryDataSource.getMovieCategoryList()
        emit(list)
    }

    override suspend fun getMovieCategoryDetails(categoryId: String): MovieCategoryDetails {
        val categoryList = movieCategoryDataSource.getMovieCategoryList()
        val category = categoryList.find { categoryId == it.id } ?: categoryList.first()

        val movieList = movieDataSource.getMovieList().shuffled().subList(0, 20)

        return MovieCategoryDetails(
            id = category.id,
            name = category.name,
            movies = movieList
        )
    }

    override suspend fun getMovieDetails(movieId: String): MovieDetails {
        val movieList = movieDataSource.getMovieList()
        val movie = movieList.find { it.id == movieId } ?: movieList.first()
        val similarMovieList = movieList.subList(1, 4)
        val castList = movieCastDataSource.getMovieCastList()

        return MovieDetails(
            id = movie.id,
            videoUri = movie.videoUri,
            subtitleUri = movie.subtitleUri,
            posterUri = movie.posterUri,
            name = movie.name,
            description = movie.description,
            pgRating = "PG-13",
            releaseDate = "2021 (US)",
            categories = listOf("Action", "Adventure", "Fantasy", "Comedy"),
            duration = "1h 59m",
            director = "Larry Page",
            screenplay = "Sundai Pichai",
            music = "Sergey Brin",
            castAndCrew = castList,
            status = "Released",
            originalLanguage = "English",
            budget = "$15M",
            revenue = "$40M",
            similarMovies = similarMovieList,
            reviewsAndRatings = listOf(
                MovieReviewsAndRatings(
                    reviewerName = FreshTomatoes,
                    reviewerIconUri = StringConstants.Movie.Reviewer.FreshTomatoesImageUrl,
                    reviewCount = "22",
                    reviewRating = "89%"
                ),
                MovieReviewsAndRatings(
                    reviewerName = ReviewerName,
                    reviewerIconUri = StringConstants.Movie.Reviewer.ImageUrl,
                    reviewCount = DefaultCount,
                    reviewRating = DefaultRating
                ),
            ),
        )
    }

    override suspend fun searchMovies(query: String): MovieList {
        return movieDataSource.getMovieList().filter {
            it.name.contains(other = query, ignoreCase = true)
        }
    }

    override fun getMoviesWithLongThumbnail() = flow {
        val list = movieDataSource.getMovieList(ThumbnailType.Long)
        emit(list)
    }

    override fun getMovies(): Flow<MovieList> = flow {
        val list = movieDataSource.getMovieList()
        emit(list)
    }

    override fun getPopularFilmsThisWeek(): Flow<MovieList> = flow {
        val list = movieDataSource.getPopularFilmThisWeek()
        emit(list)
    }

    override fun getTVShows(): Flow<MovieList> = flow {
        val list = tvDataSource.getTvShowList()
        emit(list)
    }

    override fun getBingeWatchDramas(): Flow<MovieList> = flow {
        val list = tvDataSource.getBingeWatchDramaList()
        emit(list)
    }

    override fun getFavouriteMovies(): Flow<MovieList> = flow {
        val list = movieDataSource.getFavoriteMovieList()
        emit(list)
    }
}
