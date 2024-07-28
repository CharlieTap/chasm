package io.github.charlietap.chasm.decoder.section

import io.github.charlietap.chasm.decoder.decoder.section.code.FunctionBody
import kotlin.jvm.JvmInline

@JvmInline
internal value class CodeSection(val bodies: List<FunctionBody>) : Section
