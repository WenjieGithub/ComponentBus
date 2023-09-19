package cn.moltres.component_bus.processor

import cn.moltres.component_bus.annotation.Action
import cn.moltres.component_bus.annotation.Component
import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

class ComponentBusProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {
    private val packageName = "cn.moltres.component_bus"

    private fun log(msg: String) {
        environment.logger.warn("ksp--> $msg")
    }

    @OptIn(KspExperimental::class)
    override fun process(resolver: Resolver): List<KSAnnotated> {
        Component::class.qualifiedName?.let { componentName ->
            resolver.getSymbolsWithAnnotation(componentName).filterIsInstance<KSClassDeclaration>().forEach { cd ->
                if (cd.classKind == ClassKind.OBJECT) {
                    cd.getAnnotationsByType(Component::class).firstOrNull()?.let { component ->
                        val callFunSpec = addFunOnCallStart(cd.qualifiedName!!.asString())
                        val callSyncFunSpec = addFunOnCallSyncStart(cd.qualifiedName!!.asString())
                        val packageImportMap = mutableMapOf<String, String>()
                        val actionMapInterceptor = mutableMapOf<String, Array<String>>()
                        cd.getAllFunctions().forEach { fd ->
                            fd.getAnnotationsByType(Action::class).firstOrNull()?.let { action ->
                                addFunOnCallContent(callFunSpec, fd, action.actionName, packageImportMap)
                                addFunOnCallSyncContent(callSyncFunSpec, fd, action.actionName)
                                actionMapInterceptor[action.actionName] = action.interceptorName
                            }
                        }

                        val fileBuilder = FileSpec.builder(packageName, "${component.componentName}ComponentImpl")
                            .addType(
                                TypeSpec.objectBuilder("${component.componentName}ComponentImpl")
                                    .addModifiers(KModifier.PUBLIC)
                                    .superclass(ClassName.bestGuess("$packageName.IComponent"))
                                    .addFunction(addFunGetInterceptorNames(actionMapInterceptor))
                                    .addFunction(addFunOnCallEnd(callFunSpec))
                                    .addFunction(addFunOnCallSyncEnd(callSyncFunSpec))
                                    .build()
                            )
                            .apply {
                                packageImportMap.forEach { (name, packageN) ->
                                    addImport(packageN, name)
//                                    log("-------$packageN   $name")
                                }
                            }
                            .build()
                        environment.codeGenerator.createNewFile(
                            dependencies = Dependencies.ALL_FILES,
                            packageName = packageName,
                            fileName = "${component.componentName}ComponentImpl"
                        ).use { outputStream ->
                            try {
                                outputStream.writer().use { fileBuilder.writeTo(it) }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                outputStream.close()
                            }
                        }
                    }
                } else {
                    log("$cd : Component Annotation 应该使用在 Object 类型上")
                }
            }
        }
        return emptyList()
    }

    private fun addImportPackage(map: MutableMap<String, String>, name: String, packageName: String) {
        val typeName = name.split("<")[0].replace("?", "")
        map[typeName] = packageName
    }
    private fun addImportPackage(map: MutableMap<String, String>, type: KSType) {
        if (type.arguments.isNotEmpty()) {
            type.arguments.forEach {
                it.type?.resolve()?.let { t ->
                    val tn = delTypeAlias(t.toString())
                    val tp = t.declaration.packageName.asString()
                    addImportPackage(map, tn, tp)
                    if (t.arguments.isNotEmpty()) {
                        addImportPackage(map, t)
                    }
                }
            }
        }
    }
    private fun delTypeAlias(typeName: String): String {
        return if (typeName.startsWith("[typealias")) {
            typeName.replace("[typealias ", "").replace("]", "")
        } else {
            typeName
        }
    }

    private fun addFunGetInterceptorNames(map: Map<String, Array<String>>): FunSpec {
        val arrayType = ClassName("kotlin", "Array")
        val stringType = ClassName("kotlin", "String")
        val funSpec = FunSpec.builder("getInterceptorNames")
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("action", stringType)
            .returns(arrayType.parameterizedBy(stringType).copy(nullable = true))
//            .addStatement("")
            .beginControlFlow("return when (action)")
        for ((action, interceptors) in map) {
            var array = "null"
            if (interceptors.isNotEmpty()) {
                array = "arrayOf( "
                for (i in interceptors) {
                    array += "\"$i\", "
                }
                array = array.removeSuffix(", ")
                array += " )"
            }
            funSpec.beginControlFlow("\"$action\" ->")
                .addStatement(array)
                .endControlFlow()
        }
        return funSpec.beginControlFlow("else ->")
            .addStatement("null")
            .endControlFlow()
            .endControlFlow()
            .build()
    }

    private fun addFunOnCallStart(componentQualifiedName: String): FunSpec.Builder {
        return FunSpec.builder("onCall")
            .addTypeVariable(TypeVariableName("T"))
            .addModifiers(KModifier.SUSPEND)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("request", ClassName.bestGuess("$packageName.Request"))
            .returns(ClassName.bestGuess("$packageName.Result").parameterizedBy(TypeVariableName("T")))
            .addStatement("val component = $componentQualifiedName")
            .beginControlFlow("return when (request.action)")
    }
    private fun addFunOnCallContent(funSpec: FunSpec.Builder, function: KSFunctionDeclaration, action: String, map: MutableMap<String, String>): FunSpec.Builder {
        val sb = StringBuilder()
        val paramsList = mutableListOf<String>()
        function.parameters.forEach { vp ->
            vp.name?.getShortName()?.let { name ->
                val type = vp.type.resolve()
                val hasDefault = vp.hasDefault
                val typeName =  delTypeAlias(type.toString())
//                log("======$typeName")
//                log("======${type.arguments}")
                addImportPackage(map, typeName, type.declaration.packageName.asString())
                addImportPackage(map, type)
                paramsList.add(name)
                if (type.isMarkedNullable) {
                    sb.append(
                        """
                            var ${name}V: $typeName = null
                            if (request.params.containsKey("$name")) {
                              try {
                                ${name}V = request.params["$name"] as $typeName
                              } catch(e: Exception) {
                                return Result.resultErrorParams<T>(-5, 
                                  "$name", 
                                  "$type"
                                )
                              }
                            }
                        
                        """.trimIndent())
                } else {
                    sb.append(
                        """
                            val ${name}V: $typeName
                            when {
                              request.params.containsKey("$name") -> {
                                try {
                                  ${name}V = request.params["$name"] as $typeName
                                } catch(e: Exception) {
                                  return Result.resultErrorParams<T>(-5, 
                                    "$name", 
                                    "$type"
                                  )
                                }
                              }
                              $hasDefault -> return Result.resultErrorParams<T>(-4, 
                                "$name", 
                                "$type",
                                true
                              )
                              else -> return Result.resultErrorParams<T>(-4, 
                                "$name", 
                                "$type"
                              )
                            }
                            
                        """.trimIndent()
                    )
                }
            }
        }
        sb.append("val fr: Any? = component.${function.simpleName.getShortName()}(")
        paramsList.forEachIndexed { index, s ->
            if (index == paramsList.size - 1) {
                sb.append("$s = ${s}V")
            } else {
                sb.append("$s = ${s}V, ")
            }
        }
        sb.append(
            """
                )
                if (fr is Result<*>) {
                  fr as Result<T>
                } else {
                  Result.resultSuccess(fr as T)
                }
                
            """.trimIndent()
        )

        return funSpec.beginControlFlow("\"$action\" ->")
            .addCode(sb.toString())
            .endControlFlow()
    }
    private fun addFunOnCallEnd(funSpec: FunSpec.Builder): FunSpec {
        return funSpec.beginControlFlow("else ->")
            .addStatement("Result.resultError<T>(-3, \"组件中没有找到能处理此次请求的 Action (\${request.action})\")")
            .endControlFlow()
            .endControlFlow()
            .build()
    }

    private fun addFunOnCallSyncStart(componentQualifiedName: String): FunSpec.Builder {
        return FunSpec.builder("onCallSync")
            .addTypeVariable(TypeVariableName("T"))
            .addModifiers(KModifier.OVERRIDE)
            .addParameter("request", ClassName.bestGuess("$packageName.Request"))
            .returns(ClassName.bestGuess("$packageName.Result").parameterizedBy(TypeVariableName("T")))
            .addStatement("val component = $componentQualifiedName")
            .beginControlFlow("return when (request.action)")
    }
    private fun addFunOnCallSyncContent(funSpec: FunSpec.Builder, function: KSFunctionDeclaration, action: String): FunSpec.Builder {
        val sb = StringBuilder()
        val paramsList = mutableListOf<String>()
        if (function.modifiers.contains(Modifier.SUSPEND)) {
            sb.append("Result.resultError<T>(-6, \"请求的 Action 为挂起函数，请使用 call 函数调用\")")
        } else {
            function.parameters.forEach { vp ->
                vp.name?.getShortName()?.let { name ->
                    val type = vp.type.resolve()
                    val hasDefault = vp.hasDefault
                    val typeName = delTypeAlias(type.toString())
                    paramsList.add(name)
                    if (type.isMarkedNullable) {
                        sb.append(
                            """
                                var ${name}V: $typeName = null
                                if (request.params.containsKey("$name")) {
                                  try {
                                    ${name}V = request.params["$name"] as $typeName
                                  } catch(e: Exception) {
                                    return Result.resultErrorParams<T>(-5, 
                                      "$name", 
                                      "$type"
                                    )
                                  }
                                }
                        
                            """.trimIndent())
                    } else {
                        sb.append(
                            """
                                val ${name}V: $typeName
                                when {
                                  request.params.containsKey("$name") -> {
                                    try {
                                      ${name}V = request.params["$name"] as $typeName
                                    } catch(e: Exception) {
                                      return Result.resultErrorParams<T>(-5, 
                                        "$name", 
                                        "$type"
                                      )
                                    }
                                  }
                                  $hasDefault -> return Result.resultErrorParams<T>(-4, 
                                    "$name", 
                                    "$type",
                                    true
                                  )
                                  else -> return Result.resultErrorParams<T>(-4, 
                                    "$name", 
                                    "$type"
                                  )
                                }
                            
                            """.trimIndent()
                        )
                    }
                }
            }
            sb.append("val fr: Any? = component.${function.simpleName.getShortName()}(")
            paramsList.forEachIndexed { index, s ->
                if (index == paramsList.size - 1) {
                    sb.append("$s = ${s}V")
                } else {
                    sb.append("$s = ${s}V, ")
                }
            }
            sb.append(
                """
                    )
                    if (fr is Result<*>) {
                      fr as Result<T>
                    } else {
                      Result.resultSuccess(fr as T)
                    }
                
                """.trimIndent()
            )
        }
        return funSpec.beginControlFlow("\"$action\" ->")
            .addCode(sb.toString())
            .endControlFlow()
    }
    private fun addFunOnCallSyncEnd(funSpec: FunSpec.Builder): FunSpec {
        return funSpec.beginControlFlow("else ->")
            .addStatement("Result.resultError<T>(-3, \"组件中没有找到能处理此次请求的 Action (\${request.action})\")")
            .endControlFlow()
            .endControlFlow()
            .build()
    }
}