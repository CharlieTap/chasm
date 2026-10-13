package io.github.charlietap.chasm.parallel

actual fun availableParallelProcessors(): Int = Runtime.getRuntime().availableProcessors()

actual val parallelPlatform: ParallelPlatform = ParallelPlatform.ANDROID
