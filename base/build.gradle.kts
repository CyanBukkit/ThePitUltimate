import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("plugin.lombok") version "2.1.20"
    id("io.freefair.lombok") version "8.10"

    kotlin("jvm") version "2.1.20"
    alias(libs.plugins.shadow)
}

group = "me.huanmeng"
version = "1.0-SNAPSHOT"

val plugin_name = "Python"
val plugin_version = "1.0.1"
repositories {
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
tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set(plugin_name)
    archiveVersion.set(plugin_version)
    exclude("kotlin/**", "junit/**", "org/junit/**")
    from("build/tmp/processed-resources")
}


dependencies {
    api(libs.reflectionhelper)
    api(libs.hutool.core)
    api(libs.hutool.crypto)
    api(libs.book)
    api(libs.slf4j)
    api(libs.litecommands)
    api(libs.adventure.bukkit)
    compileOnly("com.caoccao.javet:javet:3.1.4") // Linux and Windows (x86_64)
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    //we uses the local library from the paper 1.8.8 to get more efficient because
    //
    //    public net.minecraft.server.v1_8_R3.ItemStack handle; // Paper - public
    //
    //compileOnly(libs.spigot.get8())
    compileOnly(libs.protocollib)
    compileOnly(libs.luckperms)
    compileOnly("com.github.ben-manes.caffeine:caffeine:3.1.8")
    implementation("com.github.f4b6a3:uuid-creator:6.0.0")
    compileOnly(libs.papi)
    compileOnly(libs.narshorn)
    compileOnly(libs.protocollib)
    compileOnly(libs.httpclient)
    compileOnly(libs.httpcore)
    compileOnly(libs.jedis)// https://mvnrepository.com/artifact/org.mongojack/mongojack
    compileOnly("org.mongojack:mongojack:5.0.1")
    compileOnly("org.mongodb:mongodb-driver-sync:5.2.0")

    compileOnly(libs.websocket)
    // fawe /we
    compileOnly(fileTree("../libs"))
    // to get the proper api
    compileOnly("it.unimi.dsi:fastutil:8.5.13")

    compileOnly("us.crazycrew.crazycrates:api:0.7")
    compileOnly(libs.spigot.get8())
    compileOnly(libs.luckperms)
    compileOnly(libs.playerpoints)
    compileOnly(libs.decentholograms)
    compileOnly(libs.adventure.bukkit)
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
tasks.register<Copy>("processPluginYml") {
    from("src/main/resources/plugin.yml")
    into("build/tmp/processed-resources")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    expand(
        "plugin_version" to plugin_version
    )
}


tasks.processResources {
    dependsOn("processPluginYml")
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from("build/tmp/processed-resources")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}