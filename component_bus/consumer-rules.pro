-keep class love.nuoyan.component_bus.* {*;}
-keep class love.nuoyan.component_bus.interceptor.* {*;}
-keep class love.nuoyan.component_bus.annotation.* {*;}

# 不混淆 IInterceptor 接口的实现
-keepclasseswithmembers class * implements love.nuoyan.component_bus.interceptor.IInterceptor {
    <fields>;
    <methods>;
}