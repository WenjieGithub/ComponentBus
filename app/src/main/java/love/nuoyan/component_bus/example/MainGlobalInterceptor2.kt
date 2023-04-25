package love.nuoyan.component_bus.example

import android.util.Log
import love.nuoyan.component_bus.Chain
import love.nuoyan.component_bus.Result
import love.nuoyan.component_bus.interceptor.GlobalInterceptor

object MainGlobalInterceptor2 : GlobalInterceptor() {
    init {
        priority = 8
    }

    override suspend fun <T> intercept(chain: Chain): Result<T> {
        chain.request.componentName
        Log.e("MainGlobalInterceptor", "执行了 MainGlobalInterceptor2 8")
        return chain.proceed()
    }

    override fun <T> interceptSync(chain: Chain): Result<T> {
        chain.request.componentName
        Log.e("MainGlobalInterceptor", "执行了 MainGlobalInterceptor2 8 Sync")
        return chain.proceedSync()
    }
}