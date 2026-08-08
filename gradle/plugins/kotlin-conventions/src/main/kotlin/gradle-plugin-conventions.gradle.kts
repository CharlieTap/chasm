import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    id("kotlin-conventions")
}

val libs = the<LibrariesForLibs>()

configure<KotlinConventionsExtension> {
    jvmBytecodeVersion.set(libs.versions.java.plugin.bytecode.version.map(String::toInt))
}
