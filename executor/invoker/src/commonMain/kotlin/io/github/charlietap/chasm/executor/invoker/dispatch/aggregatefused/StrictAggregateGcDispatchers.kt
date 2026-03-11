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
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction

fun ArrayNewDefaultDispatcher(instruction: AggregateSuperInstruction.ArrayNewDefaultI) = dispatchInstruction(instruction, ::ArrayNewDefaultExecutor)

fun ArrayNewDefaultDispatcher(instruction: AggregateSuperInstruction.ArrayNewDefaultS) = dispatchInstruction(instruction, ::ArrayNewDefaultExecutor)

fun ArrayNewDataDispatcher(instruction: AggregateSuperInstruction.ArrayNewDataIi) = dispatchInstruction(instruction, ::ArrayNewDataExecutor)

fun ArrayNewDataDispatcher(instruction: AggregateSuperInstruction.ArrayNewDataIs) = dispatchInstruction(instruction, ::ArrayNewDataExecutor)

fun ArrayNewDataDispatcher(instruction: AggregateSuperInstruction.ArrayNewDataSi) = dispatchInstruction(instruction, ::ArrayNewDataExecutor)

fun ArrayNewDataDispatcher(instruction: AggregateSuperInstruction.ArrayNewDataSs) = dispatchInstruction(instruction, ::ArrayNewDataExecutor)

fun ArrayNewElementDispatcher(instruction: AggregateSuperInstruction.ArrayNewElementIi) = dispatchInstruction(instruction, ::ArrayNewElementExecutor)

fun ArrayNewElementDispatcher(instruction: AggregateSuperInstruction.ArrayNewElementIs) = dispatchInstruction(instruction, ::ArrayNewElementExecutor)

fun ArrayNewElementDispatcher(instruction: AggregateSuperInstruction.ArrayNewElementSi) = dispatchInstruction(instruction, ::ArrayNewElementExecutor)

fun ArrayNewElementDispatcher(instruction: AggregateSuperInstruction.ArrayNewElementSs) = dispatchInstruction(instruction, ::ArrayNewElementExecutor)

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataIii) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataIis) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataIsi) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataIss) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataSii) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataSis) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataSsi) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataSss) = dispatchInstruction(instruction, ::ArrayInitDataExecutor)

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementIii) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementIis) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementIsi) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementIss) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementSii) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementSis) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementSsi) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementSss) = dispatchInstruction(instruction, ::ArrayInitElementExecutor)

fun RefI31Dispatcher(instruction: AggregateSuperInstruction.RefI31I) = dispatchInstruction(instruction, ::RefI31Executor)

fun RefI31Dispatcher(instruction: AggregateSuperInstruction.RefI31S) = dispatchInstruction(instruction, ::RefI31Executor)

fun I31GetSignedDispatcher(instruction: AggregateSuperInstruction.I31GetSignedS) = dispatchInstruction(instruction, ::I31GetSignedExecutor)

fun I31GetUnsignedDispatcher(instruction: AggregateSuperInstruction.I31GetUnsignedS) = dispatchInstruction(instruction, ::I31GetUnsignedExecutor)

fun AnyConvertExternDispatcher(instruction: AggregateSuperInstruction.AnyConvertExternS) = dispatchInstruction(instruction, ::AnyConvertExternExecutor)

fun ExternConvertAnyDispatcher(instruction: AggregateSuperInstruction.ExternConvertAnyS) = dispatchInstruction(instruction, ::ExternConvertAnyExecutor)
