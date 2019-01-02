# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.

#테스트
#-dontwarn android.support.**
#-dontwarn com.squareup.okhttp.**
#-keep class com.savvi.rangedatepicker.**
#-keep class com.thebrownarrow.**
#-keep class com.ittianyu.bottomnavigationviewex.**
#-keep class com.gun0912.tedpermission.**
#-keep class com.koushikdutta.**
#-dontwarn okio.**
#-dontwarn org.apache.lang.**
#-dontwarn java.io.**
#-dontwarn android.support.design.**
#-dontwarn com.hotelnow.utils.**
#-keep class android.databinding.** { *; }
#-keepnames class * implements java.io.Serializable
#-dontobfuscate
-keepattributes SoureFile,LineNumberTable   #소스파일, 라인 전보 유지
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn com.squareup.picasso.Transformation
-keep public class android.support.design.widget.BottomNavigationView { *; }
-keep public class android.support.design.internal.BottomNavigationMenuView { *; }
-keep public class android.support.design.internal.BottomNavigationPresenter { *; }
-keep public class android.support.design.internal.BottomNavigationItemView { *; }
# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
