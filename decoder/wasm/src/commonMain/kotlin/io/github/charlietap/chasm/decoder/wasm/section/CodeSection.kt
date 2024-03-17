package io.github.charlietap.chasm.decoder.wasm.section

import io.github.charlietap.chasm.decoder.wasm.decoder.section.code.FunctionBody
import kotlin.jvm.JvmInline

@JvmInline
internal value class CodeSection(val bodies: List<FunctionBody>) : Section
