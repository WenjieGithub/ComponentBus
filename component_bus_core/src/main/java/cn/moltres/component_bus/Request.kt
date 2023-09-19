package cn.moltres.component_bus

import cn.moltres.component_bus.interceptor.ComponentInterceptor
import cn.moltres.component_bus.interceptor.IInterceptor
import cn.moltres.component_bus.interceptor.NotFindComponentInterceptor

/**
 * 请求调用组件功能
 * @param componentName 组件名称
 * @param action 组件功能名称
 *
 * 添加组件的参数、拦截器，并调用挂起函数（call）或同步函数（callSync）请求结果
 */
class Request internal constructor(var componentName: String, var action: String) {
    // 组件调用使用的参数
    val params = mutableMapOf<String, Any>()
    // 组件调用使用的拦截器
    val interceptors = mutableListOf<String>()

    fun params(key: String, value: Any) = apply {
        params[key] = value
    }
    fun interceptors(interceptor: String) = apply {
        interceptors.add(interceptor)
    }
    suspend fun <T> call() = Chain(this@Request, *initInterceptor().toTypedArray()).proceed<T>()
    fun <T> callSync() = Chain(this@Request, *initInterceptor().toTypedArray()).proceedSync<T>()

    private fun initInterceptor() = mutableListOf<IInterceptor>().apply {
        // 添加完所有拦截器，再添加组件功能或NotFind异常
        addAll(ComponentBus.globalInterceptorArray)
        val component = ComponentBus.getComponent(componentName)
        if (component == null) {
            addInterceptor(this)
            add(size, NotFindComponentInterceptor())
        } else {
            component.getInterceptorNames(action)?.let { interceptors.addAll(it) }
            addInterceptor(this)
            add(size, ComponentInterceptor(component))
        }
    }
    private fun addInterceptor(interceptorArray: MutableList<IInterceptor>) {
        if (interceptors.isNotEmpty()) {
            for (name in interceptors) {
                ComponentBus.getInterceptor(name)?.let {
                    interceptorArray.add(interceptorArray.size, it)
                }
            }
        }
    }
}