package com.pypisan.sanchitra.presentation.screens.videoPlayer.components
import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.NextButtonState
import androidx.media3.ui.compose.state.rememberNextButtonState
import com.pypisan.sanchitra.data.util.StringConstants


@OptIn(UnstableApi::class)
@Composable
fun NextButton(
    player: Player,
    modifier: Modifier = Modifier,
    state: NextButtonState = rememberNextButtonState(player),
    onShowControls: () -> Unit = {},
) {
    VideoPlayerControlsIcon(
        icon = Icons.Default.SkipNext,
        isPlaying = player.isPlaying,
        contentDescription =
        StringConstants.Composable.VideoPlayerControlSkipNextButton,
        onShowControls = onShowControls,
        onClick = state::onClick,
        modifier = modifier
    )
}
