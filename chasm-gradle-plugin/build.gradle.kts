import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.plugin.GradlePluginApiVersion
import org.gradle.api.tasks.Sync
import org.gradle.api.tasks.testing.Test
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlinx.test.resources)
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

buildConfig {
    buildConfigField("CHASM_JVM_DEPENDENCY", libs.chasm.jvm.get().notation())
    buildConfigField("VM_DEPENDENCY", libs.vm.kmp.get().notation())
    buildConfigField("VM_JVM_DEPENDENCY", libs.vm.jvm.get().notation())
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
val testedGradleVersions = listOf("9.5.0", "9.6.1", "9.7.1")
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

        compileOnly(projects.chasm) {
            because("We use the module and moduleInfo calls during codegen")
        }
        compileOnly(projects.vm)
        compileOnly(libs.kotlin.poet)

        testImplementation(libs.kotlin.test)
        testImplementation(libs.kotlinx.test.resources)
        testImplementation(projects.chasm)
        testImplementation(projects.vm)
        testImplementation(libs.kotlin.poet)
        testImplementation(projects.test.fixture.chasm)
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
    add(functionalTestRepositoryDependencies.name, projects.vm)
}

val minimumAgpVersion = libs.versions.minimum.android.build.tools.plugin.get()
val functionalTestRepository = layout.buildDirectory.dir("functional-test-repository")
val stageFunctionalTestRepository = tasks.register<Sync>("stageFunctionalTestRepository") {
    from(functionalTestRepositories)
    into(functionalTestRepository)
}
val pluginVersion = project.version.toString()
val pluginRepository = layout.buildDirectory.dir("functional-test-plugin-repository")
val pluginPom = layout.buildDirectory.file("publications/pluginMaven/pom-default.xml")
val pluginMarkerPom = layout.buildDirectory.file(
    "publications/chasm-gradle-pluginPluginMarkerMaven/pom-default.xml",
)
val pluginModuleMetadata = layout.buildDirectory.file("publications/pluginMaven/module.json")
val pluginArtifactPath = "${project.group.toString().replace('.', '/')}/${project.name}/$pluginVersion"
val pluginMarkerArtifactPath = "${chasmPluginId.replace('.', '/')}/${chasmPluginId}.gradle.plugin/$pluginVersion"
val pluginJarName = "${project.name}-$pluginVersion.jar"
val pluginPomName = "${project.name}-$pluginVersion.pom"
val pluginMarkerPomName = "${chasmPluginId}.gradle.plugin-$pluginVersion.pom"
val stagePluginForFunctionalTest = tasks.register<Sync>("stagePluginForFunctionalTest") {
    dependsOn(
        "generatePomFileForChasm-gradle-pluginPluginMarkerMavenPublication",
        "generatePomFileForPluginMavenPublication",
    )
    into(pluginRepository)
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
    dependsOn(
        "generateMetadataFileForPluginMavenPublication",
        stageFunctionalTestRepository,
        stagePluginForFunctionalTest,
    )
    inputs.dir(functionalTestRepository)
    inputs.dir(pluginRepository)
    inputs.file(pluginPom)
    inputs.file(pluginModuleMetadata)
    systemProperty("chasm.functionalTest.repository", functionalTestRepository.get().asFile.toURI().toString())
    systemProperty("chasm.functionalTest.pluginRepository", pluginRepository.get().asFile.toURI().toString())
    systemProperty("chasm.functionalTest.pluginPom", pluginPom.get().asFile.absolutePath)
    systemProperty(
        "chasm.functionalTest.pluginModuleMetadata",
        pluginModuleMetadata.get().asFile.absolutePath,
    )
    systemProperty("chasm.functionalTest.pluginId", chasmPluginId)
    systemProperty("chasm.functionalTest.pluginVersion", pluginVersion)
    systemProperty("chasm.functionalTest.kotlinPluginVersion", libs.versions.kotlin.get())
    systemProperty("chasm.functionalTest.androidPluginVersion", libs.versions.android.build.tools.plugin.get())
    systemProperty("chasm.functionalTest.minimumAgpPluginVersion", minimumAgpVersion)
    systemProperty("chasm.functionalTest.minimumGradleVersion", minimumGradleVersion)
    systemProperty("chasm.functionalTest.minimumAgpGradleVersion", minimumAgpGradleVersion)
    systemProperty("chasm.functionalTest.testedGradleVersions", testedGradleVersions.joinToString(","))
    systemProperty("chasm.functionalTest.compileSdk", libs.versions.compile.sdk.get())
    systemProperty(
        "chasm.functionalTest.minimumAgpCompileSdk",
        libs.versions.minimum.android.compile.sdk.get(),
    )
    systemProperty("chasm.functionalTest.minSdk", libs.versions.min.sdk.get())
}
