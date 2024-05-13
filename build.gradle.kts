plugins {
    kotlin("jvm") version "1.9.22"
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
    maven("https://maven.citizensnpcs.co/repo/")
    maven("https://mvn.lumine.io/repository/maven-public/")
}

dependencies {

    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    compileOnly(libs.spigot.get8())
    compileOnly(libs.protocollib)
    compileOnly(libs.luckperms)

    compileOnly(libs.papi)
    compileOnly(libs.narshorn)
    compileOnly(libs.citizens.main)
    compileOnly(libs.protocollib)
    compileOnly(libs.httpclient)
    compileOnly(libs.httpcore)
    compileOnly(libs.jedis)
    compileOnly(libs.book)
    compileOnly(libs.mongojack)
    compileOnly(libs.mongodb)
    compileOnly(libs.slf4j)
    compileOnly(libs.websocket)
    compileOnly(libs.reflectionhelper)
    // fawe /we
    compileOnly(fileTree("libs"))
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