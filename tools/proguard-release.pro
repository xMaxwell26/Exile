# Optional release obfuscation configuration. Put a vetted ProGuard 7.x jar at
# tools/proguard.jar before applying this configuration.
-injars release/0.1.0/client.jar
-outjars release/0.1.0/client-obfuscated.jar
-libraryjars <java.home>/lib/rt.jar
-keep public class com.koios.client.Client { public static void main(java.lang.String[]); }
-keep class com.koios.client.** { *; }
-keepattributes SourceFile,LineNumberTable,*Annotation*
-dontwarn **
