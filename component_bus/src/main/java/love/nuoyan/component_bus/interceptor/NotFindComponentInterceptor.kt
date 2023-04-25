package love.nuoyan.component_bus.interceptor

import love.nuoyan.component_bus.Chain
import love.nuoyan.component_bus.Result

class NotFindComponentInterceptor : IInterceptor {
    override suspend fun <T> intercept(chain: Chain) = Result.resultError<T>(-2, "component not found: ${chain.request.componentName}")
    override fun <T> interceptSync(chain: Chain) = Result.resultError<T>(-2, "component not found: ${chain.request.componentName}")
}