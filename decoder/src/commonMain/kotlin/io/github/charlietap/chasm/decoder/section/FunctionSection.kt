package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.decoder.decoder.section.function.FunctionHeader
import kotlin.jvm.JvmInline

@JvmInline
internal value class FunctionSection(val headers: List<FunctionHeader>) : Section
