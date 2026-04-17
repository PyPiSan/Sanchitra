package com.example.sanchitra.presentation.screens.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sanchitra.data.models.UserProfileMap

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ProfileArcItem(
    profile: UserProfileMap,
    isFocusedByParent: Boolean,
    timerProgress: Float,
    modifier: Modifier,
    iconSize: Dp,
    focusRequester: FocusRequester? = null,
    onFocusGained: () -> Unit,
    onSelected: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val ringSize = iconSize * 1.6f

    Row(
        modifier = modifier.onFocusChanged {
            if (isFocused != it.isFocused) {
                isFocused = it.isFocused
                if (it.isFocused) onFocusGained()
            }
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {

        ProfileText(profile.name, isFocused)

        ProfileIconWithRing(
            profile = profile,
            isFocused = isFocused,
            isFocusedByParent = isFocusedByParent,
            progress = timerProgress,
            iconSize = iconSize,
            ringSize = ringSize,
            focusRequester = focusRequester,
            onSelected = onSelected
        )
    }
}