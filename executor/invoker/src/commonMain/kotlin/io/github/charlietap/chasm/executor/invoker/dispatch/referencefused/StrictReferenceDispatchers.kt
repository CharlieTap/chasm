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

fun RefCastDispatcher(instruction: ReferenceSuperInstruction.RefCastS) = dispatchInstruction { vstack, context -> RefCastExecutor(vstack, context, instruction) }

fun RefEqDispatcher(instruction: ReferenceSuperInstruction.RefEqSs) = dispatchInstruction { vstack, context -> RefEqExecutor(vstack, context, instruction) }

fun RefIsNullDispatcher(instruction: ReferenceSuperInstruction.RefIsNullS) = dispatchInstruction { vstack, context -> RefIsNullExecutor(vstack, context, instruction) }

fun RefAsNonNullDispatcher(instruction: ReferenceSuperInstruction.RefAsNonNullS) = dispatchInstruction { vstack, context -> RefAsNonNullExecutor(vstack, context, instruction) }

fun RefNullDispatcher(instruction: ReferenceSuperInstruction.RefNullS) = dispatchInstruction { vstack, context -> RefNullExecutor(vstack, context, instruction) }

fun RefFuncDispatcher(instruction: ReferenceSuperInstruction.RefFuncS) = dispatchInstruction { vstack, context -> RefFuncExecutor(vstack, context, instruction) }

fun RefTestDispatcher(instruction: ReferenceSuperInstruction.RefTestS) = dispatchInstruction { vstack, context -> RefTestExecutor(vstack, context, instruction) }
