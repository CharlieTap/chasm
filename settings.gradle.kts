pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }

    includeBuild("gradle/plugins/kmp-conventions")
    includeBuild("gradle/plugins/linting-conventions")
    includeBuild("gradle/plugins/publishing-conventions")
    includeBuild("gradle/plugins/versions-conventions")
}

plugins {
    id("com.gradle.develocity") version ("3.19")
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
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
        google()
        mavenCentral()
        mavenLocal()
        maven(url = "https://jitpack.io" )

    }
}

include(":ast")
include(":benchmark")
include(":chasm")
include(":config")

include(":decoder")

include(":executor:instantiator")
include(":executor:invoker")
include(":executor:memory")
include(":executor:runtime-internal")
include(":executor:runtime-external")

include(":host")
include(":ir")

include(":libs:sse2")
include(":libs:stack")

include(":optimiser")
include(":stream")

include(":test:fake:decoder")

include(":test:fixture:ast")
include(":test:fixture:config")
include(":test:fixture:executor:instantiator")
include(":test:fixture:executor:runtime")
include(":test:fixture:ir")

include(":type-system")
include(":validator")

includeBuild("bolt")
includeBuild("example")
includeBuild("sweet")

rootProject.name = "chasm-multiplatform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
