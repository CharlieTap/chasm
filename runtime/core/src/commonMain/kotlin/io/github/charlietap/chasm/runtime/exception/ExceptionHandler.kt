package io.github.charlietap.chasm.runtime.exception

import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

data class ExceptionHandler(
    var instructions: List<ControlInstruction.CatchHandler>,
    var payloadDestinationSlots: List<List<Int>> = emptyList(),
    var continuations: List<Array<DispatchableInstruction>> = emptyList(),
    val framesDepth: Int,
    val instructionsDepth: Int,
    val labelsDepth: Int,
    val framePointer: Int,
)
