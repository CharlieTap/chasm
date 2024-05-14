import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask

plugins {
    id("org.jmailen.kotlinter")
}

tasks.withType<ConfigurableKtLintTask>().configureEach {
    exclude { it.file.path.contains("build${File.separator}") }
}
