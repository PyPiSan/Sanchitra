package com.pypisan.sanchitra.presentation.screens.movies

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import com.pypisan.sanchitra.R
import com.pypisan.sanchitra.data.entities.MovieReviewsAndRatings
import com.pypisan.sanchitra.data.entities.Videos
import com.pypisan.sanchitra.data.util.StringConstants
import com.pypisan.sanchitra.data.util.findActivity
import com.pypisan.sanchitra.presentation.common.Error
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding

object MovieDetailsScreen {
    const val MovieIdBundleKey = "movieId"
}

@Composable
fun MovieDetailsScreen(
    isPlayerActive: Boolean,
    openVideoPlayer: (metaId: String) -> Unit,
    onBackPressed: () -> Unit,
) {
    val context = LocalContext.current
    val activity = context.findActivity() as ComponentActivity

    val sharedVideoVM: VideoSharedViewModel = hiltViewModel(activity)
    val video by sharedVideoVM.selectedVideo.collectAsStateWithLifecycle()

    DisposableEffect(Unit) {
        onDispose { sharedVideoVM.clearVideo() }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .focusGroup()
    ) {
        when {
            video == null -> {
                Error(modifier = Modifier.fillMaxSize())
            }

            else -> {
                Details(
                    video = video!!,
                    isPlayerActive = isPlayerActive,
                    openVideoPlayer = openVideoPlayer,
                    onBackPressed = {
                        onBackPressed()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .animateContentSize()
                )
            }
        }
    }
}

@Composable
private fun Details(
    modifier: Modifier = Modifier,
    video: Videos,
    openVideoPlayer: (metaId: String) -> Unit,
    onBackPressed: () -> Unit,
    isPlayerActive: Boolean,
) {
    val childPadding = rememberChildPadding()

    BackHandler(onBack = onBackPressed)
    LazyColumn(
        contentPadding = PaddingValues(bottom = 135.dp),
        modifier = modifier,
    ) {
        item {
            MovieDetails(
                video = video,
                openVideoPlayer = openVideoPlayer,
                isPlayerActive =isPlayerActive
            )
        }


//        item {
//            MoviesRow(
//                title = StringConstants
//                    .Composable
//                    .movieDetailsScreenSimilarTo(video.title),
//                titleStyle = MaterialTheme.typography.titleMedium,
//                videoList = video,
//                onMovieSelected = refreshScreenWithNewMovie
//            )
//        }

        item {
            MovieReviews(
                modifier = Modifier.padding(top = childPadding.top), reviewsAndRatings = listOf(
                    MovieReviewsAndRatings(
                        StringConstants.Movie.Reviewer.RottenTomatoes,
                        StringConstants.Movie.Reviewer.RottenTomatoesImageUrl,
                        StringConstants.Movie.Reviewer.RottenTomatoesReviewCount,
                        video.meta.rottenTomatoes?:StringConstants.Movie.Reviewer.RottenTomatoesScore
                    ), MovieReviewsAndRatings(
                        StringConstants.Movie.Reviewer.IMDBReviewerName,
                        StringConstants.Movie.Reviewer.IMDBImageUrl,
                        StringConstants.Movie.Reviewer.IMDBDefaultCount,
                        video.meta.imdbRating?:StringConstants.Movie.Reviewer.IMDBDefaultRating
                    )
                )
            )
        }

        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = childPadding.start)
                    .padding(BottomDividerPadding)
                    .fillMaxWidth()
                    .height(1.dp)
                    .alpha(0.15f)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = childPadding.start),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val itemModifier = Modifier.width(192.dp)

                TitleValueText(
                    modifier = itemModifier,
                    title = stringResource(R.string.status),
                    value = StringConstants.Movie.StatusReleased
                )
                TitleValueText(
                    modifier = itemModifier,
                    title = stringResource(R.string.original_language),
                    value = video.language.joinToString(", ")
                )
                TitleValueText(
                    modifier = itemModifier,
                    title = stringResource(R.string.budget),
                    value = video.meta.budget?:StringConstants.Movie.BudgetDefault
                )
                TitleValueText(
                    modifier = itemModifier,
                    title = stringResource(R.string.revenue),
                    value = video.meta.revenue?: StringConstants.Movie.WorldWideGrossDefault
                )
            }
        }
    }
}

private val BottomDividerPadding = PaddingValues(vertical = 48.dp)
