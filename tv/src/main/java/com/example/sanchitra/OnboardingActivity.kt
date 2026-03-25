package com.example.sanchitra

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.example.sanchitra.api.UserInit
import com.example.sanchitra.data.models.UserModel
import com.example.sanchitra.utils.Constant
import com.example.sanchitra.utils.RequestModule
import com.example.sanchitra.utils.UnsafeOkHttpClient
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                OnboardingScreen()
            }
        }
    }

    @OptIn(ExperimentalTvMaterial3Api::class)
    @Composable
    fun OnboardingScreen() {
        var showSplash by remember { mutableStateOf(true) }

        LaunchedEffect(Unit) {
            // Initiate API call
            initUser()
            // Wait for splash duration
            delay(4000)
            showSplash = false
        }

        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .background(Color(0xFF141414))
        ) {
            AnimatedVisibility(
                visible = showSplash,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Companion.Center
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.ic_banner_foreground),
                        contentDescription = "App Logo",
                        modifier = Modifier.size(300.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            AnimatedVisibility(
                visible = !showSplash,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
//                Move to Main Activity
                ProfileSelectionScreen(
                    onProfileSelected = {
                        startActivity(Intent(this@OnboardingActivity, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }

    @OptIn(ExperimentalTvMaterial3Api::class)
    @Composable
    fun ProfileSelectionScreen(onProfileSelected: () -> Unit) {
        val profiles = listOf(
            Profile("Kids", R.drawable.baseline_account),
            Profile("Guest", R.drawable.baseline_account),
            Profile("Add Profile", R.drawable.baseline_account) // Use appropriate add icon if available
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Text(
                text = "Who's Watching?",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(profiles) { profile ->
                    ProfileCard(profile = profile, onClick = onProfileSelected)
                }
            }
        }
    }

    @OptIn(ExperimentalTvMaterial3Api::class)
    @Composable
    fun ProfileCard(profile: Profile, onClick: () -> Unit) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                onClick = onClick,
                modifier = Modifier.size(120.dp),
                shape = CardDefaults.shape(shape = CircleShape)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = profile.iconRes),
                        contentDescription = profile.name,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = profile.name,
                color = Color.White,
                fontSize = 18.sp
            )
        }
    }

    data class Profile(val name: String, val iconRes: Int)

    @SuppressLint("HardwareIds")
    private fun initUser() {
        val deviceUser = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ANDROID_ID
        )
        val myVersion = "Android " + Build.VERSION.RELEASE
        Constant.uid = deviceUser
        val origin = resources.configuration.locale.country

        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            Constant.versionName = pInfo.versionName
        } catch (ignored: PackageManager.NameNotFoundException) {
        }

        val client = if (BuildConfig.DEBUG) {
            UnsafeOkHttpClient.getUnsafeOkHttpClient()
        } else {
            OkHttpClient()
        }

        val retrofit = Retrofit.Builder()
            .baseUrl(Constant.userUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val getID = retrofit.create(RequestModule::class.java)
        val call = getID.getUser(
            UserInit(
                deviceUser, origin, myVersion,
                Constant.versionName, "tv"
            )
        )

        call.enqueue(object : Callback<UserModel> {
            override fun onResponse(call: Call<UserModel>, response: Response<UserModel>) {
                var flag = false
                val resource = response.body()
                Log.d("test", response.code().toString())

                if (response.code() == 200 && resource != null) {
                    flag = resource.userStatus ?: false
                    Constant.key = resource.apikey
                }

                if (flag && resource != null) {
                    if (resource.logged == true) {
                        Constant.loggedInStatus = true
                        Constant.logo = resource.icon ?: 0
                        Constant.userName = resource.userData ?: ""
                        if (resource.ads != null) {
                            Constant.isFree = resource.ads
                        }
                    } else {
                        Constant.loggedInStatus = false
                    }
                }
            }

            override fun onFailure(call: Call<UserModel>, t: Throwable) {
                Toast.makeText(this@OnboardingActivity, "Failed, Try Again $t", Toast.LENGTH_LONG).show()
            }
        })
    }
}