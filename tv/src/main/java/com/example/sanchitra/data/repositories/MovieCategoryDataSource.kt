package com.example.sanchitra.data.repositories
import com.example.sanchitra.data.entities.toMovieCategory
import com.example.sanchitra.data.util.AssetsReader
import com.example.sanchitra.data.util.StringConstants
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
