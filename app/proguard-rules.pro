# Add project specific ProGuard rules here.
-keep class * extends androidx.compose.runtime.internal.ComposableLambdaImpl { *; }
-keepclassmembers class **$WhenMappings {
    <fields>;
}
