package com.example.sanchitra.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import com.example.sanchitra.R

@Composable
fun DramaViewCompose(activity: FragmentActivity) {
    val fragmentTag = "DramaContentFragment"

    androidx.compose.ui.viewinterop.AndroidView(
        factory = { context ->
            val view = android.view.LayoutInflater.from(context).inflate(R.layout.fragment_drama_view, null)
            val fragmentContainer = view.findViewById<FragmentContainerView>(R.id.content_list)

            // Replicate original onViewCreated logic
            if (activity.supportFragmentManager.findFragmentByTag(fragmentTag) == null) {
                activity.supportFragmentManager.commit {
                    replace(fragmentContainer.id, CommonContentList(), fragmentTag)
                }
            }
            view
        },
        modifier = Modifier.fillMaxSize()
    )

    DisposableEffect(Unit) {
        onDispose {
            val fragment = activity.supportFragmentManager.findFragmentByTag(fragmentTag)
            if (fragment != null) {
                activity.supportFragmentManager.commit {
                    remove(fragment)
                }
            }
        }
    }
}
