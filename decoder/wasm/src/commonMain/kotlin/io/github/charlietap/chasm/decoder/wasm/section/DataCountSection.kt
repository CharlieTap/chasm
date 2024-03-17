package io.github.charlietap.chasm.decoder.wasm.section

import kotlin.jvm.JvmInline

@JvmInline
internal value class DataCountSection(val dataCount: UInt) : Section
