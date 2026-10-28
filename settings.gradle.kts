pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    includeBuild("tools/corpus")
    includeBuild("tools/sweet")
    includeBuild("gradle/plugins/kotlin-conventions")
    includeBuild("gradle/plugins/linting-conventions")
    includeBuild("gradle/plugins/publishing-conventions")
    includeBuild("gradle/plugins/versions-conventions")
}

plugins {
    id("com.gradle.develocity") version ("4.5.0")
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

develocity {
    buildScan {
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"

        publishing.onlyIf { _ -> !System.getenv("GITHUB_ACTIONS").isNullOrEmpty() }
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io" )
    }
}

include(":ast")
include(":module-declarations")
include(":benchmark")
include(":chasm")
include(":chasm-coroutines")
include(":chasm-gradle-plugin")
include(":cir")
include(":config")

include(":decoder")

include(":embedding-api")

include(":executor:call-core")
include(":executor:dispatch")
include(":executor:instantiator")
include(":executor:interpreter")
include(":executor:invoker")

include(":host")

include(":gc")

include(":libs:sse2")
include(":libs:stack")
include(":libs:benchmark")
include(":libs:parallel")
include(":libs:mmap")

include(":memory")

include(":module")

include(":compiler:backend-chasm")
include(":compiler:frontend")

include(":runtime:address")
include(":runtime:core")
include(":runtime:encoding")
include(":runtime:operations")
include(":runtime:resources")
include(":runtime:type")
include(":runtime:value")

include(":stream")

include(":test:fake:decoder")

include(":test:fixture:ast")
include(":test:fixture:chasm")
include(":test:fixture:cir")
include(":test:fixture:config")
include(":test:fixture:executor:instantiator")
include(":test:fixture:host")
include(":test:fixture:runtime")
include(":test:fixture:type-system")

include(":tools:compiler-baseline")

include(":tooling:bindgen")

include(":type-system")
include(":validator")
include(":vm")

includeBuild("tools/sweet")
includeBuild("tools/corpus")

rootProject.name = "chasm-multiplatform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("ENHANCED_GRAPH_ORDERING")
enableFeaturePreview("NO_IMPLICIT_LOOKUP_IN_PARENT_PROJECTS")
