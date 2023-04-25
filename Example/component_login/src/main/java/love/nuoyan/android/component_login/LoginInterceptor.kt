package love.nuoyan.android.component_login

import love.nuoyan.component_bus.Chain
import love.nuoyan.component_bus.Result
import love.nuoyan.component_bus.interceptor.IInterceptor

/**
 * 判断是否是登录的拦截器
 * 未登录会进入登录页面
 */
object LoginInterceptor : IInterceptor {
    override suspend fun <T> intercept(chain: Chain): Result<T> {
        return if (ComponentLogin.isLogin()) {
            chain.proceed()
        } else {
            ComponentLogin.showLoginPage()
            Result.resultError(-3, "拦截, 进入登录页")
        }
    }

    override fun <T> interceptSync(chain: Chain): Result<T> {
        return if (ComponentLogin.isLogin()) {
            chain.proceedSync()
        } else {
            ComponentLogin.showLoginPage()
            Result.resultError(-3, "拦截, 进入登录页")
        }
    }
}