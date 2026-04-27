package com.pypisan.sanchitra.presentation.screens.auth


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pypisan.sanchitra.data.models.UserProfileMap

@Composable
fun ProfileIconWithRing(
    profile: UserProfileMap,
    isFocused: Boolean,
    isFocusedByParent: Boolean,
    progress: Float,
    iconSize: Dp,
    ringSize: Dp,
    focusRequester: FocusRequester?,
    onSelected: () -> Unit
) {

    val iconScale by animateFloatAsState(
        targetValue = if (isFocused) 1.2f else 1f,
        animationSpec = tween(150),
        label = "iconScale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isFocused) 20.dp else 0.dp,
        label = "shadow"
    )

    val borderAlpha by animateFloatAsState(
        targetValue = if (isFocused) 1f else 0f,
        label = "borderAlpha"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(ringSize)
    ) {

        ProfileProgressRing(
            progress = progress,
            visible = isFocusedByParent,
            ringSize = ringSize
        )

        Surface(
            shape = CircleShape,
            color = Color(0xFF1a1a1a),
            border = BorderStroke(3.dp, Color.White.copy(alpha = borderAlpha)),
            modifier = Modifier
                .size(iconSize)
                .then(
                    if (focusRequester != null) Modifier.focusRequester(focusRequester)
                    else Modifier
                )
                .focusable()
                .onKeyEvent { event ->
                    if (event.type == KeyEventType.KeyDown &&
                        event.key == Key.DirectionCenter
                    ) {
                        onSelected()
                        true
                    } else {
                        false
                    }
                }
                .shadow(
                    elevation = elevation,
                    shape = CircleShape,
                    ambientColor = Color(0xFF00bbff),
                    spotColor = Color(0xFF00bbff)
                )
                .graphicsLayer {
                    scaleX = iconScale
                    scaleY = iconScale
                }
        ) {

            if (profile.imageUrl != null) {
                AsyncImage(
                    model = profile.imageUrl,
                    contentDescription = profile.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(iconSize * 0.2f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(id = profile.icon),
                    contentDescription = profile.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(iconSize * 0.2f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}