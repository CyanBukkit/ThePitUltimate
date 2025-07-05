import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import java.util.Scanner
import org.apache.tools.ant.filters.ReplaceTokens
import java.io.ByteArrayOutputStream

plugins {
    kotlin("plugin.lombok") version "2.1.20"
    id("io.freefair.lombok") version "8.10"
    kotlin("jvm") version "2.1.20"
    alias(libs.plugins.shadow)
}
var devBuild = true
if (devBuild) {
    println("WARN! 当前使用DevBuild模式构建!!,请详细斟酌是否构建")
}
group = "cn.klee"
version = "core"
repositories {

    maven("https://maven.cleanroommc.com")
    maven("https://maven.aliyun.com/repository/public/")
    mavenCentral()
    maven("https://repo.crazycrew.us/releases")
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.inventivetalent.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.panda-lang.org/releases")
}
tasks.named("compileKotlin") {
    dependsOn(injectGitVersion)
}
tasks.named("compileJava") {
    dependsOn(injectGitVersion)
}

val injectGitVersion by tasks.registering {
    group = "versioning"
    description = "Injects Git version into source code before compilation."

    val gitVersion = rootProject.extra["gitVersionString"].toString()

    val sourceDirs = listOf("src/main/java", "src/main/kotlin")

    doLast {
        sourceDirs.forEach { dir ->
            fileTree(dir) {
                include("**/*.kt", "**/*.java")
            }.forEach { file ->
                val original = file.readText()
                val updated = original.replace("%git_version%", gitVersion,false)
                if (original != updated) {
                    file.writeText(updated)
                    println("🔧 Patched: ${file.relativeTo(projectDir)}")
                }
            }
        }
        println("✅ Git version '$gitVersion' injected.")
    }
}

tasks.named<ShadowJar>("shadowJar") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    archiveFileName.set("ThePitUltimate-$version" + (if (devBuild) "-dev" else "") + ".jar")
    exclude("META-INF/**")
    relocate("pku.yim.license", "net.mizukilab.pit.license")
    relocate("panda", "net.mizukilab.pit.libs")
    relocate("dev.rollczi", "net.mizukilab.pit.libs")
    relocate("cn.hutool", "net.mizukilab.pit.libs")
    relocate("net.kyori", "net.mizukilab.pit.libs")
    relocate("net.jodah", "net.mizukilab.pit.libs")
    relocate("net.jitse", "net.mizukilab.pit.libs")
    relocate("xyz.upperlevel.spigot", "net.mizukilab.pit.libs")
    if (!devBuild) {
        exclude("org/**")
    }

    exclude("kotlin/**", "junit/**", "org/junit/**")
    from("build/tmp/processed-resources")
    mergeServiceFiles()
}
dependencies {
    var dependencyNotation = project(":base")
    compileOnly(dependencyNotation)
    if (devBuild) {
        implementation(dependencyNotation)
    }
    compileOnly(fileTree("../packLib"))
    compileOnly(fileTree(mapOf("dir" to "../libs", "include" to listOf("*.jar"))))
    if (devBuild) {
        api(libs.reflectionhelper)
    }else{
        compileOnly(libs.reflectionhelper)
    }
    compileOnly(libs.hutool.crypto)
    compileOnly(libs.book)
    compileOnly(libs.slf4j)
    compileOnly(libs.litecommands)
    compileOnly(libs.adventure.bukkit)
    compileOnly("com.caoccao.javet:javet:3.1.4")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    compileOnly(libs.luckperms)
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8")
    compileOnly("com.github.f4b6a3:uuid-creator:6.0.0")
    compileOnly(libs.papi)
    compileOnly(libs.narshorn)
    compileOnly(libs.protocollib)
    compileOnly(libs.jedis)
    compileOnly("org.mongojack:mongojack:5.0.1")
    compileOnly("org.mongodb:mongodb-driver-sync:5.2.0")

    compileOnly(fileTree("libs"))

    compileOnly(libs.fastutil)

    compileOnly("us.crazycrew.crazycrates:api:0.7")
    compileOnly(libs.spigot.get8())
    compileOnly(libs.luckperms)
    compileOnly(libs.playerpoints)
    compileOnly(libs.decentholograms)
    compileOnly(libs.adventure.bukkit)
    implementation(kotlin("reflect"))
}
kotlin {
    jvmToolchain(17)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks.build {
    dependsOn(tasks.shadowJar)
}