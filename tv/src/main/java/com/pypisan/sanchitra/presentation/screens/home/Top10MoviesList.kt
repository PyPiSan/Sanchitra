package com.pypisan.sanchitra.presentation.screens.home
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import bringIntoViewIfChildrenAreFocused
import com.pypisan.sanchitra.R
import com.pypisan.sanchitra.data.entities.Movie
import com.pypisan.sanchitra.data.entities.MovieList
import com.pypisan.sanchitra.presentation.common.ItemDirection
import com.pypisan.sanchitra.presentation.common.PosterImage
import com.pypisan.sanchitra.presentation.common.ImmersiveListMoviesRow
import com.pypisan.sanchitra.presentation.screens.dashboard.rememberChildPadding

@Composable
fun Top10MoviesList(
    movieList: MovieList,
    modifier: Modifier = Modifier,
    gradientColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
    onMovieClick: (movie: Movie) -> Unit
) {
    var isListFocused by remember { mutableStateOf(false) }
    var selectedMovie by remember(movieList) { mutableStateOf(movieList.first()) }

    val sectionTitle = if (isListFocused) {
        null
    } else {
        stringResource(R.string.top_10_movies_title)
    }

    ImmersiveList(
        selectedMovie = selectedMovie,
        isListFocused = isListFocused,
        gradientColor = gradientColor,
        movieList = movieList,
        sectionTitle = sectionTitle,
        onMovieClick = onMovieClick,
        onMovieFocused = {
            selectedMovie = it
        },
        onFocusChanged = {
            isListFocused = it.hasFocus
        },
        modifier = modifier.bringIntoViewIfChildrenAreFocused(
            PaddingValues(bottom = 116.dp)
        )
    )
}

@Composable
private fun ImmersiveList(
    selectedMovie: Movie,
    isListFocused: Boolean,
    gradientColor: Color,
    movieList: MovieList,
    sectionTitle: String?,
    onFocusChanged: (FocusState) -> Unit,
    onMovieFocused: (Movie) -> Unit,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.BottomStart,
        modifier = modifier
    ) {
        Background(
            movie = selectedMovie,
            visible = isListFocused,
            modifier = modifier
                .height(432.dp)
                .gradientOverlay(gradientColor)
        )
        Column {
            if (isListFocused) {
                MovieDescription(
                    movie = selectedMovie,
                    modifier = Modifier.padding(
                        start = rememberChildPadding().start,
                        bottom = 40.dp
                    )
                )
            }

            ImmersiveListMoviesRow(
                movieList = movieList,
                itemDirection = ItemDirection.Horizontal,
                title = sectionTitle,
                showItemTitle = !isListFocused,
                showIndexOverImage = true,
                onMovieSelected = onMovieClick,
                onMovieFocused = onMovieFocused,
                modifier = Modifier.onFocusChanged(onFocusChanged)
            )
        }
    }
}

@Composable
private fun Background(
    movie: Movie,
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Crossfade(
            targetState = movie,
            label = "posterUriCrossfade",

        ) {
            PosterImage(movie = it, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun MovieDescription(
    movie: Movie,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = movie.name, style = MaterialTheme.typography.displaySmall)
        Text(
            modifier = Modifier.fillMaxWidth(0.5f),
            text = movie.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            fontWeight = FontWeight.Light
        )
    }
}

private fun Modifier.gradientOverlay(gradientColor: Color): Modifier =
    drawWithCache {
        val horizontalGradient = Brush.horizontalGradient(
            colors = listOf(
                gradientColor,
                Color.Transparent
            ),
            startX = size.width.times(0.2f),
            endX = size.width.times(0.7f)
        )
        val verticalGradient = Brush.verticalGradient(
            colors = listOf(
                Color.Transparent,
                gradientColor
            ),
            endY = size.width.times(0.3f)
        )
        val linearGradient = Brush.linearGradient(
            colors = listOf(
                gradientColor,
                Color.Transparent
            ),
            start = Offset(
                size.width.times(0.2f),
                size.height.times(0.5f)
            ),
            end = Offset(
                size.width.times(0.9f),
                0f
            )
        )

        onDrawWithContent {
            drawContent()
            drawRect(horizontalGradient)
            drawRect(verticalGradient)
            drawRect(linearGradient)
        }
    }
