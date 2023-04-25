package love.nuoyan.android.component_login

import love.nuoyan.component_bus.annotation.Action
import love.nuoyan.component_bus.annotation.Component

@Component(componentName = "Login")
object ComponentLogin {
    @Action(actionName = "isLogin")
    fun isLogin(): Boolean {
        return false
    }

    @Action(actionName = "showLoginPage")
    fun showLoginPage() {

    }

    // 使用 LoginInterceptor 拦截器, 调用时, 如果未登陆, 会被拦截进入登陆页
    @Action(actionName = "showUserInfoPage", interceptorName = ["LoginInterceptor"])
    fun showUserInfoPage() {

    }
}