package cn.moltres.component_bus

import cn.moltres.component_bus.interceptor.GlobalInterceptor
import cn.moltres.component_bus.interceptor.IInterceptor

object ComponentBus {
    internal val globalInterceptorArray = mutableListOf<GlobalInterceptor>()

    init {
        initGlobalInterceptor()
    }

    fun with(componentName: String, action: String): Request {
        return Request(componentName, action)
    }

    internal fun getComponent(componentName: String): IComponent? {
        return null
    }
    internal fun getInterceptor(interceptorName: String): IInterceptor? {
        return null
    }
    private fun initGlobalInterceptor() {
        globalInterceptorArray.sortByDescending{ it.priority }
    }
}