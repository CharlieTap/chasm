package io.github.charlietap.chasm.section

import io.github.charlietap.chasm.ast.module.StartFunction
import kotlin.jvm.JvmInline

@JvmInline
value class StartSection(val startFunction: StartFunction) : Section
