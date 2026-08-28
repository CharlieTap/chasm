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

fun ArrayNewDefaultDispatcher(instruction: AggregateSuperInstruction.ArrayNewDefaultI) = dispatchInstruction { vstack, context -> ArrayNewDefaultExecutor(vstack, context, instruction) }

fun ArrayNewDefaultDispatcher(instruction: AggregateSuperInstruction.ArrayNewDefaultS) = dispatchInstruction { vstack, context -> ArrayNewDefaultExecutor(vstack, context, instruction) }

fun ArrayNewDataDispatcher(instruction: AggregateSuperInstruction.ArrayNewDataIi) = dispatchInstruction { vstack, context -> ArrayNewDataExecutor(vstack, context, instruction) }

fun ArrayNewDataDispatcher(instruction: AggregateSuperInstruction.ArrayNewDataIs) = dispatchInstruction { vstack, context -> ArrayNewDataExecutor(vstack, context, instruction) }

fun ArrayNewDataDispatcher(instruction: AggregateSuperInstruction.ArrayNewDataSi) = dispatchInstruction { vstack, context -> ArrayNewDataExecutor(vstack, context, instruction) }

fun ArrayNewDataDispatcher(instruction: AggregateSuperInstruction.ArrayNewDataSs) = dispatchInstruction { vstack, context -> ArrayNewDataExecutor(vstack, context, instruction) }

fun ArrayNewElementDispatcher(instruction: AggregateSuperInstruction.ArrayNewElementIi) = dispatchInstruction { vstack, context -> ArrayNewElementExecutor(vstack, context, instruction) }

fun ArrayNewElementDispatcher(instruction: AggregateSuperInstruction.ArrayNewElementIs) = dispatchInstruction { vstack, context -> ArrayNewElementExecutor(vstack, context, instruction) }

fun ArrayNewElementDispatcher(instruction: AggregateSuperInstruction.ArrayNewElementSi) = dispatchInstruction { vstack, context -> ArrayNewElementExecutor(vstack, context, instruction) }

fun ArrayNewElementDispatcher(instruction: AggregateSuperInstruction.ArrayNewElementSs) = dispatchInstruction { vstack, context -> ArrayNewElementExecutor(vstack, context, instruction) }

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataIii) = dispatchInstruction { vstack, context -> ArrayInitDataExecutor(vstack, context, instruction) }

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataIis) = dispatchInstruction { vstack, context -> ArrayInitDataExecutor(vstack, context, instruction) }

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataIsi) = dispatchInstruction { vstack, context -> ArrayInitDataExecutor(vstack, context, instruction) }

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataIss) = dispatchInstruction { vstack, context -> ArrayInitDataExecutor(vstack, context, instruction) }

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataSii) = dispatchInstruction { vstack, context -> ArrayInitDataExecutor(vstack, context, instruction) }

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataSis) = dispatchInstruction { vstack, context -> ArrayInitDataExecutor(vstack, context, instruction) }

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataSsi) = dispatchInstruction { vstack, context -> ArrayInitDataExecutor(vstack, context, instruction) }

fun ArrayInitDataDispatcher(instruction: AggregateSuperInstruction.ArrayInitDataSss) = dispatchInstruction { vstack, context -> ArrayInitDataExecutor(vstack, context, instruction) }

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementIii) = dispatchInstruction { vstack, context -> ArrayInitElementExecutor(vstack, context, instruction) }

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementIis) = dispatchInstruction { vstack, context -> ArrayInitElementExecutor(vstack, context, instruction) }

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementIsi) = dispatchInstruction { vstack, context -> ArrayInitElementExecutor(vstack, context, instruction) }

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementIss) = dispatchInstruction { vstack, context -> ArrayInitElementExecutor(vstack, context, instruction) }

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementSii) = dispatchInstruction { vstack, context -> ArrayInitElementExecutor(vstack, context, instruction) }

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementSis) = dispatchInstruction { vstack, context -> ArrayInitElementExecutor(vstack, context, instruction) }

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementSsi) = dispatchInstruction { vstack, context -> ArrayInitElementExecutor(vstack, context, instruction) }

fun ArrayInitElementDispatcher(instruction: AggregateSuperInstruction.ArrayInitElementSss) = dispatchInstruction { vstack, context -> ArrayInitElementExecutor(vstack, context, instruction) }

fun RefI31Dispatcher(instruction: AggregateSuperInstruction.RefI31I) = dispatchInstruction { vstack, context -> RefI31Executor(vstack, context, instruction) }

fun RefI31Dispatcher(instruction: AggregateSuperInstruction.RefI31S) = dispatchInstruction { vstack, context -> RefI31Executor(vstack, context, instruction) }

fun I31GetSignedDispatcher(instruction: AggregateSuperInstruction.I31GetSignedS) = dispatchInstruction { vstack, context -> I31GetSignedExecutor(vstack, context, instruction) }

fun I31GetUnsignedDispatcher(instruction: AggregateSuperInstruction.I31GetUnsignedS) = dispatchInstruction { vstack, context -> I31GetUnsignedExecutor(vstack, context, instruction) }

fun AnyConvertExternDispatcher(instruction: AggregateSuperInstruction.AnyConvertExternS) = dispatchInstruction { vstack, context -> AnyConvertExternExecutor(vstack, context, instruction) }

fun ExternConvertAnyDispatcher(instruction: AggregateSuperInstruction.ExternConvertAnyS) = dispatchInstruction { vstack, context -> ExternConvertAnyExecutor(vstack, context, instruction) }
