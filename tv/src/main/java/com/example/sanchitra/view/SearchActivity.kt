package com.example.sanchitra.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.*
import com.example.sanchitra.model.CommonDataModel
import com.example.sanchitra.model.ContentModel
import com.example.sanchitra.utils.Constant
import com.example.sanchitra.utils.RequestModule
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                SearchScreen()
            }
        }
    }

    @OptIn(ExperimentalTvMaterial3Api::class)
    @Composable
    fun SearchScreen() {
        var query by remember { mutableStateOf("") }
        var searchResults by remember { mutableStateOf<List<ContentModel>>(emptyList()) }
        var isSearching by remember { mutableStateOf(false) }

        LaunchedEffect(query) {
            if (query.isNotEmpty()) {
                isSearching = true
                delay(500) // Debounce
                performSearch(query) { results ->
                    searchResults = results
                    isSearching = false
                }
            } else {
                searchResults = emptyList()
                isSearching = false
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF141414))
                .padding(24.dp)
        ) {
            // Search Input
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color.DarkGray, shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (query.isEmpty()) {
                    Text("Search for Anime or Drama...", color = Color.Gray, fontSize = 18.sp)
                }
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (isSearching) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Searching...", color = Color.White)
                }
            } else if (searchResults.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(searchResults) { content ->
                        Card(
                            onClick = {
                                val intent = Intent(this@SearchActivity, DetailViewActivity::class.java).apply {
                                    putExtra("title", content.title)
                                    putExtra("summary", content.summary)
                                    putExtra("image", content.image)
                                    putExtra("release", content.released)
                                    putExtra("type", content.type)
                                }
                                startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF2A2A2A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = content.title ?: "Unknown",
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        maxLines = 2
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = content.type?.uppercase() ?: "",
                                        color = Color.LightGray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            } else if (query.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No results found.", color = Color.White)
                }
            }
        }
    }

    private var currentAnimeCall: Call<CommonDataModel>? = null
    private var currentDramaCall: Call<CommonDataModel>? = null

    private fun performSearch(query: String, onResult: (List<ContentModel>) -> Unit) {
        currentAnimeCall?.cancel()
        currentDramaCall?.cancel()

        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.animeUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val request = retrofit.create(RequestModule::class.java)
        currentAnimeCall = request.getSearchResults(Constant.key, query)

        currentAnimeCall?.enqueue(object : Callback<CommonDataModel> {
            override fun onResponse(call: Call<CommonDataModel>, response: Response<CommonDataModel>) {
                if (call.isCanceled) return
                val animeResults = if (response.isSuccessful && response.body()?.success == true) {
                    response.body()?.results?.map { it.type = "anime"; it } ?: emptyList()
                } else emptyList()

                val dramaRetrofit = Retrofit.Builder()
                    .baseUrl(Constant.dramaUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val dramaRequest = dramaRetrofit.create(RequestModule::class.java)
                currentDramaCall = dramaRequest.getSearchResults(Constant.key, query)

                currentDramaCall?.enqueue(object : Callback<CommonDataModel> {
                    override fun onResponse(call: Call<CommonDataModel>, response: Response<CommonDataModel>) {
                        if (call.isCanceled) return
                        val dramaResults = if (response.isSuccessful && response.body()?.success == true) {
                            response.body()?.results?.map { it.type = "drama"; it } ?: emptyList()
                        } else emptyList()

                        onResult(animeResults + dramaResults)
                    }

                    override fun onFailure(call: Call<CommonDataModel>, t: Throwable) {
                        if (!call.isCanceled) onResult(animeResults)
                    }
                })
            }

            override fun onFailure(call: Call<CommonDataModel>, t: Throwable) {
                if (call.isCanceled) return
                val dramaRetrofit = Retrofit.Builder()
                    .baseUrl(Constant.dramaUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                val dramaRequest = dramaRetrofit.create(RequestModule::class.java)
                currentDramaCall = dramaRequest.getSearchResults(Constant.key, query)

                currentDramaCall?.enqueue(object : Callback<CommonDataModel> {
                    override fun onResponse(call: Call<CommonDataModel>, response: Response<CommonDataModel>) {
                        if (call.isCanceled) return
                        val dramaResults = if (response.isSuccessful && response.body()?.success == true) {
                            response.body()?.results?.map { it.type = "drama"; it } ?: emptyList()
                        } else emptyList()
                        onResult(dramaResults)
                    }

                    override fun onFailure(call: Call<CommonDataModel>, t: Throwable) {
                        if (!call.isCanceled) onResult(emptyList())
                    }
                })
            }
        })
    }
}
