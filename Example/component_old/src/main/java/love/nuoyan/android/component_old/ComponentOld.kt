package love.nuoyan.android.component_old

import android.app.Activity
import android.graphics.Bitmap
import love.nuoyan.component_bus.annotation.Action
import love.nuoyan.component_bus.annotation.Component

@Component(componentName = "Old")
object ComponentOld {
    @Action(actionName = "showOldActivity")
    fun showOldActivity(activity: Activity) {
        // TODO 显示某个页面
    }
}