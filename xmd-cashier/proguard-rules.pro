##---------------Begin: proguard configuration common for all Android apps ----------
#优化级别
-optimizationpasses 5
#不使用大小写混合
-dontusemixedcaseclassnames
#指定不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
#不做预校验
-dontpreverify
#打印详细信息
-verbose
#-dump class_files.txt
#-printseeds seeds.txt
#-printusage unused.txt
#生成mapping文件
-printmapping mapping.txt
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-allowaccessmodification
-repackageclasses ''


#保留SDK系统自带内容
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,*Annotation*,EnclosingMethod
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keep public class com.android.vending.licensing.ILicensingService
-dontnote com.android.vending.licensing.ILicensingService

-keep class android.app.** {*;}

#保留资源
-keepclasseswithmembers class **.R$* {
  public static <fields>;
}

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

#保留native函数
-keepclasseswithmembernames class * {
    native <methods>;
}

#保留enum
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保留parcelable
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保留serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#保留构造函数
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保留构造函数
-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}


# Gson specific classes
-keep class sun.misc.Unsafe { *; }

#保留所有bean包中的类和公开变量名,用于gson解析
-keepclasseswithmembernames class **.bean.**{*;}
-keepclasseswithmembernames class **.bean.**$*{*;}

-keep class com.xmd.cashier.dal.**{*;}
-keep class com.xmd.cashier.dal.**$*{*;}

#网络返回
-keep class com.xmd.cashier.dal.net.response.** {
    public <fields>;
}
-keep class com.xmd.cashier.dal.net.response.**$* {
    public <fields>;
}


#各类第三方库
-dontwarn com.android.**
-keep class com.android.**{*;}

#rxAndroid & rxJava
-dontwarn rx.**
-keep class rx.**{*;}

-dontwarn retrofit2.**
-keep class retrofit2.**{*;}

-dontwarn okhttp3.**
-keep class okhttp3.**{*;}

-dontwarn com.google.**
-keep class com.google.**{*;}
-keep class com.google.gson.** { *; }

-dontwarn com.github.**
-keep class com.github.**{*;}

-dontwarn cn.weipass.**
-keep class cn.weipass.**{*;}

-dontwarn org.hamcrest.**
-keep class org.hamcrest.**{*;}

-dontwarn okio.**
-keep class okio.**{*;}

-dontwarn android.**
-keep class android.**{*;}

-dontwarn com.umeng.**
-keep class com.umeng.**{*;}

-dontwarn com.iboxpay.**
-keep class com.iboxpay.**{*;}

-dontwarn com.xmd.app.**
-keep class com.xmd.app.** {*;}

-dontwarn org.greenrobot.**
-keep class org.greenrobot.** {*;}

# Getui
-dontwarn com.igexin.**
-dontwarn android.support.**
-keep class com.igexin.**{*;}
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep interface android.support.annotation.** { *;}
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }