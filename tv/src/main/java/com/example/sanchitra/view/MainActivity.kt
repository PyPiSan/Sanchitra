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

        NavigationDrawer(
            drawerContent = { drawerState ->
                Column(
                    modifier = Modifier
                        .background(Color(0xFF1C1C1E)) // Dark background for the side menu
                        .fillMaxHeight()
                        .padding(12.dp)
                ) {
                    val isOpen = drawerState == DrawerValue.Open

                    // User icon
                    NavigationDrawerItem(
                        selected = false,
                        onClick = {},
                        leadingContent = {
                            Icon(painterResource(id = R.drawable.baseline_account), contentDescription = "Account")
                        }
                    ) {
                        Text(stringResource(id = R.string.app_name))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    NavigationMenuItem(
                        isOpen = isOpen,
                        iconRes = R.drawable.baseline_search,
                        textRes = R.string.search,
                        isSelected = selectedMenu == Constant.MENU_SEARCH,
                        onClick = { selectedMenu = Constant.MENU_SEARCH }
                    )

                    NavigationMenuItem(
                        isOpen = isOpen,
                        iconRes = R.drawable.baseline_home,
                        textRes = R.string.home,
                        isSelected = selectedMenu == Constant.MENU_HOME,
                        onClick = { selectedMenu = Constant.MENU_HOME }
                    )

                    NavigationMenuItem(
                        isOpen = isOpen,
                        iconRes = R.drawable.anime_movie,
                        textRes = R.string.anime,
                        isSelected = selectedMenu == Constant.MENU_ANIME,
                        onClick = { selectedMenu = Constant.MENU_ANIME }
                    )

                    NavigationMenuItem(
                        isOpen = isOpen,
                        iconRes = R.drawable.baseline_movie,
                        textRes = R.string.drama,
                        isSelected = selectedMenu == Constant.MENU_DRAMA,
                        onClick = { selectedMenu = Constant.MENU_DRAMA }
                    )

                    NavigationMenuItem(
                        isOpen = isOpen,
                        iconRes = R.drawable.baseline_live_tv,
                        textRes = R.string.live_tv,
                        isSelected = selectedMenu == Constant.MENU_TV,
                        onClick = { selectedMenu = Constant.MENU_TV }
                    )

                    NavigationMenuItem(
                        isOpen = isOpen,
                        iconRes = R.drawable.baseline_local_movies,
                        textRes = R.string.movies,
                        isSelected = selectedMenu == Constant.MENU_MOVIE,
                        onClick = { selectedMenu = Constant.MENU_MOVIE }
                    )

                    NavigationMenuItem(
                        isOpen = isOpen,
                        iconRes = R.drawable.baseline_settings,
                        textRes = R.string.settings,
                        isSelected = selectedMenu == Constant.MENU_SETTINGS,
                        onClick = { selectedMenu = Constant.MENU_SETTINGS }
                    )
                }
            }
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.weight(1f)) {
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

    @Composable
    fun NavigationDrawerScope.NavigationMenuItem(
        isOpen: Boolean,
        iconRes: Int,
        textRes: Int,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        NavigationDrawerItem(
            selected = isSelected,
            onClick = onClick,
            leadingContent = {
                Icon(
                    painter = painterResource(id = iconRes),
                    contentDescription = stringResource(id = textRes)
                )
            }
        ) {
            Text(stringResource(id = textRes))
        }
    }
}
