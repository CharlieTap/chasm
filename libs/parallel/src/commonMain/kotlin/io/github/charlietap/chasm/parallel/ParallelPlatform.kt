package io.github.charlietap.chasm.parallel

expect fun availableParallelProcessors(): Int

enum class ParallelPlatform {
    JVM,
    ANDROID,
    NATIVE,
}

expect val parallelPlatform: ParallelPlatform
