package io.github.charlietap.chasm.executor.invoker.dispatch.controlfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.BrIfExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.BrOnCastExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.BrOnCastFailExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.BrOnNonNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.BrOnNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.BrTableExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.CallExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.IfExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.ReturnCallExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.ThrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.controlfused.ThrowRefExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction

fun BrIfDispatcher(instruction: FusedControlInstruction.BrIfI) = dispatchInstruction(instruction, ::BrIfExecutor)

fun BrIfDispatcher(instruction: FusedControlInstruction.BrIfS) = dispatchInstruction(instruction, ::BrIfExecutor)

fun BrTableDispatcher(instruction: FusedControlInstruction.BrTableI) = dispatchInstruction(instruction, ::BrTableExecutor)

fun BrTableDispatcher(instruction: FusedControlInstruction.BrTableS) = dispatchInstruction(instruction, ::BrTableExecutor)

fun BrOnNullDispatcher(instruction: FusedControlInstruction.BrOnNullS) = dispatchInstruction(instruction, ::BrOnNullExecutor)

fun BrOnNonNullDispatcher(instruction: FusedControlInstruction.BrOnNonNullS) = dispatchInstruction(instruction, ::BrOnNonNullExecutor)

fun BrOnCastDispatcher(instruction: FusedControlInstruction.BrOnCastS) = dispatchInstruction(instruction, ::BrOnCastExecutor)

fun BrOnCastFailDispatcher(instruction: FusedControlInstruction.BrOnCastFailS) = dispatchInstruction(instruction, ::BrOnCastFailExecutor)

fun CallDispatcher(instruction: FusedControlInstruction.WasmCall) = dispatchInstruction(instruction, ::CallExecutor)

fun CallDispatcher(instruction: FusedControlInstruction.HostCall) = dispatchInstruction(instruction, ::CallExecutor)

fun CallDispatcher(instruction: FusedControlInstruction.CallIndirectI) = dispatchInstruction(instruction, ::CallExecutor)

fun CallDispatcher(instruction: FusedControlInstruction.CallIndirectS) = dispatchInstruction(instruction, ::CallExecutor)

fun CallDispatcher(instruction: FusedControlInstruction.CallRefS) = dispatchInstruction(instruction, ::CallExecutor)

fun ReturnCallDispatcher(instruction: FusedControlInstruction.ReturnWasmCall) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ReturnCallDispatcher(instruction: FusedControlInstruction.ReturnHostCall) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ReturnCallDispatcher(instruction: FusedControlInstruction.ReturnCallIndirectI) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ReturnCallDispatcher(instruction: FusedControlInstruction.ReturnCallIndirectS) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ReturnCallDispatcher(instruction: FusedControlInstruction.ReturnCallRefS) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ThrowDispatcher(instruction: FusedControlInstruction.Throw) = dispatchInstruction(instruction, ::ThrowExecutor)

fun ThrowRefDispatcher(instruction: FusedControlInstruction.ThrowRefS) = dispatchInstruction(instruction, ::ThrowRefExecutor)

fun IfDispatcher(instruction: FusedControlInstruction.IfI) = dispatchInstruction(instruction, ::IfExecutor)

fun IfDispatcher(instruction: FusedControlInstruction.IfS) = dispatchInstruction(instruction, ::IfExecutor)
