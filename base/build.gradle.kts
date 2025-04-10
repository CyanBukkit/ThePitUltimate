import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("plugin.lombok") version "2.1.20"
    id("io.freefair.lombok") version "8.10"
    id("java-library")
    kotlin("jvm") version "2.1.20"
    alias(libs.plugins.shadow)
}

group = "me.huanmeng"
version = "4.01"
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
    archiveFileName.set("ThePitUltimate-$version.jar")
    relocate("pku.yim.license", "cn.charlotte.pit.license")
    relocate("panda","cn.charlotte.pit.libs")
    relocate("dev.rollczi","cn.charlotte.pit.libs")
    relocate("cn.hutool","cn.charlotte.pit.libs")
    relocate("io.irina","cn.charlotte.pit.libs")
    exclude("kotlin/**", "junit/**", "org/junit/**")
    from("build/tmp/processed-resources")
    mergeServiceFiles()
}


dependencies {
    compileOnly(fileTree(mapOf("dir" to "../libs", "include" to listOf("*.jar"))))
    implementation(fileTree(mapOf("dir" to "../libs", "include" to listOf("magic-license-1.0.7.jar"))))
    api(libs.reflectionhelper)
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