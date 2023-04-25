package love.nuoyan.android.component_main

import love.nuoyan.component_bus.annotation.Action
import love.nuoyan.component_bus.annotation.Component

@Component(componentName = "Main")
object ComponentMain {
    @Action(actionName = "MainAction1")
    suspend fun action1(s: String): String {
        return "$s : MainAction1"
    }

    @Action(actionName = "MainAction2")
    fun action2(s: String): String {
        return "$s : MainAction2"
    }
}