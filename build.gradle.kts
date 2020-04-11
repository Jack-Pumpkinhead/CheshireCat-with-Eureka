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

//If something went wrong, replace testImplementation to implementation
dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.5")
    testImplementation("io.kotest:kotest-runner-junit5-jvm:4.0.2") // for kotest framework
    testImplementation("io.kotest:kotest-assertions-core-jvm:4.0.2") // for kotest core jvm assertions
    implementation("org.apache.logging.log4j","log4j-api","2.13.1")
    implementation("org.apache.logging.log4j","log4j-core","2.13.1")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-api
    implementation("org.slf4j", "slf4j-api", "1.7.25")
    // https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-slf4j-impl
    implementation("org.apache.logging.log4j", "log4j-slf4j-impl", "2.13.1")
    implementation("io.github.microutils:kotlin-logging:1.7.9"){
        exclude("org.slf4j", "slf4j-api")
    }

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
    implementation("com.github.kotlin-graphics:vkk:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
        exclude("org.jetbrains.kotlin","kotlin-reflect")
    }
    implementation("com.github.kotlin-graphics:gln:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
        exclude("org.jetbrains.kotlin","kotlin-reflect")
    }
    testImplementation("com.github.kotlin-graphics.glm","glm-test","-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
        exclude("org.jetbrains.kotlin","kotlin-reflect")
        exclude("org.junit.jupiter","junit-jupiter-api")
        exclude("org.slf4j","slf4j-api")
    }
    implementation("com.github.kotlin-graphics:glm:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics.glm", "glm-test")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
    implementation("com.github.kotlin-graphics:gli:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
    }
    implementation("com.github.kotlin-graphics:kool:-SNAPSHOT")
//    implementation("com.github.kotlin-graphics:kotlin-unsigned:v3.2.4")
    implementation("com.github.kotlin-graphics:kotlin-unsigned:-SNAPSHOT")

    implementation("com.github.kotlin-graphics.uno-sdk:uno-core:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
        exclude("org.jetbrains.kotlin","kotlin-reflect")
    }
    implementation("com.github.kotlin-graphics:uno-sdk:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
        exclude("org.jetbrains.kotlin","kotlin-reflect")
    }
    implementation("com.github.kotlin-graphics.uno-sdk:build:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
        exclude("org.jetbrains.kotlin","kotlin-reflect")
    }
    implementation("com.github.kotlin-graphics.uno-sdk:uno-awt:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
        exclude("org.jetbrains.kotlin","kotlin-reflect")
    }
    implementation("com.github.kotlin-graphics.uno-sdk:uno-gl:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
        exclude("org.jetbrains.kotlin","kotlin-reflect")
    }
    implementation("com.github.kotlin-graphics.uno-sdk:uno-vk:-SNAPSHOT"){
        exclude("com.github.kotlin-graphics","vkk")
        exclude("com.github.kotlin-graphics","gln")
        exclude("com.github.kotlin-graphics.glm","glm")
        exclude("com.github.kotlin-graphics","gli")
        exclude("com.github.kotlin-graphics","uno-sdk")
        exclude("com.github.kotlin-graphics","kool")
        exclude("com.github.kotlin-graphics","kotlin-unsigned")
        exclude("org.jetbrains.kotlin","kotlin-reflect")
    }
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

}

/*
compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
compileTestKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
*/

