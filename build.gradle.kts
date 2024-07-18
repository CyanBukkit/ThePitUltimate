plugins {
    kotlin("jvm") version "1.9.22"
    alias(libs.plugins.shadow)
}

group = "me.huanmeng"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://repo.inventivetalent.org/content/groups/public/")
    maven("https://jitpack.io")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.panda-lang.org/releases")
}

dependencies {
    api(libs.reflectionhelper)
    api(libs.hutool.core)
    api(libs.hutool.crypto)
    api(libs.book)
    api(libs.slf4j)
    api(libs.litecommands)
    api(libs.adventure.bukkit)

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")
    //we uses the local library from the paper 1.8.8 to get more efficient because
    //
    //    public net.minecraft.server.v1_8_R3.ItemStack handle; // Paper - public
    //
    //compileOnly(libs.spigot.get8())
    compileOnly(libs.protocollib)
    compileOnly(libs.luckperms)

    compileOnly(libs.papi)
    compileOnly(libs.narshorn)
    compileOnly(libs.protocollib)
    compileOnly(libs.httpclient)
    compileOnly(libs.httpcore)
    compileOnly(libs.jedis)
    compileOnly(libs.mongojack)
    compileOnly(libs.mongodb)
    compileOnly(libs.websocket)
    // fawe /we
    compileOnly(fileTree("libs"))
    // to get the proper api
    compileOnly("it.unimi.dsi:fastutil:8.5.13")

    compileOnly(libs.spigot.get8())
    compileOnly(libs.luckperms)
    compileOnly(libs.playerpoints)
    compileOnly(libs.decentholograms)
    compileOnly(libs.adventure.bukkit)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(11)
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