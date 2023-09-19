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

    implementation("com.squareup:kotlinpoet:1.12.0")
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.10-1.0.6")

    // google的 auto service库,自动生成service,省去了手动配置resources/META-INF/services
//    implementation 'com.google.auto.service:auto-service-annotations:1.0.1'
//    kapt 'com.google.auto.service:auto-service:1.0.1'
}

