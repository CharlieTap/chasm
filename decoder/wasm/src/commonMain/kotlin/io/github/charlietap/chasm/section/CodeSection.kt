package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.decoder.section.code.FunctionBody
import kotlin.jvm.JvmInline

@JvmInline
value class CodeSection(val bodies: List<FunctionBody>) : Section
