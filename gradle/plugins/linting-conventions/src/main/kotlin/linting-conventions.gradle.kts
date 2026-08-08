import org.jmailen.gradle.kotlinter.tasks.ConfigurableKtLintTask
import org.jmailen.gradle.kotlinter.tasks.FormatTask

plugins {
    id("org.jmailen.kotlinter")
}

tasks.withType<ConfigurableKtLintTask>().configureEach {
    exclude { it.file.path.contains("build${File.separator}") }
}

tasks.register("fmt") {
    group = "formatting"
    description = "Format sources in this project"

    dependsOn(tasks.withType<FormatTask>())
}
