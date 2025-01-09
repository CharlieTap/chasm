package io.github.charlietap.chasm.executor.runtime.exception

import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler

data class ExceptionHandler(
    var instructions: List<CatchHandler>,
    val framesDepth: Int,
    val instructionsDepth: Int,
    val labelsDepth: Int,
)
