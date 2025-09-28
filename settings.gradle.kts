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
    id("com.gradle.develocity") version ("4.1")
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
include(":benchmark")
include(":chasm")
include(":chasm-gradle-plugin")
include(":config")

include(":decoder")

include(":executor:instantiator")
include(":executor:invoker")

include(":host")
include(":ir")
include(":ir-factory")

include(":libs:sse2")
include(":libs:stack")

include(":memory")

include(":predecoder")

include(":compiler")

include(":runtime:address")
include(":runtime:core")
include(":runtime:type")
include(":runtime:value")

include(":stream")

include(":test:fake:decoder")

include(":test:fixture:ast")
include(":test:fixture:chasm")
include(":test:fixture:config")
include(":test:fixture:executor:instantiator")
include(":test:fixture:ir")
include(":test:fixture:runtime")
include(":test:fixture:type-system")

include(":type-system")
include(":validator")

includeBuild("bolt")
//includeBuild("example")
includeBuild("sweet")

rootProject.name = "chasm-multiplatform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
