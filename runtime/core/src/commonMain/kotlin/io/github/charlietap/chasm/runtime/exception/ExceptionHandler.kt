package io.github.charlietap.chasm.runtime.exception

import io.github.charlietap.chasm.ast.instruction.ControlInstruction

data class ExceptionHandler(
    val handlers: List<ControlInstruction.CatchHandler>,
    val payloadDestinationSlots: List<IntArray> = [],
    val continuationIps: IntArray,
    val framesDepth: Int,
    val framePointer: Int,
    val valueDepth: Int,
)
