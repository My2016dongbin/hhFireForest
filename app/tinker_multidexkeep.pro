

#tinker multidex keep patterns:
-keep public class * implements com.tencent.tinker.entry.ApplicationLifeCycle {
    <init>(...);
    void onBaseContextAttached(android.content.Context);
}

-keep public class * extends com.tencent.tinker.loader.TinkerLoader {
    <init>(...);
}

-keep public class * extends android.app.Application {
     <init>();
     void attachBaseContext(android.content.Context);
}

-keep class com.tencent.tinker.loader.TinkerTestDexLoad {
    <init>(...);
}

#your dex.loader patterns here
#注意 AndroidManifest.xml中的applicaion
-keep class com.haohai.platform.fireforestplatform.MyApplication {
    <init>(...);
}

-keep class com.tencent.tinker.loader.** {
    <init>(...);
}

