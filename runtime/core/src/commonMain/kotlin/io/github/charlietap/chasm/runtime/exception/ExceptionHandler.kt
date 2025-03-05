package io.github.charlietap.chasm.runtime.exception

import io.github.charlietap.chasm.ir.instruction.ControlInstruction

data class ExceptionHandler(
    var instructions: List<ControlInstruction.CatchHandler>,
    val framesDepth: Int,
    val instructionsDepth: Int,
    val labelsDepth: Int,
)
