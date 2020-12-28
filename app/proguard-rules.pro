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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class org.jaudiotagger.tag.id3.framebody.* { *; }
-keep class org.jaudiotagger.tag.datatype.* { *; }
-dontwarn java.awt.geom.AffineTransform
-dontwarn java.awt.Graphics2D
-dontwarn java.awt.Image
-dontwarn java.awt.image.BufferedImage
-dontwarn javax.imageio.ImageIO
-dontwarn javax.imageio.ImageWriter
-dontwarn javax.imageio.stream.ImageInputStream
-dontwarn javax.swing.filechooser.FileFilter
-dontwarn sun.security.action.GetPropertyAction
-dontwarn java.nio.file.Paths
-dontwarn java.nio.file.OpenOption
-dontwarn java.nio.file.Files