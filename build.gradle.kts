plugins {
    kotlin("jvm") version "1.3.71"
}

tasks.withType<Test> {
    useJUnitPlatform()
}

java {
    group = "land.oz.munchkin"
    version = "1.0-SNAPSHOT"
    sourceCompatibility = JavaVersion.VERSION_14
//    targetCompatibility = JavaVersion.VERSION_1_8
}

val lwjglVersion = "3.2.3"
val jomlVersion = "1.9.22"
val lwjglNatives = "natives-windows"

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")

    implementation("com.google.guava:guava:28.2-jre")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))
    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-jemalloc")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-shaderc")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl-vma")
    implementation("org.lwjgl", "lwjgl-vulkan")

    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-jemalloc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-shaderc", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-vma", classifier = lwjglNatives)
    implementation("org.joml", "joml", jomlVersion)

//    duplicated
//    outdated
//    implementation("com.github.kotlin-graphics:vkk:v0.3.0"){
    implementation("com.github.kotlin-graphics:vkk:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
//    implementation("com.github.kotlin-graphics:gln:0.5.0"){
    implementation("com.github.kotlin-graphics:gln:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
//    implementation("com.github.kotlin-graphics:glm:v1.0.1"){
    implementation("com.github.kotlin-graphics:glm:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
//    implementation("com.github.kotlin-graphics:gli:v0.8.3.0-build-14"){
    implementation("com.github.kotlin-graphics:gli:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
//    implementation("com.github.kotlin-graphics:kool:v0.8.5")
    implementation("com.github.kotlin-graphics:kool:-SNAPSHOT")
//    implementation("com.github.kotlin-graphics:kotlin-unsigned:v3.2.4")

//    implementation("com.github.kotlin-graphics.uno-sdk:uno-core:v0.7.7"){
    implementation("com.github.kotlin-graphics.uno-sdk:uno-core:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
    implementation("com.github.kotlin-graphics:uno-sdk:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
    implementation("com.github.kotlin-graphics.uno-sdk:build:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
    implementation("com.github.kotlin-graphics.uno-sdk:uno-awt:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
    implementation("com.github.kotlin-graphics.uno-sdk:uno-gl:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
    implementation("com.github.kotlin-graphics.uno-sdk:uno-vk:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
//    implementation("com.github.kotlin-graphics.imgui:imgui-core:v1.75"){
    implementation("com.github.kotlin-graphics.imgui:imgui-core:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }

//    implementation("graphics.scenery:spirvcrossj:-SNAPSHOT"){ //not find
    implementation("graphics.scenery:spirvcrossj:0.7.0-1.1.106.0"){
        exclude("com.github.kotlin-graphics.uno-sdk")
    }

    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.0.2") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.0.2") // for kotest core jvm assertions
}

/*
compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
*/

