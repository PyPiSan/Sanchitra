package com.example.sanchitra.data.repositories
import com.example.sanchitra.data.entities.toMovieCast
import com.example.sanchitra.data.util.AssetsReader
import com.example.sanchitra.data.util.StringConstants
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
