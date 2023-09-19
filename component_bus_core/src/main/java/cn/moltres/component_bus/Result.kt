package cn.moltres.component_bus

/**
 * 组件调用的结果
 * 根据 code  来判断是否成功及失败类型;
 *              0  调用成功,
 *              -1 保留状态码：默认的请求错误, 调用过程中捕获异常
 *              -2 没有找到组件
 *              -3 没有找到组件中能处理此次请求的 Action
 *              -4 没有找到请求 Action 需要的必要参数
 *              -5 请求 Action 需要的必要参数，类型错误
 *              -6 请求 Action 是挂起函数，请使用 call 函数调用
 * 根据 msg   获取对应信息
 * 根据 data  来获取返回的内容
 */
data class Result<Data>(
    var code: Int = -1,
    var msg: String = "",
    var data: Data? = null
) {
    val isSuccess: Boolean
        get() = code == 0

    val isSuccessAndDataNotNull: Boolean
        get() = code == 0 && data != null

    companion object {
        fun <T> resultSuccess(data: T? = null, msg: String = "Success"): Result<T> {
            return Result(0, msg, data)
        }

        fun <T> resultError(code: Int = -1, msg: String): Result<T> {
            return Result(code, msg)
        }

        fun <T> resultErrorParams(code: Int = -4, paramsName: String, paramsType: String, isDefault: Boolean = false): Result<T> {
            return Result(code, when (code) {
                -4 -> "参数 $paramsName: $paramsType 是必要参数，并未找到该参数${if (isDefault) ", 暂不支持使用带默认值的参数" else ""}"
                -5 -> "参数 $paramsName 类型转换异常，需要 $paramsType 类型的参数"
                else -> "参数 $paramsName: $paramsType 是必要参数"
            })
        }
    }
}
