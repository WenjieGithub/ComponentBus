package cn.moltres.component_bus.interceptor

import cn.moltres.component_bus.Chain
import cn.moltres.component_bus.Result

/**
 * 拦截器
 */
interface IInterceptor {
    /**
     * 调用 chain.proceed() 来传递调用链或不调用来中止调用链的传递
     */
    suspend fun <T> intercept(chain: Chain): Result<T>
    fun <T> interceptSync(chain: Chain): Result<T>
}