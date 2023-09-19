package cn.moltres.component_bus

import cn.moltres.component_bus.interceptor.IInterceptor


class Chain internal constructor(val request: Request, private vararg val interceptors: IInterceptor) {
    private var index = 0

    suspend fun <T> proceed(): Result<T> {
        var interceptorName: String? = null
        return try {
            val interceptor = interceptors[index++]
            interceptorName = interceptor.javaClass.simpleName
            interceptor.intercept(this)
        } catch (e: Exception) {
            Result.resultError(msg = "Chain ## component:${request.componentName} action:${request.action} interceptor($interceptorName) error: ${e.stackTraceToString()}")
        }
    }

    @Synchronized
    fun <T> proceedSync(): Result<T> {
        var interceptorName: String? = null
        return try {
            val interceptor = interceptors[index++]
            interceptorName = interceptor.javaClass.simpleName
            interceptor.interceptSync(this)
        } catch (e: Exception) {
            Result.resultError(msg = "Chain ## component:${request.componentName} action:${request.action} interceptor($interceptorName) error: ${e.stackTraceToString()}")
        }
    }
}