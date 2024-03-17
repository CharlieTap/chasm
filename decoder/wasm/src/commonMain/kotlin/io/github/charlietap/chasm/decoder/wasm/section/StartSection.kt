package io.github.charlietap.chasm.decoder.wasm.section

import io.github.charlietap.chasm.ast.module.StartFunction
import kotlin.jvm.JvmInline

@JvmInline
internal value class StartSection(val startFunction: StartFunction) : Section
