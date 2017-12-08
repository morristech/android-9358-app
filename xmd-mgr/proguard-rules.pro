# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/sdcm/Android/sdk/tools/proguard/proguard-android.txt
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

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keep class android.app.** {*;}

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

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keep public class com.xmd.manager.R$*{
    public static final int *;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# Gson specific classes
-keep class com.google.gson.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.xmd.manager.service.response.** { *; }
-keep class com.xmd.manager.beans.** { *; }

##---------------End: proguard configuration for Gson  ----------

-dontwarn com.umeng.**
-keep class com.umeng.** {*;}
-keep class com.alimama.mobile.** {*;}
-keep class u.upd.** {*;}
-keep class com.ut.device.**{*;}
-keep class com.ta.utdid2.**{*;}

-keep class android.support.annotation.** { *;}


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




#easemobe
-keep class com.hyphenate.** {*;}
-dontwarn  com.hyphenate.**

#升级
-dontwarn com.shidou.update.**
-keep class com.shidou.update.**{*;}


-dontwarn com.google.**
-keep class com.google.**{*;}

#RxJava
-dontwarn rx.**
-keep class rx.**{*;}

-dontwarn com.android.**
-keep class com.android.**{*;}

-dontwarn app.dinus.com.**
-keep class app.dinus.com.**{*;}
#permissions
-dontwarn com.zhy.m.**
-keep class com.zhy.m.** {*;}
-keep interface com.zhy.m.** { *; }
-keep class **$$PermissionProxy { *; }

-dontwarn com.qq.**
-keep class com.qq.**{*;}
-dontwarn src.com.qq.**
-keep class src.com.qq.**{*;}
-dontwarn com.tencent.**
-keep class com.tencent.**{*;}
-dontwarn FileCloud.**
-keep class FileCloud.**{*;}

-dontwarn com.xmd.app.**
-keep class com.xmd.app.** {*;}

-dontwarn org.greenrobot.**
-keep class org.greenrobot.** {*;}

-dontwarn com.xmd.appointment.**
-keep class com.xmd.appointment.** {*;}

-dontwarn com.xmd.m.network.**
-keep class com.xmd.m.network.** {*;}

-dontwarn com.xmd.m.notify.**
-keep class com.xmd.m.notify.** {*;}

-dontwarn com.xmd.permission.**
-keep class com.xmd.permission.** {*;}

-dontwarn com.xmd.m.**
-keep class com.xmd.m.** {*;}

-dontwarn com.xmd.chat.**
-keep class com.xmd.chat.** {*;}

-dontwarn com.xmd.contact.**
-keep class com.xmd.contact.** {*;}

-dontwarn com.xmd.black.**
-keep class com.xmd.black.** {*;}

-dontwarn com.xmd.inner.**
-keep class com.xmd.inner.** {*;}

-keep public class ** {
    @org.greenrobot.eventbus.Subscribe public <methods>;
}

#permission
-keep class com.xmd.permission.CheckBusinessPermission {*;}
-keep class * {
    @com.xmd.permission.CheckBusinessPermission public <methods>;
}

-keep class com.xmd.**.beans.** { *; }