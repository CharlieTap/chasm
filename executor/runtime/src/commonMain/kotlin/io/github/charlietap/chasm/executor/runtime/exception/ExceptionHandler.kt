package io.github.charlietap.chasm.executor.runtime.exception

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import kotlin.jvm.JvmInline

@JvmInline
value class ExceptionHandler(val instructions: List<ControlInstruction.CatchHandler>)
