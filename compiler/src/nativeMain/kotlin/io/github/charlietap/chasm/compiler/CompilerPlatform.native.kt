package io.github.charlietap.chasm.compiler

import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
internal actual fun availableCompilerProcessors(): Int = Platform.getAvailableProcessors()
