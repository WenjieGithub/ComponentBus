// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("org.jetbrains.kotlin.jvm") version "1.7.10" apply false
}

val isDev by extra(true)
val groupId by extra("cn.moltres.component_bus")
val componentBusVersion by extra("0.3.5")
