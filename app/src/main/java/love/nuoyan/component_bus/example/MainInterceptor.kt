package love.nuoyan.component_bus.example

import android.util.Log
import love.nuoyan.component_bus.Chain
import love.nuoyan.component_bus.Result
import love.nuoyan.component_bus.interceptor.IInterceptor

object MainInterceptor : IInterceptor {
    override suspend fun <T> intercept(chain: Chain): Result<T> {
        chain.request.componentName
        Log.e("MainInterceptor", "执行了 MainInterceptor")
        return chain.proceed()
    }

    override fun <T> interceptSync(chain: Chain): Result<T> {
        chain.request.componentName
        Log.e("MainInterceptor", "执行了 MainInterceptor Sync")
        return chain.proceedSync()
    }
}