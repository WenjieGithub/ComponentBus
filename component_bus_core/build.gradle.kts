plugins {
    id("java-library")
    id("org.jetbrains.kotlin.jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    val isDev: Boolean by rootProject.extra
    if (isDev) {
        api(project(":component_bus_annotation"))
    } else {
        val componentBusVersion: String by rootProject.extra
        api("cn.moltres.component_bus:annotation:$componentBusVersion")
    }
}

