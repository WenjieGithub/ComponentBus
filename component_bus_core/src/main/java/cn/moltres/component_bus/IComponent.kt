package cn.moltres.component_bus

/**
 * 组件接口
 * 1. 此接口的实现类代表的是一个组件暴露给外部调用的入口
 * 2. 实现类有且只有一个对象会被注册到组件库中
 */
abstract class IComponent {
    /**
     * 根据action名称添加对应拦截器
     */
    abstract fun getInterceptorNames(action: String): Array<String>?
    /**
     * 调用此组件时执行的方法
     * @param request 调用信息
     * @return 是否延迟回调结果
     */
    abstract suspend fun <T> onCall(request: Request): Result<T>
    abstract fun <T> onCallSync(request: Request): Result<T>
}