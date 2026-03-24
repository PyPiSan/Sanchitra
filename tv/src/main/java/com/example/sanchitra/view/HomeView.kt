package com.example.sanchitra.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen() {
    val dummyCategories = listOf(
        "Hotstar Exclusives" to listOf("Special Ops", "Aarya", "The Night Manager", "Criminal Justice", "City of Dreams", "Hostages"),
        "Netflix Originals" to listOf("Stranger Things", "The Crown", "Money Heist", "Bridgerton", "The Witcher", "Dark"),
        "Trending Now" to listOf("Movie 1", "Show 2", "Anime 3", "Drama 4", "Movie 5", "Show 6"),
        "Top Picks For You" to listOf("Recommendation A", "Recommendation B", "Recommendation C", "Recommendation D", "Recommendation E")
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 48.dp)
    ) {
        item {
            // Main Featured Hero Banner
            Card(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF3B1062), Color(0xFF140A26))
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Featured Title",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "New season now streaming. Dive into the world of action and adventure.",
                            fontSize = 16.sp,
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        items(dummyCategories.size) { index ->
            val (categoryName, titles) = dummyCategories[index]

            Column(modifier = Modifier.padding(bottom = 24.dp)) {
                Text(
                    text = categoryName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 12.dp, start = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    items(titles.size) { titleIndex ->
                        val title = titles[titleIndex]
                        Card(
                            onClick = { /*TODO*/ },
                            modifier = Modifier
                                .width(160.dp)
                                .aspectRatio(2f / 3f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFF2A2A2A)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    color = Color.White,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
