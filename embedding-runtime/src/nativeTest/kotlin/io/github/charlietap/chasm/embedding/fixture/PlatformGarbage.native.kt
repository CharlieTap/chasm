@file:OptIn(kotlin.native.runtime.NativeRuntimeApi::class)

package io.github.charlietap.chasm.embedding.fixture

import kotlin.native.runtime.GC

internal actual fun collectPlatformGarbage() {
    GC.collect()
}
