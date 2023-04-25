package love.nuoyan.android.lib_component.interceptor

import android.util.Log
import love.nuoyan.component_bus.Chain
import love.nuoyan.component_bus.interceptor.GlobalInterceptor

/**
 * 全局日志拦截器
 */
object LogGlobalInterceptor : GlobalInterceptor() {
    override suspend fun <T> intercept(chain: Chain) = chain.proceed<T>().apply {
        Log.v("Component", "Component: ${chain.request.componentName}  action: ${chain.request.action}  result: ($code) $msg")
    }
    override fun <T> interceptSync(chain: Chain) = chain.proceedSync<T>().apply {
        Log.v("Component", "Component: ${chain.request.componentName}  action: ${chain.request.action}  result: ($code) $msg")
    }
}