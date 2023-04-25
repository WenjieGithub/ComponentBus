package love.nuoyan.component_bus.example

import android.util.Log
import love.nuoyan.component_bus.Result
import love.nuoyan.component_bus.annotation.Action
import love.nuoyan.component_bus.annotation.Component

@Component(componentName = "Main")
object MainComponent {

    @Action(actionName = "showDialogSync")
    suspend fun showDialog1(f: ((String, MainComponent, ArrayList<MainActivity?>?)->String)?): String? {
        return f?.invoke("1111", MainComponent, null)
    }

    @Action(actionName = "showUserInfoSuspend", interceptorName = ["Login"])
    fun showUserInfo(params: Map<String, Any>, list: ArrayList<MainActivity>): Result<String> {
        Log.e("showUserInfo", params["key"] as String)

        val a = params["111"] as Int
        return Result.resultSuccess("成功了")
    }

    @Action(actionName = "showUserInfo1Suspend", interceptorName = ["Login"])
    suspend fun showUserInfo1(params: Map<String, Any>?): Result<String> {

        return Result.resultSuccess("成功了")
    }
}