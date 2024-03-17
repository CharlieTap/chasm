package io.github.charlietap.chasm.decoder.wasm.decoder.vector

import kotlin.jvm.JvmInline

@JvmInline
internal value class Vector<T>(val vector: List<T>)
