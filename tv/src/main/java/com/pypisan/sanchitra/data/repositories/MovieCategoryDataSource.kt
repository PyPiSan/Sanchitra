package com.pypisan.sanchitra.data.repositories
import com.pypisan.sanchitra.data.entities.toMovieCategory
import com.pypisan.sanchitra.data.util.AssetsReader
import com.pypisan.sanchitra.data.util.StringConstants
import javax.inject.Inject

class MovieCategoryDataSource @Inject constructor(
    assetsReader: AssetsReader
) {

    private val movieCategoryDataReader = CachedDataReader {
        readMovieCategoryData(assetsReader, StringConstants.Assets.MovieCategories).map {
            it.toMovieCategory()
        }
    }

    suspend fun getMovieCategoryList() = movieCategoryDataReader.read()
}
