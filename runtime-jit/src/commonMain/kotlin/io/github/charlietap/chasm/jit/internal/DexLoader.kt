package io.github.charlietap.chasm.jit.internal

import dalvik.system.InMemoryDexClassLoader
import java.nio.ByteBuffer

internal typealias DexLoader = (List<ByteArray>, ClassLoader) -> ClassLoader

internal fun DexLoader(
    dex: List<ByteArray>,
    parent: ClassLoader,
): ClassLoader = InMemoryDexClassLoader(dex.map(ByteBuffer::wrap).toTypedArray(), parent)
