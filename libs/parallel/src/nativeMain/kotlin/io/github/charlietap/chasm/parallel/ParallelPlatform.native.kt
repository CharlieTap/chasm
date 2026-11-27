package io.github.charlietap.chasm.parallel

import io.github.charlietap.chasm.InternalChasmApi
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@InternalChasmApi
@OptIn(ExperimentalNativeApi::class)
actual fun availableParallelProcessors(): Int = Platform.getAvailableProcessors()

@InternalChasmApi
actual val parallelPlatform: ParallelPlatform = ParallelPlatform.NATIVE
