
import androidx.annotation.OptIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.state.PreviousButtonState
import androidx.media3.ui.compose.state.rememberPreviousButtonState
import com.example.sanchitra.data.util.StringConstants
import com.example.sanchitra.presentation.screens.videoPlayer.components.VideoPlayerControlsIcon

@OptIn(UnstableApi::class)
@Composable
fun PreviousButton(
    player: Player,
    modifier: Modifier = Modifier,
    state: PreviousButtonState = rememberPreviousButtonState(player),
    onShowControls: () -> Unit = {},
) {
    VideoPlayerControlsIcon(
        icon = Icons.Default.SkipPrevious,
        isPlaying = player.isPlaying,
        contentDescription =
            StringConstants.Composable.VideoPlayerControlSkipPreviousButton,
        onShowControls = onShowControls,
        onClick = state::onClick,
        modifier = modifier,
    )
}
