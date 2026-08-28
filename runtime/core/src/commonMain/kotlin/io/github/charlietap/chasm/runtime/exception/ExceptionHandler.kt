package io.github.charlietap.chasm.runtime.exception

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

data class ExceptionHandler(
    val handlers: List<ControlInstruction.CatchHandler>,
    val payloadDestinationSlots: List<IntArray> = [],
    val continuationIps: IntArray,
    val instance: ModuleInstance,
    val fp: Int,
    val sp: Int,
)
