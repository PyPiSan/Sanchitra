
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod, Exceptions


# 1. Keep entire Media3 FFmpeg decoder package
-keep class androidx.media3.decoder.ffmpeg.** { *; }
-keepclassmembers class androidx.media3.decoder.ffmpeg.** { *; }

# 2. Keep all native JNI methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# 3. Keep Media3 Renderers and Decoders (needed for dynamic reflection loading)
-keep class * extends androidx.media3.exoplayer.Renderer {
    public <init>(...);
}
-keep class * extends androidx.media3.decoder.Decoder {
    public <init>(...);
}

-dontwarn androidx.media3.decoder.ffmpeg.**

# Keep all data models and their fields/constructors
-keepclassmembers class com.pypisan.sanchitra.data.models.** { *; }

# Keep API services & any Response/Result wrapper classes (e.g. ApiResponse<T>)
-keep interface com.pypisan.sanchitra.data.** { *; }
-keep class com.pypisan.sanchitra.data.entities.** { *; }
-keep class com.pypisan.sanchitra.data.models.** { *; }

# Keep any field with @SerializedName anywhere in the app
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}


# Keep TypeToken subclasses (Fixes generic List<T> / Model<T> casting)
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers class * extends com.google.gson.reflect.TypeToken { *; }

# Keep Retrofit interface methods and converters
-keepclassmembers interface * {
    @retrofit2.http.* <methods>;
}
-keep class * extends retrofit2.Converter$Factory
-keep class * extends retrofit2.CallAdapter$Factory

# Don't warn about missing platform dependencies in third-party libs
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.google.gson.**


-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

-dontwarn kotlinx.coroutines.**
-keepclassmembers class * extends kotlin.coroutines.jvm.internal.ContinuationImpl {
    <fields>;
}


-keepclasseswithmembernames class * {
    native <methods>;
}