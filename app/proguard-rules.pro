# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/sdcm/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-----------------------------------bugtags----------------------------------
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep class android.app.** {*;}

-keepattributes LineNumberTable,SourceFile

-keep class com.bugtags.library.** {*;}
-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.bugtags.library.vender.**
-dontwarn com.bugtags.library.**

#easymob
-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**

# Getui
-dontwarn com.igexin.**
-dontwarn android.support.**
-keep class com.igexin.**{*;}
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep interface android.support.annotation.** { *;}
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-dontwarn okio.**
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class com.google.gson.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.xmd.technician.http.gson.** { *; }
-keep class com.xmd.technician.http.gson.**$* { *; }
-keep class com.xmd.technician.bean.** { *; }
##---------------End: proguard configuration for Gson  ----------

-dontwarn com.umeng.**
-keep class com.umeng.** {*;}
-keep class com.alimama.mobile.** {*;}
-keep class u.upd.** {*;}

-keep class android.support.annotation.** { *;}
-keep class javax.annotation.** { *; }


# Weixin
-keep class com.tencent.mm.sdk.** {
   *;
}

#retrolambda
-dontwarn java.lang.invoke.*

#butterknife
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions

#RxJava
-dontwarn rx.internal.util.**