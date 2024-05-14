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
    id("com.gradle.enterprise") version ("3.16.2")
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.8.0")
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"

        publishAlwaysIf(!System.getenv("GITHUB_ACTIONS").isNullOrEmpty())
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
include(":chasm")

include(":decoder")
include(":decoder:wasm")
include(":decoder:wat")

include(":executor:gc")
include(":executor:instantiator")
include(":executor:invoker")
include(":executor:memory")
include(":executor:runtime")
include(":executor:runtime-ext")
include(":executor:type")

include(":test:fake:decoder")

include(":test:fixture:ast")
include(":test:fixture:executor:runtime")

include(":validator")

includeBuild("example")
includeBuild("sweet")


rootProject.name = "chasm-multiplatform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
