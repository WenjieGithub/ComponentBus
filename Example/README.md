# Android 快速搭建组件化框架

## 基本结构

### 项目基础依赖管理: 
- project 级 build.gradle 引用配置:
```groovy
apply from: 'https://raw.githubusercontent.com/WenjieGithub/ComponentBus/main/build_project.gradle'
```
- 此配置可自动为项目所有 module 配置依赖;
- 所有 module 都会依赖 component_bus 库, 所以每个 module 的配置都要添加 ksp 插件;
- 如果 module 名称以 lib_ 开始, 则认定为基础组件, 所有非 lib_ 开头的 module 都会依赖基础组件;
- local.properties 配置, 可控制非基础组件的引用依赖:   
    不配置, 默认全部 module 组件构建应用, 忽略 module 为空  
    allBuild 为 false 时, 为单独组件打包, 将打包组件配置到 needModule, 多个 module 间用逗号分开  
```properties
# 全部构建
allBuild = true
# allBuild = true 时生效，打包时忽略模块
ignoreModule = component_third_google
# allBuild = false 时生效，打包时需要的模块
needModule = component_statistics,component_launcher
```

### 添加 studio 插件
搜索插件 ComponentBus, 当前版本 0.0.1

### 组件是否上传 maven
可以这么做, 需要修改一些配置;
如果不是多项目公用组件, 并不需要这么做;

### 渐进式组件化项目
1. 先新建一个组件, 将老项目全部移植到这个组件内;
2. 根据规划, 逐步新加组件替换项目内的逻辑;

## 开始配置

### 1. 在项目目录下的 build.gradle 文件内配置:
```groovy
// 配置插件, component_bus_register 020 及以上版本仅支持 agp 7.4.2 以上
// 示例用了 agp 8.0.0
plugins {
//    id 'com.android.application' version '7.4.2' apply false
//    id 'com.android.library' version '7.4.2' apply false
//    id 'org.jetbrains.kotlin.android' version '1.7.10' apply false
//    id "com.google.devtools.ksp" version "1.7.10-1.0.6" apply false
    id 'com.android.application' version '8.0.0' apply false
    id 'com.android.library' version '8.0.0' apply false
    id 'org.jetbrains.kotlin.android' version '1.8.0' apply false
    id "com.google.devtools.ksp" version "1.8.20-1.0.10" apply false

    id 'love.nuoyan.android.component_bus_register' version '0.2.1' apply false
}

// 配置项目依赖
apply from: 'https://raw.githubusercontent.com/WenjieGithub/ComponentBus/main/build_project.gradle'

ext {
    compileSdkV = 33
    minSdkV = 23
    targetSdkV = 33
}
```

### 2. 将 module 的 gradle 配置抽取成 build_base.gradle 及 build_module.gradle

### 3. 新建 component_old module, 配置 build.gradle
```groovy
plugins {
    id 'com.android.library'
    id 'org.jetbrains.kotlin.android'
    id "com.google.devtools.ksp"
}
android {
    namespace 'love.nuoyan.android.component_old'
}

apply from: '../build_module.gradle'

dependencies {

}
```

### 4. 将老项目移至 module 内

### 5. 逐渐新建 module, 拆分老项目形成新组件

1. 需要整个项目共享的, 放到基础组件, 以 lib_ 作为 module 名称前缀;
2. 相对独立的业务逻辑, 以 component_ 作为 module 名称前缀;

## 总结

1. 这个项目是一个基本的Demo, 实际开发中会有不同的需求, 需要做相应的变更;
2. 组件 module 间是不能相互引用的, 但是通过 ComponentBus 和组件对外的 Api 可以打到相互调用的目的.
3. 全局拦截器的使用可以方便的添加日志, 普通拦截器可以方便的实现调用组件 API 时未登陆拦截并跳转登陆页;
4. local.properties 的配置可以方便的配置不同组件组合的 apk, 如果使用 Jenkins 等工具, 更可以自动化构建不同需求的 apk;