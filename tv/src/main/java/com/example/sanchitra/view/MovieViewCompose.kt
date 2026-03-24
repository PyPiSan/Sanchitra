package com.example.sanchitra.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.example.sanchitra.R

@Composable
fun MovieViewCompose(activity: FragmentActivity) {
    val fragmentTag = "MovieContentFragment"

    androidx.compose.ui.viewinterop.AndroidView(
        factory = { context ->
            val fragmentContainer = FragmentContainerView(context).apply {
                id = R.id.movieContentContainer
            }

            if (activity.supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
                activity.supportFragmentManager.commit {
                    replace(fragmentContainer.id, MovieContent(), fragmentTag)
                }
            }
            fragmentContainer
        },
        modifier = Modifier.fillMaxSize().padding(start = 45.dp, top = 60.dp)
    )

    DisposableEffect(Unit) {
        onDispose {
            val fragment = activity.supportFragmentManager.findFragmentByTag(fragmentTag)
            if (fragment != null) {
                if (!activity.supportFragmentManager.isStateSaved) {
                    activity.supportFragmentManager.commit {
                        remove(fragment)
                    }
                }
            }
        }
    }
}
