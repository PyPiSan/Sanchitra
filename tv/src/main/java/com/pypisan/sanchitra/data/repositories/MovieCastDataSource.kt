package com.pypisan.sanchitra.data.repositories
import com.pypisan.sanchitra.data.entities.toMovieCast
import com.pypisan.sanchitra.data.util.AssetsReader
import com.pypisan.sanchitra.data.util.StringConstants
import javax.inject.Inject

class MovieCastDataSource @Inject constructor(
    assetsReader: AssetsReader
) {

    private val movieCastDataReader = CachedDataReader {
        readMovieCastData(assetsReader, StringConstants.Assets.MovieCast).map {
            it.toMovieCast()
        }
    }

    suspend fun getMovieCastList() = movieCastDataReader.read()
}
