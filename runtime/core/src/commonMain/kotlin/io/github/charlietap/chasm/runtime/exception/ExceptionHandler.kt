package io.github.charlietap.chasm.runtime.exception

import io.github.charlietap.chasm.ir.instruction.ControlInstruction

data class ExceptionHandler(
    val handlers: List<ControlInstruction.CatchHandler>,
    val payloadDestinationSlots: List<List<Int>> = [],
    val continuationIps: IntArray,
    val framesDepth: Int,
    val framePointer: Int,
    val valueDepth: Int,
)
