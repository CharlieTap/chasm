import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.plugin.GradlePluginApiVersion
import org.gradle.api.file.FileCollection
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.GenerateMavenPom
import org.gradle.api.publish.tasks.GenerateModuleMetadata
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.testing.Test
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.build.config)
    `java-gradle-plugin`

    alias(libs.plugins.conventions.gradle.plugin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
}

group = "io.github.charlietap.chasm"
version = libs.versions.plugin.version.name.get()

fun MinimalExternalModuleDependency.notation(): String {
    return "$module:${versionConstraint.requiredVersion}"
}

val wasiPreview1Dependency = libs.wasi.emscripten.host.chasm.wasip1.kmp.get().notation()

buildConfig {
    buildConfigField("TOOLING_BINDGEN_DEPENDENCY", libs.tooling.bindgen.get().notation())
    buildConfigField("CHASM_DEPENDENCY", libs.chasm.kmp.get().notation())
    buildConfigField("CHASM_JVM_DEPENDENCY", libs.chasm.jvm.get().notation())
    buildConfigField("CHASM_COROUTINES_DEPENDENCY", libs.chasm.coroutines.kmp.get().notation())
    buildConfigField("CHASM_COROUTINES_JVM_DEPENDENCY", libs.chasm.coroutines.jvm.get().notation())
    buildConfigField("RUNTIME_AOT_DEPENDENCY", libs.runtime.aot.kmp.get().notation())
    buildConfigField("RUNTIME_AOT_JVM_DEPENDENCY", libs.runtime.aot.jvm.get().notation())
    buildConfigField("VM_DEPENDENCY", libs.vm.kmp.get().notation())
    buildConfigField("VM_JVM_DEPENDENCY", libs.vm.jvm.get().notation())
    buildConfigField("WASI_PREVIEW1_DEPENDENCY", wasiPreview1Dependency)
    buildConfigField("KOTLIN_POET_DEPENDENCY", libs.kotlin.poet.get().notation())
}

val publishingConventions = extensions.getByType<PublishingConventionsExtension>()
publishingConventions.apply {
    name = "chasm-gradle-plugin"
    description = "A gradle plugin for generating a typesafe Kotlin interface from a wasm binary"
}

val chasmPluginId = libs.plugins.chasm.get().pluginId
val minimumGradleVersion = "9.1"
val minimumAgpGradleVersion = "9.1.0"
val testedGradleVersions = listOf("9.1.0", "9.5.0", "9.6.1", "9.7.1", "9.8.2")
// AGP 9.4 needs Gradle 9.6 or newer.
val testedAgpGradleVersions = listOf("9.6.1", "9.7.1", "9.8.2")
val kotlinStdlibModule = libs.kotlin.stdlib.get().module

gradlePlugin {
    plugins {
        create("chasm-gradle-plugin") {
            id = chasmPluginId
            implementationClass = "io.github.charlietap.chasm.gradle.ChasmPlugin"
        }
    }
}

listOf("apiElements", "runtimeElements").forEach { configurationName ->
    configurations.named(configurationName) {
        attributes.attribute(
            GradlePluginApiVersion.GRADLE_PLUGIN_API_VERSION_ATTRIBUTE,
            objects.named(minimumGradleVersion),
        )
    }
}

kotlin {

    @OptIn(ExperimentalAbiValidation::class)
    abiValidation()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.compiler.version.get().toInt()))
    }

    dependencies {
        compileOnly(libs.kotlin.gradle.plugin)
        compileOnly(libs.android.gradle.plugin)
        compileOnly(libs.kotlin.stdlib)

        compileOnly(projects.tooling.bindgen)
        compileOnly(libs.kotlin.poet)
    }
}

configurations.named("implementation") {
    dependencies.removeIf { dependency ->
        dependency.group == kotlinStdlibModule.group && dependency.name == kotlinStdlibModule.name
    }
}

val functionalTestSourceSet = sourceSets.create("functionalTest")

dependencies {
    add(
        functionalTestSourceSet.implementationConfigurationName,
        sourceSets.main.get().output,
    )
    add(functionalTestSourceSet.implementationConfigurationName, gradleTestKit())
    add(functionalTestSourceSet.implementationConfigurationName, libs.kotlin.junit)
}

val functionalTest = tasks.register<Test>("functionalTest") {
    description = "Runs the Gradle plugin functional tests"
    group = LifecycleBasePlugin.VERIFICATION_GROUP
    testClassesDirs = functionalTestSourceSet.output.classesDirs
    classpath = functionalTestSourceSet.runtimeClasspath
    shouldRunAfter(tasks.test)
}

tasks.check {
    dependsOn(functionalTest)
}

val functionalTestRepositoryDependencies = configurations.dependencyScope("functionalTestRepositoryDependencies")
val functionalTestRepositories = configurations.resolvable("functionalTestRepositories") {
    description = "JVM repositories consumed by the Gradle plugin functional tests"
    extendsFrom(functionalTestRepositoryDependencies.get())
    attributes {
        attribute(
            Category.CATEGORY_ATTRIBUTE,
            objects.named(Category::class.java, "functional-test-repository"),
        )
    }
}

dependencies {
    add(functionalTestRepositoryDependencies.name, projects.chasm)
    add(functionalTestRepositoryDependencies.name, projects.tooling.bindgen)
    add(functionalTestRepositoryDependencies.name, projects.chasmAot)
    add(functionalTestRepositoryDependencies.name, projects.chasmCoroutines)
    add(functionalTestRepositoryDependencies.name, projects.runtimeAot)
    add(functionalTestRepositoryDependencies.name, projects.vm)
}

fun publicationPom(publicationName: String): FileCollection = files(
    tasks.withType<GenerateMavenPom>().matching { task ->
        val publication = publishing.publications.findByName(publicationName)
        publication is MavenPublication && task.pom === publication.pom
    },
)

fun publicationModuleMetadata(publicationName: String): FileCollection = files(
    tasks.withType<GenerateModuleMetadata>().matching { task ->
        task.publication.orNull?.name == publicationName
    },
)

val minimumAgpVersion = libs.versions.minimum.android.build.tools.plugin.get()
val stageFunctionalTestRepository = tasks.register<Sync>("stageFunctionalTestRepository") {
    from(functionalTestRepositories)
    into(layout.buildDirectory.dir("functional-test-repository"))
}
val pluginVersion = project.version.toString()
val pluginPom = publicationPom("pluginMaven")
val pluginMarkerPom = publicationPom("chasm-gradle-pluginPluginMarkerMaven")
val pluginModuleMetadata = publicationModuleMetadata("pluginMaven")
val pluginArtifactPath = "${project.group.toString().replace('.', '/')}/${project.name}/$pluginVersion"
val pluginMarkerArtifactPath = "${chasmPluginId.replace('.', '/')}/${chasmPluginId}.gradle.plugin/$pluginVersion"
val pluginJarName = "${project.name}-$pluginVersion.jar"
val pluginPomName = "${project.name}-$pluginVersion.pom"
val pluginMarkerPomName = "${chasmPluginId}.gradle.plugin-$pluginVersion.pom"
val stagePluginForFunctionalTest = tasks.register<Sync>("stagePluginForFunctionalTest") {
    into(layout.buildDirectory.dir("functional-test-plugin-repository"))
    from(tasks.jar.flatMap { task -> task.archiveFile }) {
        into(pluginArtifactPath)
        rename(".*\\.jar", pluginJarName)
    }
    from(pluginPom) {
        into(pluginArtifactPath)
        rename("pom-default.xml", pluginPomName)
    }
    from(pluginMarkerPom) {
        into(pluginMarkerArtifactPath)
        rename("pom-default.xml", pluginMarkerPomName)
    }
}

functionalTest.configure {
    val repository = stageFunctionalTestRepository.flatMap { it.destinationDirectory }
    val pluginRepository = stagePluginForFunctionalTest.flatMap { it.destinationDirectory }
    val pom = pluginPom
    val moduleMetadata = pluginModuleMetadata
    inputs.dir(repository).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.dir(pluginRepository).withPathSensitivity(PathSensitivity.RELATIVE)
    inputs.files(pom).withPathSensitivity(PathSensitivity.NONE)
    inputs.files(moduleMetadata).withPathSensitivity(PathSensitivity.NONE)
    doFirst {
        val test = this as Test
        test.systemProperty("chasm.functionalTest.repository", repository.get().asFile.toURI().toString())
        test.systemProperty("chasm.functionalTest.pluginRepository", pluginRepository.get().asFile.toURI().toString())
        test.systemProperty("chasm.functionalTest.pluginPom", pom.singleFile.absolutePath)
        test.systemProperty("chasm.functionalTest.pluginModuleMetadata", moduleMetadata.singleFile.absolutePath)
    }
    systemProperty("chasm.functionalTest.pluginId", chasmPluginId)
    systemProperty("chasm.functionalTest.pluginVersion", pluginVersion)
    systemProperty("chasm.functionalTest.wasiPreview1Dependency", wasiPreview1Dependency)
    systemProperty("chasm.functionalTest.kotlinPluginVersion", libs.versions.kotlin.get())
    systemProperty("chasm.functionalTest.coroutinesVersion", libs.versions.kotlinx.coroutines.get())
    systemProperty("chasm.functionalTest.androidPluginVersion", libs.versions.android.build.tools.plugin.get())
    systemProperty("chasm.functionalTest.minimumAgpPluginVersion", minimumAgpVersion)
    systemProperty("chasm.functionalTest.minimumGradleVersion", minimumGradleVersion)
    systemProperty("chasm.functionalTest.minimumAgpGradleVersion", minimumAgpGradleVersion)
    systemProperty("chasm.functionalTest.testedGradleVersions", testedGradleVersions.joinToString(","))
    systemProperty("chasm.functionalTest.testedAgpGradleVersions", testedAgpGradleVersions.joinToString(","))
    systemProperty("chasm.functionalTest.compileSdk", libs.versions.compile.sdk.get())
    systemProperty(
        "chasm.functionalTest.minimumAgpCompileSdk",
        libs.versions.minimum.android.compile.sdk.get(),
    )
    systemProperty("chasm.functionalTest.minSdk", libs.versions.min.sdk.get())
}
