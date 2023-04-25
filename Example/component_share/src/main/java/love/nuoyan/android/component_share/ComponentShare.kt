package love.nuoyan.android.component_share

import android.app.Activity
import android.graphics.Bitmap
import love.nuoyan.component_bus.annotation.Action
import love.nuoyan.component_bus.annotation.Component

@Component(componentName = "Share")
object ComponentShare {
    @Action(actionName = "showShareDialog")
    fun showShareDialog(activity: Activity, shareTitle: String, shareBitmap: Bitmap): Boolean {
        // TODO 显示分享选择框
        return true
    }
}