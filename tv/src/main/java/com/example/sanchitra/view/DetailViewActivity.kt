//package com.example.sanchitra.view
//
//import android.content.Intent
//import android.os.Bundle
//import android.util.Log
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.tv.material3.*
//import com.example.sanchitra.api.EpisodeBody
//import com.example.sanchitra.api.Title
//import com.example.sanchitra.data.models.EpisodeListModel
//import com.example.sanchitra.player.VideoPlayerActivity
//import com.example.sanchitra.utils.Constant
//import com.example.sanchitra.utils.RequestModule
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//
//class DetailViewActivity : ComponentActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val title = intent.getStringExtra("title") ?: ""
//        val summary = intent.getStringExtra("summary") ?: ""
//        val image = intent.getStringExtra("image") ?: ""
//        val release = intent.getStringExtra("release") ?: ""
//        val type = intent.getStringExtra("type") ?: ""
//
//        setContent {
//            MaterialTheme {
//                DetailScreen(
//                    title = title,
//                    summary = summary,
//                    image = image,
//                    release = release,
//                    type = type,
//                    onPlayClicked = { episode ->
//                        val playIntent = Intent(this, VideoPlayerActivity::class.java).apply {
//                            putExtra("title", title)
//                            putExtra("episode", episode)
//                            putExtra("type", type)
//                        }
//                        startActivity(playIntent)
//                    }
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalTvMaterial3Api::class)
//@Composable
//fun DetailScreen(
//    title: String,
//    summary: String,
//    image: String,
//    release: String,
//    type: String,
//    onPlayClicked: (String) -> Unit
//) {
//    var episodes by remember { mutableStateOf<List<EpisodeBody>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }
//
//    LaunchedEffect(title, type) {
//        val url = if (type == "anime") Constant.animeUrl else Constant.dramaUrl
//        val retrofit = Retrofit.Builder()
//            .baseUrl(url)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val request = retrofit.create(RequestModule::class.java)
//        request.getEpisodeList(Constant.key, Title(title)).enqueue(object : Callback<EpisodeListModel> {
//            override fun onResponse(call: Call<EpisodeListModel>, response: Response<EpisodeListModel>) {
//                val body = response.body()
//                if (response.isSuccessful && body?.success == true) {
//                    val details = body.data
//                    val numEpisodes = details?.episodes ?: 0
//                    val episodeList = (1..numEpisodes).map { i ->
//                        EpisodeBody(details?.title ?: title, i.toString(), details?.imageLink ?: image)
//                    }
//                    episodes = episodeList
//                }
//                isLoading = false
//            }
//
//            override fun onFailure(call: Call<EpisodeListModel>, t: Throwable) {
//                Log.e("DetailViewActivity", "Failed to fetch episodes", t)
//                isLoading = false
//            }
//        })
//    }
//
//    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF282828))) {
//        // Fallback or AndroidView for Glide can be placed here if needed
//        // For pure compose without glide compose library, we can omit it or use Coil.
//        // Since glide compose isn't available, we will just use a generic background container for now.
//        Box(
//            modifier = Modifier
//                .fillMaxWidth(0.7f)
//                .fillMaxHeight()
//                .align(Alignment.TopEnd)
//                .background(Color.DarkGray)
//        )
//
//        // Gradient overlay
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .background(
//                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
//                        colors = listOf(Color(0xFF282828), Color.Transparent),
//                        startX = 0f,
//                        endX = Float.POSITIVE_INFINITY
//                    )
//                )
//        )
//
//        Column(
//            modifier = Modifier
//                .fillMaxHeight()
//                .fillMaxWidth(0.5f)
//                .padding(start = 48.dp, top = 48.dp, bottom = 48.dp),
//            verticalArrangement = Arrangement.Top
//        ) {
//            Text(
//                text = title,
//                color = Color.White,
//                fontSize = 32.sp,
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Released: $release | Type: ${type.uppercase()}",
//                color = Color.LightGray,
//                fontSize = 14.sp
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//            Text(
//                text = summary,
//                color = Color.White,
//                fontSize = 14.sp,
//                maxLines = 7,
//                modifier = Modifier.padding(end = 16.dp)
//            )
//
//            Spacer(modifier = Modifier.height(32.dp))
//
//            if (isLoading) {
//                Text(text = "Loading episodes...", color = Color.White)
//            } else if (episodes.isNotEmpty()) {
//                Text(text = "Episodes", color = Color.White, fontSize = 20.sp)
//                Spacer(modifier = Modifier.height(16.dp))
//                LazyRow(
//                    horizontalArrangement = Arrangement.spacedBy(16.dp)
//                ) {
//                    items(episodes) { ep ->
//                        Card(
//                            onClick = { onPlayClicked(ep.episode) },
//                            modifier = Modifier
//                                .width(120.dp)
//                                .height(80.dp)
//                        ) {
//                            Box(
//                                modifier = Modifier
//                                    .fillMaxSize()
//                                    .background(Color.DarkGray),
//                                contentAlignment = Alignment.Center
//                            ) {
//                                Text(
//                                    text = "Episode ${ep.episode}",
//                                    color = Color.White,
//                                    fontSize = 14.sp
//                                )
//                            }
//                        }
//                    }
//                }
//            } else {
//                Text(text = "No episodes available.", color = Color.White)
//            }
//        }
//    }
//}
