package cn.moltres.component_bus.interceptor

import cn.moltres.component_bus.Chain
import cn.moltres.component_bus.IComponent

// 组件拦截器, 用于执行组件 API
class ComponentInterceptor(private val component: IComponent) : IInterceptor {
    override suspend fun <T> intercept(chain: Chain) = component.onCall<T>(chain.request)
    override fun <T> interceptSync(chain: Chain) = component.onCallSync<T>(chain.request)
}