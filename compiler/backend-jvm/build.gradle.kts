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
        api(projects.compiler.backendAbi)
        api(projects.moduleDeclarations)

        implementation(projects.compiler.backendAnalysis)
        implementation(projects.runtime.compiledApi)
        implementation(libs.asm)
        implementation(libs.asm.tree)

        testImplementation(projects.compiler.frontend)
        testImplementation(projects.decoder)
        testImplementation(projects.module)
        testImplementation(projects.validator)
        testImplementation(libs.kotlin.test)
        testImplementation(libs.result)
    }
}

tasks.withType<Test>().configureEach {
    jvmArgs("-Xverify:all")
    providers.systemPropertiesPrefixedBy("codegen.").get().forEach { (key, value) -> systemProperty(key, value) }
}

configure<PublishingConventionsExtension> {
    name = "compiler-backend-jvm"
    description = "Lowers Chasm IR to JVM class files that run on the JVM and, through D8, on Android"
}
