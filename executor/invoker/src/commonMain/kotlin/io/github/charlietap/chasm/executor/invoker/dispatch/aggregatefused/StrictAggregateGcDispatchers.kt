package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.AnyConvertExternExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayInitDataExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayInitElementExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayNewDataExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayNewDefaultExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayNewElementExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ExternConvertAnyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.I31GetSignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.I31GetUnsignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.RefI31Executor
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayNewDefaultDispatcher(instruction: FusedAggregateInstruction.ArrayNewDefaultI) = dispatchInstruction(instruction, ::ArrayNewDefaultExecutor)

fun ArrayNewDefaultDispatcher(instruction: FusedAggregateInstruction.ArrayNewDefaultS) = dispatchInstruction(instruction, ::ArrayNewDefaultExecutor)

fun ArrayNewDataDispatcher(instruction: FusedAggregateInstruction.ArrayNewDataIi) = dispatchInstruction(instruction, ::ArrayNewDataExecutor)

fun ArrayNewDataDispatcher(instruction: FusedAggregateInstruction.ArrayNewDataIs) = dispatchInstruction(instruction, ::ArrayNewDataExecutor)

fun ArrayNewDataDispatcher(instruction: FusedAggregateInstruction.ArrayNewDataSi) = dispatchInstruction(instruction, ::ArrayNewDataExecutor)

fun ArrayNewDataDispatcher(instruction: FusedAggregateInstruction.ArrayNewDataSs) = dispatchInstruction(instruction, ::ArrayNewDataExecutor)

fun ArrayNewElementDispatcher(instruction: FusedAggregateInstruction.ArrayNewElementIi) = dispatchInstruction(instruction, ::ArrayNewElementExecutor)

fun ArrayNewElementDispatcher(instruction: FusedAggregateInstruction.ArrayNewElementIs) = dispatchInstruction(instruction, ::ArrayNewElementExecutor)

fun ArrayNewElementDispatcher(instruction: FusedAggregateInstruction.ArrayNewElementSi) = dispatchInstruction(instruction, ::ArrayNewElementExecutor)

fun ArrayNewElementDispatcher(instruction: FusedAggregateInstruction.ArrayNewElementSs) = dispatchInstruction(instruction, ::ArrayNewElementExecutor)

fun ArrayInitDataDispatcher(instruction: FusedAggregateInstruction.ArrayInitDataIii) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: FusedAggregateInstruction.ArrayInitDataIis) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: FusedAggregateInstruction.ArrayInitDataIsi) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: FusedAggregateInstruction.ArrayInitDataIss) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: FusedAggregateInstruction.ArrayInitDataSii) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: FusedAggregateInstruction.ArrayInitDataSis) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: FusedAggregateInstruction.ArrayInitDataSsi) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: FusedAggregateInstruction.ArrayInitDataSss) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitElementDispatcher(instruction: FusedAggregateInstruction.ArrayInitElementIii) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: FusedAggregateInstruction.ArrayInitElementIis) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: FusedAggregateInstruction.ArrayInitElementIsi) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: FusedAggregateInstruction.ArrayInitElementIss) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: FusedAggregateInstruction.ArrayInitElementSii) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: FusedAggregateInstruction.ArrayInitElementSis) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: FusedAggregateInstruction.ArrayInitElementSsi) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: FusedAggregateInstruction.ArrayInitElementSss) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun RefI31Dispatcher(instruction: FusedAggregateInstruction.RefI31I) = dispatchInstruction(instruction, ::RefI31Executor)

fun RefI31Dispatcher(instruction: FusedAggregateInstruction.RefI31S) = dispatchInstruction(instruction, ::RefI31Executor)

fun I31GetSignedDispatcher(instruction: FusedAggregateInstruction.I31GetSignedS) = dispatchInstruction(instruction, ::I31GetSignedExecutor)

fun I31GetUnsignedDispatcher(instruction: FusedAggregateInstruction.I31GetUnsignedS) = dispatchInstruction(instruction, ::I31GetUnsignedExecutor)

fun AnyConvertExternDispatcher(instruction: FusedAggregateInstruction.AnyConvertExternS) = dispatchInstruction(instruction, ::AnyConvertExternExecutor)

fun ExternConvertAnyDispatcher(instruction: FusedAggregateInstruction.ExternConvertAnyS) = dispatchInstruction(instruction, ::ExternConvertAnyExecutor)
