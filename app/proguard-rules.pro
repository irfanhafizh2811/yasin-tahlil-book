# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/herisulistiyanto/Library/Android/sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-verbose
-optimizationpasses 5

-dontwarn com.android.**
-dontwarn com.squareup.**
-dontwarn okio.**
-dontwarn retrofit2.**
-dontwarn rx.**
-dontwarn com.google.**
-dontwarn android.support.**
-dontwarn java.lang.invoke.*
-dontwarn javax.annotation.**
-dontwarn javax.inject.**
-dontwarn org.slf4j.**

-keepattributes *Annotation*
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes Exceptions
-keepattributes EnclosingMethod
-keepattributes Deprecated

#COMMON JAVA
-keepnames class * implements java.io.Serializable { *;}
-keep class java.util.** { *; }
-keep class javax.inject.* { *; }

#ANDROID_COMPONENT
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-keep class android.support.v7.widget.** { *; }
-keep public class * extends android.app.Activity
-keep public class * extends android.support.v7.widget.** { *;}
-keep interface android.support.v7.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v4.** { *; }
-keep class android.support.v4.** { *; }
-keep class android.net.http.** { *; }
-keepclassmembers class android.net.http.** { *; }
-keep class android.support.** {
   public protected private *;
 }
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service {
*;
}
-keepclasseswithmembernames class * {
   native <methods>;
}
-keepclassmembers class **.R$* {
   public static <fields>;
}
-keepattributes InnerClasses
-keep class **.R
-keep class **.R$* {
    <fields>;
}
-keepclasseswithmembers class * {
   public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * {
   public void *ButtonClicked(android.view.View);
}
-keepclassmembers class android.os.Environment
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepnames class * implements java.io.Serializable { *;}
-keep class android.telephony.** { *; }
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keep class android.support.constraint.** { *; }

#DAGGER
-dontwarn dagger.internal.codegen.**
-keepclassmembers,allowobfuscation class * {
    @javax.inject.* *;
    @dagger.* *;
    <init>();
}
-keep class **$$ModuleAdapter
-keep class **$$InjectAdapter
-keep class **$$StaticInjection

#GLIDE
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#OKIO
-dontwarn okio.**

#OKHTTP
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

#OKHTTP3
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

#RETROFIT
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers class * {
    @retrofit2.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}

#GSON
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.* { *; }
-keep public class com.google.gson.** {
    public private protected *;
}
-keep class com.google.appengine.** { *; }
-keep class com.google.gson.* { *; }
-keep class com.google.inject.* { *; }
-keep class com.google.gson.stream.** { *; }
-keepclassmembers enum * { *; }

# Mixpanel
-dontwarn com.mixpanel.**

# SendBird
-dontwarn com.sendbird.android.shadow.**

# exclude module register and service
-dontwarn com.redkendi.consumer.consumerapp.ui.register.worker.**
-dontwarn com.redkendi.consumer.consumerapp.service.**

 # Add this global rule
-keepattributes Signature

# This rule will properly ProGuard all the model classes in
# the package com.yourcompany.models. Modify to fit the structure
# of your app.
-keepclassmembers class com.quran.almulk.** {
  *;
}

#UX-CAM
-keep class com.uxcam.** { *; }
-dontwarn com.uxcam.**

-keep class androidx.core.app.CoreComponentFactory { *; }

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** { <fields>; }