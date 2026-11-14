package com.pypisan.sanchitra.utils

import android.content.Context
import android.util.Log
import com.google.android.engage.common.datamodel.ContinuationCluster
import com.google.android.engage.common.datamodel.Image
import com.google.android.engage.common.datamodel.PlatformSpecificUri
import com.google.android.engage.common.datamodel.PlatformType
import com.google.android.engage.service.AppEngagePublishClient
import com.google.android.engage.service.PublishContinuationClusterRequest
import com.google.android.engage.video.datamodel.MovieEntity
import com.google.android.engage.video.datamodel.WatchNextType
import androidx.core.net.toUri

data class ContinueWatchingItem(
    val id: String,
    val title: String,
    val playbackUri: String,
    val posterUrl: String,
    val durationMillis: Long,
    val playbackPositionMillis: Long,
    val lastEngagementTimeMillis: Long = System.currentTimeMillis(),
    val posterWidthPx: Int = 1280,
    val posterHeightPx: Int = 720,
    val ctaText: String = "Resume"
)

object EngageTvHelper {

    private const val TAG = "EngageTvHelper"
    private const val MAX_TV_CONTINUATION_ITEMS = 5

    /**
     * Convenience overload for pushing a single Continue Watching item.
     * Note: publishContinuationCluster replaces the entire Continue Watching list on the TV home screen.
     */
    fun publishContinueWatching(
        context: Context,
        item: ContinueWatchingItem,
        syncAcrossDevices: Boolean = false,
        onComplete: (Boolean) -> Unit = {}
    ) {
        publishContinueWatching(
            context = context,
            items = listOf(item),
            syncAcrossDevices = syncAcrossDevices,
            onComplete = onComplete
        )
    }

    /**
     * Publishes up to 5 Continue Watching items to the Google TV / Android TV home screen.
     * Passing an empty list clears the current items when the user finishes everything.
     */
    fun publishContinueWatching(
        context: Context,
        items: List<ContinueWatchingItem>,
        syncAcrossDevices: Boolean = false,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val client = AppEngagePublishClient(context.applicationContext)

        client.isServiceAvailable
            .addOnSuccessListener { isAvailable ->
                if (!isAvailable) {
                    Log.d(TAG, "Engage service is not available on this device.")
                    onComplete(false)
                    return@addOnSuccessListener
                }

                val clusterBuilder = ContinuationCluster.Builder()
                // Note: If enabling syncAcrossDevices = true, you should also set .setAccountProfile(...)
                clusterBuilder.setSyncAcrossDevices(syncAcrossDevices)

                items.take(MAX_TV_CONTINUATION_ITEMS).forEach { item ->
                    clusterBuilder.addEntity(item.toMovieEntity())
                }

                val request = PublishContinuationClusterRequest.Builder()
                    .setContinuationCluster(clusterBuilder.build())
                    .build()

                client.publishContinuationCluster(request)
                    .addOnSuccessListener {
                        Log.d(TAG, "Published ${items.size} Continue Watching item(s).")
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to publish Continue Watching cluster", e)
                        onComplete(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to check Engage service availability", e)
                onComplete(false)
            }
    }

    /**
     * Clears the Continue Watching cluster (e.g., on user logout).
     */
    fun clearContinueWatching(
        context: Context,
        onComplete: (Boolean) -> Unit = {}
    ) {
        val client = AppEngagePublishClient(context.applicationContext)

        client.isServiceAvailable
            .addOnSuccessListener { isAvailable ->
                if (!isAvailable) {
                    onComplete(false)
                    return@addOnSuccessListener
                }

                client.deleteContinuationCluster()
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Failed to delete Continue Watching cluster", e)
                        onComplete(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to check Engage service availability", e)
                onComplete(false)
            }
    }

    private fun ContinueWatchingItem.toMovieEntity(): MovieEntity {
        val uri = playbackUri.toUri()

        val tvUri = PlatformSpecificUri.Builder()
            .setPlatformType(PlatformType.TYPE_ANDROID_TV)
            .setActionUri(uri)
            .build()

        return MovieEntity.Builder()
            .setEntityId(id)
            .setName(title)
            .setPlayBackUri(uri)
            .addPlatformSpecificPlaybackUri(tvUri)
            .addPosterImage(
                Image.Builder()
                    .setImageUri(posterUrl.toUri())
                    .setImageWidthInPixel(posterWidthPx)
                    .setImageHeightInPixel(posterHeightPx)
                    .build()
            )
            .setDurationMillis(durationMillis)
            .setWatchNextType(WatchNextType.TYPE_CONTINUE)
            .setLastEngagementTimeMillis(lastEngagementTimeMillis)
            .setLastPlayBackPositionTimeMillis(playbackPositionMillis)
            .setCallToActionText(ctaText)
            .build()
    }
}

//EngageTvHelper.publishContinueWatching(
//    context = this,
//    item = ContinueWatchingItem(
//        id = "movie_123",
//        title = "Inception",
//        playbackUri = "myapp://watch/movie_123",
//        posterUrl = "https://example.com/poster_landscape.jpg",
//        durationMillis = 8880000L,
//        playbackPositionMillis = player.currentPosition
//    )
//)

//EngageTvHelper.publishContinueWatching(
//    context = context,
//    items = recentUnfinishedMovies
//)

//EngageTvHelper.clearContinueWatching(context)