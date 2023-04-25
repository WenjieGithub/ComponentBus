# ComponentBus
ComponentBus 用于组件（module）间的通信, 而不必相互依赖.

参考 example 实践示例

## 1. project build.gradle 内添加 ksp 插件
```groovy
plugins {
    id 'com.android.application' version '7.4.2' apply false
    id 'com.android.library' version '7.4.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.7.10' apply false

    id "com.google.devtools.ksp" version "1.7.10-1.0.6" apply false
    id 'love.nuoyan.android.component_bus_register' version '0.2.1' apply false
}
```

## 2. app module build.gradle 内添加插件、要扫描的jar包、ksp 生成文件引入
```groovy
plugins {
    id "com.google.devtools.ksp"
    id 'love.nuoyan.android.component_bus_register'
}

// 扫描列表会根据包名匹配需要扫描的类文件, 包含包内的子包下的文件
componentBusExt {
    // 需要扫描的进行添加(拦截器等包名, 可以是主包名)
    packageNameScanList = [
            "com.xxx.bbb",
            "com.xxx.aaa"
    ]
}
```

## 3. library module 内添加依赖和 ksp 插件及生成文件引用
```groovy
plugins {
    id "com.google.devtools.ksp"
}

// 添加依赖
api 'love.nuoyan.android:component_bus:0.2.1'
ksp "love.nuoyan.android:component_bus_processor:0.2.1"
```

## 4. module 内添加组件 API
```kotlin
@Component(componentName = "组件名称")
object MainComponent {

    @Action(actionName = "事件名称")
    fun event1(params1: Int, params2: Int): Boolean {
        return params1 > params2
    }

    // 挂起函数，必须使用 call 函数调用
    @Action(actionName = "事件名称2", interceptorName = ["拦截器类名称"])
    suspend fun event2(params1: String, params2: String): String {
        return params1 + params2
    }

    // 可以直接返回对应的 Result
    @Action(actionName = "事件名称3")
    fun event3(params1: Int, params2: Int): Result<Boolean> {
        return Result.resultError(code = 1, msg = "参数 params1 应在 0-10 之间")
    }
}
```

## 5. 添加拦截器
```kotlin
object TestInterceptor : IInterceptor {
    override suspend fun <T> intercept(chain: Chain): Result<T> {
        Log.e("TestInterceptor", "${chain.request.componentName} 执行了 TestInterceptor")
        return chain.proceed()
    }

    override fun <T> interceptSync(chain: Chain): Result<T> {
        Log.e("TestInterceptor", "${chain.request.componentName} 执行了 TestInterceptor Sync")
        return chain.proceedSync()
    }
}

object LoginInterceptor : IInterceptor {
    override suspend fun <T> intercept(chain: Chain): Result<T> {
        return if (UsercenterComponent.isLogin == true) {
            chain.proceed()
        } else {
            UsercenterComponent.showLogin()
            Result.resultError(-3, "拦截, 进入登录页")
        }
    }

    override fun <T> interceptSync(chain: Chain): Result<T> {
        return if (UsercenterComponent.isLogin == true) {
            chain.proceedSync()
        } else {
            UsercenterComponent.showLogin()
            Result.resultError(-3, "拦截, 进入登录页")
        }
    }
}
```

## 6. 添加全局拦截器
```kotlin
object LogGlobalInterceptor : GlobalInterceptor() {
    init {
        priority = 8    // 用于设置拦截器的优先级，越高执行顺序越靠前
    }
    
    override suspend fun <T> intercept(chain: Chain): Result<T> {
        Log.e("LogGlobalInterceptor", "${chain.request.componentName} 执行了 LogGlobalInterceptor 0")
        return chain.proceed()
    }

    override fun <T> interceptSync(chain: Chain): Result<T> {
        Log.e("LogGlobalInterceptor", "${chain.request.componentName} 执行了 LogGlobalInterceptor 0 Sync")
        return chain.proceedSync()
    }
}
```

## 7. 调用组件 Action
```kotlin
val result = ComponentBus.with("组件名称", "事件名称")
    .params("params1", 1)
    .interceptors("TestInterceptor")
    .apply {
        params["params2"] = 2   // 两种添加参数方式是等效的
        interceptors.add("TestInterceptor2")
    }
    .callSync<Boolean>()
```

## 8. AS 插件使用
搜索插件 ComponentBus 添加

![img.png](ComponentBusPlugin.gif)

### 1. 用于选择组件及事件
快捷键：option + B  
Tools -> ComponentBus -> ComponentSelect

### 2. 导航到该行代码使用的组件名称对应的类
ComponentBus.with("ComponentName", "ActionName")

### 3. 代码提示
输入匹配的组件名提示补全组件名称  
当首字母输入数字时，会显示全部组件名称  
提示所属组为 ComponentBus  
