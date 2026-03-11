package io.github.charlietap.chasm.executor.invoker.dispatch.referencefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefAsNonNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefCastExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefEqExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefFuncExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefIsNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefNullExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.referencefused.RefTestExecutor
import io.github.charlietap.chasm.runtime.instruction.ReferenceSuperInstruction

fun RefCastDispatcher(instruction: ReferenceSuperInstruction.RefCastS) = dispatchInstruction(instruction, ::RefCastExecutor)

fun RefEqDispatcher(instruction: ReferenceSuperInstruction.RefEqSs) = dispatchInstruction(instruction, ::RefEqExecutor)

fun RefIsNullDispatcher(instruction: ReferenceSuperInstruction.RefIsNullS) = dispatchInstruction(instruction, ::RefIsNullExecutor)

fun RefAsNonNullDispatcher(instruction: ReferenceSuperInstruction.RefAsNonNullS) = dispatchInstruction(instruction, ::RefAsNonNullExecutor)

fun RefNullDispatcher(instruction: ReferenceSuperInstruction.RefNullS) = dispatchInstruction(instruction, ::RefNullExecutor)

fun RefFuncDispatcher(instruction: ReferenceSuperInstruction.RefFuncS) = dispatchInstruction(instruction, ::RefFuncExecutor)

fun RefTestDispatcher(instruction: ReferenceSuperInstruction.RefTestS) = dispatchInstruction(instruction, ::RefTestExecutor)
