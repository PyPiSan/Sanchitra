package com.pypisan.sanchitra.data.util
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject

class AssetsReader @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun getJsonDataFromAsset(fileName: String, context: Context = this.context): Result<String> {
        return try {
            val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
            Result.success(jsonString)
        } catch (e: IOException) {
            Result.failure(e)
        }
    }
}
