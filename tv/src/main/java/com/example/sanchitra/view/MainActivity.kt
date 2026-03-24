package com.example.sanchitra.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.compose.foundation.layout.fillMaxSize
import com.example.sanchitra.R
import com.example.sanchitra.utils.Constant

@OptIn(ExperimentalTvMaterial3Api::class)
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        var selectedMenu by remember { mutableStateOf(Constant.MENU_HOME) }

        // Effect to update the fragment whenever the selectedMenu changes
        LaunchedEffect(selectedMenu) {
            if (selectedMenu == Constant.MENU_SEARCH) {
                // Navigate to SearchActivity and keep Home as selected
                startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                selectedMenu = Constant.MENU_HOME
            }
        }

        val tabs = listOf(
            Triple(Constant.MENU_SEARCH, R.string.search, R.drawable.baseline_search),
            Triple(Constant.MENU_HOME, R.string.home, R.drawable.baseline_home),
            Triple(Constant.MENU_ANIME, R.string.anime, R.drawable.anime_movie),
            Triple(Constant.MENU_DRAMA, R.string.drama, R.drawable.baseline_movie),
            Triple(Constant.MENU_TV, R.string.live_tv, R.drawable.baseline_live_tv),
            Triple(Constant.MENU_MOVIE, R.string.movies, R.drawable.baseline_local_movies),
            Triple(Constant.MENU_SETTINGS, R.string.settings, R.drawable.baseline_settings)
        )

        val selectedTabIndex = tabs.indexOfFirst { it.first == selectedMenu }.takeIf { it >= 0 } ?: 1

        Column(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F0F))) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 12.dp)
            ) {
                tabs.forEachIndexed { index, (menuId, textRes, iconRes) ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onFocus = { selectedMenu = menuId },
                        onClick = { selectedMenu = menuId }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = iconRes),
                                contentDescription = stringResource(id = textRes),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = textRes),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when (selectedMenu) {
                    Constant.MENU_ANIME -> AnimeViewCompose(this@MainActivity)
                    Constant.MENU_DRAMA -> DramaViewCompose(this@MainActivity)
                    Constant.MENU_TV -> TvViewCompose(this@MainActivity)
                    Constant.MENU_MOVIE -> MovieViewCompose(this@MainActivity)
                    Constant.MENU_HOME -> HomeScreen()
                    else -> HomeScreen()
                }
            }
        }
    }
}
