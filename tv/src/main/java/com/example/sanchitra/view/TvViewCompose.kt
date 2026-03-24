package com.example.sanchitra.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity

@Composable
fun TvViewCompose(activity: FragmentActivity) {
    Box(modifier = Modifier.fillMaxSize().padding(start = 45.dp, top = 60.dp)) {
        TVContentCompose(activity)
    }
}
