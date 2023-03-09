# KuaiChuan[![](https://jitpack.io/v/wong1988/KuaiChuan.svg)](https://jitpack.io/#wong1988/KuaiChuan)

X快传

Step 1. Add it in your root build.gradle at the end of repositories:

 ```
 allprojects {
     repositories {
         ...
         maven { url 'https://jitpack.io' }
     }
 }
 ```

Step 2. Add the dependency

 ```
 dependencies {
     implementation 'com.github.wong1988:KuaiChuan:1.0.1'
     // 需要额外引入以下依赖
     implementation 'com.airbnb.android:lottie:4.2.1'
     implementation 'com.github.wong1988:MediaCenter:1.0.2'
     implementation 'com.github.wong1988:AndroidKit:1.2.4'
     implementation 'com.google.code.gson:gson:2.9.0'
     implementation 'com.github.wong1988:EasyAdapter:1.2.1'
     implementation 'com.github.bumptech.glide:glide:4.12.0'
 }
 ```

## Change Log

1.0.1:

* 加入开启Android小型服务的功能
* 加入对客户端请求处理的功能（图片、视频、音频、文档、应用、assets文件、下载等）
* 加入网页传的html
 
