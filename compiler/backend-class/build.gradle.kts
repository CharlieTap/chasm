plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.conventions.kotlin)
    alias(libs.plugins.conventions.linting)
    alias(libs.plugins.conventions.publishing)
    alias(libs.plugins.conventions.jvm.functional.test)
}

kotlin {
    dependencies {
        api(projects.ast)
        api(projects.cir)
        api(projects.compiler.backendAnalysis)
        api(projects.config)
        api(projects.moduleDeclarations)
        api(libs.asm)
        api(libs.asm.tree)

        implementation(projects.runtime.compiledFormat)

        testImplementation(projects.compiler.frontend)
        testImplementation(projects.decoder)
        testImplementation(projects.module)
        testImplementation(projects.runtimeCompiled)
        testImplementation(projects.validator)
        testImplementation(libs.asm.analysis)
        testImplementation(libs.result)
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs("-Xverify:all")
    providers.systemPropertiesPrefixedBy("codegen.").get().forEach { (key, value) -> systemProperty(key, value) }
}

configure<PublishingConventionsExtension> {
    name = "compiler-backend-class"
    description = "Lowers Chasm IR to JVM class files that run on the JVM and, through D8, on Android"
}
