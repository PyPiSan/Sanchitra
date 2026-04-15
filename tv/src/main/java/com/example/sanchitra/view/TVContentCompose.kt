//package com.example.sanchitra.view
//
//import android.content.Intent
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.tv.material3.Card
//import androidx.tv.material3.ExperimentalTvMaterial3Api
//import androidx.tv.material3.Text
//import com.example.sanchitra.data.models.TVChannelListModel.datum
//import androidx.compose.foundation.lazy.items
//import androidx.fragment.app.FragmentActivity
//import com.example.sanchitra.data.models.TVChannelListModel
//import com.example.sanchitra.player.VideoPlayerActivity
//import com.example.sanchitra.utils.Constant
//import com.example.sanchitra.utils.RequestModule
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import com.google.android.material.progressindicator.CircularProgressIndicator
//import androidx.compose.ui.viewinterop.AndroidView
//import androidx.compose.ui.platform.LocalContext
//import androidx.appcompat.view.ContextThemeWrapper
//import com.example.sanchitra.R
//import android.widget.ImageView
//import com.bumptech.glide.Glide
//import com.bumptech.glide.load.engine.DiskCacheStrategy
//import androidx.compose.foundation.shape.RoundedCornerShape
//
//@OptIn(ExperimentalTvMaterial3Api::class)
//@Composable
//fun TVContentCompose(activity: FragmentActivity) {
//    var isLoading by remember { mutableStateOf(true) }
//    var channelHeaders by remember { mutableStateOf<List<datum>>(emptyList()) }
//    val context = LocalContext.current
//
//    LaunchedEffect(Unit) {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(Constant.tvUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        val tvChannelRequest = retrofit.create(RequestModule::class.java)
//        val call = tvChannelRequest.getTvChannelList("e7y6acFyHGqwtkBLKHx6eA")
//
//        call.enqueue(object : Callback<TVChannelListModel> {
//            override fun onResponse(call: Call<TVChannelListModel>, response: Response<TVChannelListModel>) {
//                val resources = response.body()
//                if (response.isSuccessful && resources?.success == true) {
//                    channelHeaders = resources.data ?: emptyList()
//                } else {
//                    Log.d("TVContentCompose", "API Error: ${response.code()}")
//                }
//                isLoading = false
//            }
//
//            override fun onFailure(call: Call<TVChannelListModel>, t: Throwable) {
//                Log.e("TVContentCompose", "API Failure: ${t.message}")
//                isLoading = false
//            }
//        })
//    }
//
//    if (isLoading) {
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            AndroidView(
//                factory = { ctx ->
//                    val themeWrapper = ContextThemeWrapper(ctx, R.style.Theme_MaterialSpinner)
//                    CircularProgressIndicator(themeWrapper).apply {
//                        isIndeterminate = true
//                    }
//                }
//            )
//        }
//    } else {
//        androidx.compose.foundation.lazy.LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            contentPadding = PaddingValues(24.dp),
//            verticalArrangement = Arrangement.spacedBy(24.dp)
//        ) {
//            items(channelHeaders) { header ->
//                Column {
//                    Text(
//                        text = header.contentHeader ?: "",
//                        fontSize = 20.sp,
//                        fontWeight = FontWeight.SemiBold,
//                        color = Color.White,
//                        modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
//                    )
//
//                    androidx.compose.foundation.lazy.LazyRow(
//                        horizontalArrangement = Arrangement.spacedBy(16.dp),
//                        contentPadding = PaddingValues(horizontal = 8.dp)
//                    ) {
//                        items(header.contentList ?: emptyList()) { channel ->
//                            Card(
//                                onClick = {
//                                    val intent = Intent(activity, VideoPlayerActivity::class.java).apply {
//                                        putExtra("channel", channel.channelName)
//                                        putExtra("id", channel.channelId?.toString())
//                                        putExtra("logo", channel.logoUrl)
//                                        putExtra("language", channel.channelLanguageId?.toString())
//                                        putExtra("type", "tv")
//                                    }
//                                    activity.startActivity(intent)
//                                },
//                                modifier = Modifier
//                                    .width(160.dp)
//                                    .height(90.dp)
//                            ) {
//                                Box(
//                                    modifier = Modifier
//                                        .fillMaxSize()
//                                        .background(Color(0xFF2A2A2A))
//                                ) {
//                                    if (channel.logoUrl != null && channel.logoUrl.isNotEmpty()) {
//                                        AndroidView(
//                                            factory = { ctx ->
//                                                ImageView(ctx).apply {
//                                                    scaleType = ImageView.ScaleType.FIT_CENTER
//                                                }
//                                            },
//                                            update = { view ->
//                                                Glide.with(view.context)
//                                                    .load(channel.logoUrl)
//                                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                                                    .into(view)
//                                            },
//                                            modifier = Modifier.fillMaxSize()
//                                        )
//                                    } else {
//                                        Text(
//                                            text = channel.channelName ?: "",
//                                            color = Color.White,
//                                            modifier = Modifier.align(Alignment.Center)
//                                        )
//                                    }
//
//                                    // Live tag green dot
//                                    Row(
//                                        modifier = Modifier
//                                            .align(Alignment.TopEnd)
//                                            .padding(6.dp)
//                                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
//                                            .padding(horizontal = 6.dp, vertical = 2.dp),
//                                        verticalAlignment = Alignment.CenterVertically,
//                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
//                                    ) {
//                                        Box(
//                                            modifier = Modifier
//                                                .size(6.dp)
//                                                .background(Color.Green, CircleShape)
//                                        )
//                                        Text(
//                                            text = "LIVE",
//                                            color = Color.White,
//                                            fontSize = 8.sp,
//                                            fontWeight = FontWeight.Bold
//                                        )
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
