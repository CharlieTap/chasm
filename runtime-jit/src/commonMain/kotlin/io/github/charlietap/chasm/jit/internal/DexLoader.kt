package io.github.charlietap.chasm.jit.internal

import dalvik.system.InMemoryDexClassLoader
import java.nio.ByteBuffer

internal typealias DexLoader = (List<ByteBuffer>, ClassLoader) -> ClassLoader

internal fun DexLoader(
    dex: List<ByteBuffer>,
    parent: ClassLoader,
): ClassLoader = InMemoryDexClassLoader(dex.toTypedArray(), parent)
