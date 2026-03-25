package com.example.sanchitra.data.repositories
import com.example.sanchitra.data.entities.ThumbnailType
import com.example.sanchitra.data.entities.toMovie
import com.example.sanchitra.data.util.StringConstants
import com.example.sanchitra.data.util.AssetsReader
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
