package io.github.charlietap.chasm.parallel

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
actual fun availableParallelProcessors(): Int = Platform.getAvailableProcessors()
