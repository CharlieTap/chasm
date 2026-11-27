package io.github.charlietap.chasm.parallel

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
actual fun availableParallelProcessors(): Int = Runtime.getRuntime().availableProcessors()

@InternalChasmApi
actual val parallelPlatform: ParallelPlatform = ParallelPlatform.JVM
