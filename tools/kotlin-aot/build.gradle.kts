import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    application
}

// Reuse Chasm's test-owned corpus host adapter, including its internal resources.
val chasmJar = project(":chasm").tasks.named<Jar>("jvmJar")
tasks.withType<KotlinJvmCompile>().configureEach {
    dependsOn(chasmJar)
    compilerOptions.freeCompilerArgs.add(chasmJar.flatMap { it.archiveFile }.map { "-Xfriend-paths=${it.asFile.absolutePath}" })
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("../../chasm/src/jvmTest/kotlin/io/github/charlietap/chasm/corpus")
    }
    dependencies {
        implementation(projects.chasm)
        implementation(projects.chasmCoroutines)
        implementation(projects.compiler.kotlin)
        implementation(projects.libs.benchmark)
        implementation(libs.corpus.lib)
        implementation(libs.wasi.emscripten.host.chasm.wasip1)
        implementation(libs.kotlinx.serialization)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.io.core)
        testImplementation(libs.kotlin.test)
    }
}

application {
    mainClass.set("io.github.charlietap.chasm.tools.aot.MainKt")
    applicationDefaultJvmArgs = listOf("-Xms1g", "-Xmx8g")
}

tasks.register("writeRuntimeClasspath") {
    val runtime = sourceSets.main.get().runtimeClasspath
    dependsOn(tasks.named("classes"), runtime)
    val output = layout.buildDirectory.file("runtime-classpath.txt")
    outputs.file(output)
    doLast { output.get().asFile.writeText(runtime.asPath) }
}
