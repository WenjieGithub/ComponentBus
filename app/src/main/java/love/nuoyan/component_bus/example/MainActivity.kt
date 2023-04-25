package love.nuoyan.component_bus.example

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import love.nuoyan.component_bus.ComponentBus

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.text).setOnClickListener {
            lifecycleScope.launch {
                val result1 = ComponentBus.with("Main", "showDialogSync")
                    .params("f", { s: String -> s + "2222" })
                    .call<Boolean>()
                Log.e("MainInterceptor", "result = ${result1.msg}  ${result1.data}")
                Toast.makeText(this@MainActivity, "result = ${result1.msg}  ${result1.data}", Toast.LENGTH_LONG).show()
            }
        }
        val result = ComponentBus.with("Main", "showUserInfoSuspend")
            .params("key", "value")
            .interceptors("")
            .apply {
            params["key"] = "Value"
            interceptors.add("MainInterceptor")
        }.callSync<Boolean>()
        Toast.makeText(this@MainActivity, "result = ${result.msg}", Toast.LENGTH_LONG).show()
    }
}