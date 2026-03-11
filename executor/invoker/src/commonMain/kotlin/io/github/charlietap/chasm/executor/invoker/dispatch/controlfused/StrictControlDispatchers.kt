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
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction

fun BrIfDispatcher(instruction: ControlSuperInstruction.BrIfI) = dispatchInstruction(instruction, ::BrIfExecutor)

fun BrIfDispatcher(instruction: ControlSuperInstruction.BrIfS) = dispatchInstruction(instruction, ::BrIfExecutor)

fun BrTableDispatcher(instruction: ControlSuperInstruction.BrTableI) = dispatchInstruction(instruction, ::BrTableExecutor)

fun BrTableDispatcher(instruction: ControlSuperInstruction.BrTableS) = dispatchInstruction(instruction, ::BrTableExecutor)

fun BrOnNullDispatcher(instruction: ControlSuperInstruction.BrOnNullS) = dispatchInstruction(instruction, ::BrOnNullExecutor)

fun BrOnNonNullDispatcher(instruction: ControlSuperInstruction.BrOnNonNullS) = dispatchInstruction(instruction, ::BrOnNonNullExecutor)

fun BrOnCastDispatcher(instruction: ControlSuperInstruction.BrOnCastS) = dispatchInstruction(instruction, ::BrOnCastExecutor)

fun BrOnCastFailDispatcher(instruction: ControlSuperInstruction.BrOnCastFailS) = dispatchInstruction(instruction, ::BrOnCastFailExecutor)

fun CallDispatcher(instruction: ControlSuperInstruction.WasmCall) = dispatchInstruction(instruction, ::CallExecutor)

fun CallDispatcher(instruction: ControlSuperInstruction.HostCall) = dispatchInstruction(instruction, ::CallExecutor)

fun CallDispatcher(instruction: ControlSuperInstruction.CallIndirectI) = dispatchInstruction(instruction, ::CallExecutor)

fun CallDispatcher(instruction: ControlSuperInstruction.CallIndirectS) = dispatchInstruction(instruction, ::CallExecutor)

fun CallDispatcher(instruction: ControlSuperInstruction.CallRefS) = dispatchInstruction(instruction, ::CallExecutor)

fun ReturnCallDispatcher(instruction: ControlSuperInstruction.ReturnWasmCall) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ReturnCallDispatcher(instruction: ControlSuperInstruction.ReturnHostCall) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ReturnCallDispatcher(instruction: ControlSuperInstruction.ReturnCallIndirectI) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ReturnCallDispatcher(instruction: ControlSuperInstruction.ReturnCallIndirectS) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ReturnCallDispatcher(instruction: ControlSuperInstruction.ReturnCallRefS) = dispatchInstruction(instruction, ::ReturnCallExecutor)

fun ThrowDispatcher(instruction: ControlSuperInstruction.Throw) = dispatchInstruction(instruction, ::ThrowExecutor)

fun ThrowRefDispatcher(instruction: ControlSuperInstruction.ThrowRefS) = dispatchInstruction(instruction, ::ThrowRefExecutor)

fun IfDispatcher(instruction: ControlSuperInstruction.IfI) = dispatchInstruction(instruction, ::IfExecutor)

fun IfDispatcher(instruction: ControlSuperInstruction.IfS) = dispatchInstruction(instruction, ::IfExecutor)
