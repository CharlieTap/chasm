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
    id("com.gradle.enterprise") version ("3.15.1")
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.7.0")
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

include(":executor:instantiator")
include(":executor:invoker")
include(":executor:runtime")

include(":test:fake:decoder")

include(":test:fixture:ast")
include(":test:fixture:executor:runtime")

include(":validator")

rootProject.name = "chasm-multiplatform"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
