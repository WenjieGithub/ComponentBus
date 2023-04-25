package love.nuoyan.component_bus.example

import android.util.Log
import love.nuoyan.component_bus.Chain
import love.nuoyan.component_bus.Result
import love.nuoyan.component_bus.interceptor.GlobalInterceptor

object MainGlobalInterceptor3 : GlobalInterceptor() {
    init {
        priority = 5
    }

    override suspend fun <T> intercept(chain: Chain): Result<T> {
        chain.request.componentName
        Log.e("MainGlobalInterceptor", "执行了 MainGlobalInterceptor3 5")
        return chain.proceed()
    }

    override fun <T> interceptSync(chain: Chain): Result<T> {
        chain.request.componentName
        Log.e("MainGlobalInterceptor", "执行了 MainGlobalInterceptor3 5 Sync")
        return chain.proceedSync()
    }
}