plugins {
    id("java")
}
tasks.named<Jar>("jar") {
    enabled = false
}
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
repositories {
    mavenCentral()
}