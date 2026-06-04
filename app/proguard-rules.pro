# AgnesApp ProGuard Rules

# OkHttp & Retrofit
-dontwarn okhttp3.**
-dontwarn okio.**
-keepattributes Signature
-keepattributes Exceptions
-keepclassmembers class * implements okhttp3.Dispatcher{
    *;
}

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-dontnote sun.misc.Unsafe

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.resource.image.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# Kotlin Coroutines
-keepclassmembers,ignoreoptionalmembers {
  class kotlinx.coroutines.internal.** {
    *;
  }
}
-dontwarn kotlinx.coroutines.**
-keep class kotlinx.coroutines.internal.** { *; }
-keep class kotlinx.coroutines.** { *; }
-keep interface kotlinx.coroutines.** { *; }

# Agnes AI API Models
-keep class com.agnes.app.model.** { *; }
