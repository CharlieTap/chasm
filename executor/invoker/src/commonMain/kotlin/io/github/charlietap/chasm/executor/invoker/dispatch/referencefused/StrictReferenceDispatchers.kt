package io.github.charlietap.chasm.executor.invoker.dispatch.referencefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefAsNonNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefCastExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefEqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefFuncExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefIsNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefTestExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction

fun RefCastDispatcher(instruction: FusedReferenceInstruction.RefCastS) = dispatchInstruction(instruction, ::RefCastExecutor)

fun RefEqDispatcher(instruction: FusedReferenceInstruction.RefEqSs) = dispatchInstruction(instruction, ::RefEqExecutor)

fun RefIsNullDispatcher(instruction: FusedReferenceInstruction.RefIsNullS) = dispatchInstruction(instruction, ::RefIsNullExecutor)

fun RefAsNonNullDispatcher(instruction: FusedReferenceInstruction.RefAsNonNullS) = dispatchInstruction(instruction, ::RefAsNonNullExecutor)

fun RefNullDispatcher(instruction: FusedReferenceInstruction.RefNullS) = dispatchInstruction(instruction, ::RefNullExecutor)

fun RefFuncDispatcher(instruction: FusedReferenceInstruction.RefFuncS) = dispatchInstruction(instruction, ::RefFuncExecutor)

fun RefTestDispatcher(instruction: FusedReferenceInstruction.RefTestS) = dispatchInstruction(instruction, ::RefTestExecutor)
