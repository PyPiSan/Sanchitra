package com.pypisan.sanchitra.presentation.screens.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.Dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.pypisan.sanchitra.data.models.UserProfileMap

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