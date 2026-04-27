package com.pypisan.sanchitra.data.repositories
import com.pypisan.sanchitra.data.entities.ThumbnailType
import com.pypisan.sanchitra.data.entities.toMovie
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.data.util.AssetsReader
import javax.inject.Inject

class TvDataSource @Inject constructor(
    assetsReader: AssetsReader
) {
    private val mostPopularTvShowsReader = CachedDataReader {
        readMovieData(assetsReader, StringConstants.Assets.MostPopularTVShows)
    }

    suspend fun getTvShowList() = mostPopularTvShowsReader.read().subList(0, 5).map {
        it.toMovie(ThumbnailType.Long)
    }

    suspend fun getBingeWatchDramaList() = mostPopularTvShowsReader.read().subList(6, 15).map {
        it.toMovie()
    }
}
