
# Keep Retrofit models
-keep class com.pypisan.sanchitra.data.model.** { *; }

# Keep Gson annotations
-keepattributes *Annotation*

# Keep generic type info
-keepattributes Signature

# Prevent Gson from breaking reflection
-keep class com.google.gson.reflect.TypeToken { *; }