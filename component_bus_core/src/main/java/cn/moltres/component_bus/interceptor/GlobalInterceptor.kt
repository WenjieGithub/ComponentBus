package cn.moltres.component_bus.interceptor

/**
 * 全局拦截器
 */
abstract class GlobalInterceptor : IInterceptor {
    /**
     * 全局拦截器的优先级，按从大到小的顺序执行
     */
    var priority: Int = 0

}