import org.gradle.internal.impldep.org.apache.http.client.methods.RequestBuilder.options

plugins {
    id("java")
    id("com.palantir.git-version") version "0.12.3"
}
val gitVersion: groovy.lang.Closure<String> by extra
version = gitVersion() // 自动调用 git describe
extra["gitVersionString"] = version.toString()
allprojects {
    extra["gitVersionString"] = version.toString()
}
println("ThePitUltimate version: $version")
tasks.named<Jar>("jar") {
    enabled = false
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs = listOf<String>("-Xlint:deprecation", "-Xlint:unchecked" )
}
tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
repositories {
    mavenCentral()
}