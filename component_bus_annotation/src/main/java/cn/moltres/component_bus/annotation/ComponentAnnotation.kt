package cn.moltres.component_bus.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
annotation class Component(val componentName: String)

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.FUNCTION)
annotation class Action(val actionName: String, val interceptorName: Array<String> = [])
