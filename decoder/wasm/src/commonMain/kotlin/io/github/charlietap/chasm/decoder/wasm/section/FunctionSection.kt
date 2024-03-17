package io.github.charlietap.chasm.decoder.wasm.section

import io.github.charlietap.chasm.decoder.wasm.decoder.section.function.FunctionHeader
import kotlin.jvm.JvmInline

@JvmInline
internal value class FunctionSection(val headers: List<FunctionHeader>) : Section
