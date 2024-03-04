package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.decoder.section.function.FunctionHeader
import kotlin.jvm.JvmInline

@JvmInline
value class FunctionSection(val headers: List<FunctionHeader>) : Section
