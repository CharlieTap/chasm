package io.github.charlietap.chasm.decoder.wasm.section

import io.github.charlietap.chasm.ast.module.Global
import kotlin.jvm.JvmInline

@JvmInline
internal value class GlobalSection(val globals: List<Global>) : Section
