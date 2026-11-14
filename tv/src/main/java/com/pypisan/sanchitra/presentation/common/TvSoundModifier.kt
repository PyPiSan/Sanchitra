package com.pypisan.sanchitra.presentation.common

import android.content.Context
import android.media.SoundPool
import android.view.SoundEffectConstants
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalView
import com.pypisan.sanchitra.R

@Composable
fun Modifier.onTvFocusSound(
    soundEffect: Int = SoundEffectConstants.NAVIGATION_RIGHT
): Modifier {
    val view = LocalView.current
    var isInitialComposition by remember { mutableStateOf(true) }

    return this.onFocusChanged { focusState ->
        if (focusState.isFocused) {
            // Prevent playing sound when the screen first loads and autofocuses the 1st item
            if (!isInitialComposition) {
                view.playSoundEffect(soundEffect)
            } else {
                isInitialComposition = false
            }
        }
    }
}

object SoundHelper {
    private var soundPool: SoundPool = SoundPool.Builder()
        .setMaxStreams(2)
        .build()

    private var soundId: Int = 0

    fun init(context: Context) {
        // Loads from res/raw/tv_click.ogg
        soundId = soundPool.load(context, R.raw.tv_click, 1)
    }

    fun play() {
        soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
    }
}