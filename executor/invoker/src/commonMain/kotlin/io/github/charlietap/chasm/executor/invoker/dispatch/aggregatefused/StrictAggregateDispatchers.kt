package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayLenExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayNewExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayNewFixedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArraySetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.LocalSetStructGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.RefCastStructGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetStructGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructNewDefaultExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructNewExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructSetExecutor
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopyIii) = dispatchInstruction { vstack, context -> ArrayCopyExecutor(vstack, context, instruction) }

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopyIis) = dispatchInstruction { vstack, context -> ArrayCopyExecutor(vstack, context, instruction) }

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopyIsi) = dispatchInstruction { vstack, context -> ArrayCopyExecutor(vstack, context, instruction) }

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopyIss) = dispatchInstruction { vstack, context -> ArrayCopyExecutor(vstack, context, instruction) }

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopySii) = dispatchInstruction { vstack, context -> ArrayCopyExecutor(vstack, context, instruction) }

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopySis) = dispatchInstruction { vstack, context -> ArrayCopyExecutor(vstack, context, instruction) }

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopySsi) = dispatchInstruction { vstack, context -> ArrayCopyExecutor(vstack, context, instruction) }

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopySss) = dispatchInstruction { vstack, context -> ArrayCopyExecutor(vstack, context, instruction) }

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillIii) = dispatchInstruction { vstack, context -> ArrayFillExecutor(vstack, context, instruction) }

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillIis) = dispatchInstruction { vstack, context -> ArrayFillExecutor(vstack, context, instruction) }

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillIsi) = dispatchInstruction { vstack, context -> ArrayFillExecutor(vstack, context, instruction) }

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillIss) = dispatchInstruction { vstack, context -> ArrayFillExecutor(vstack, context, instruction) }

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillSii) = dispatchInstruction { vstack, context -> ArrayFillExecutor(vstack, context, instruction) }

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillSis) = dispatchInstruction { vstack, context -> ArrayFillExecutor(vstack, context, instruction) }

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillSsi) = dispatchInstruction { vstack, context -> ArrayFillExecutor(vstack, context, instruction) }

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillSss) = dispatchInstruction { vstack, context -> ArrayFillExecutor(vstack, context, instruction) }

fun ArrayGetDispatcher(instruction: AggregateSuperInstruction.ArrayGetI) = dispatchInstruction { vstack, context -> ArrayGetExecutor(vstack, context, instruction) }

fun ArrayGetDispatcher(instruction: AggregateSuperInstruction.ArrayGetS) = dispatchInstruction { vstack, context -> ArrayGetExecutor(vstack, context, instruction) }

fun ArrayGetSignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetSignedI) = PackedArrayGetSignedDispatcher(instruction)

fun ArrayGetSignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetSignedS) = PackedArrayGetSignedDispatcher(instruction)

fun ArrayGetUnsignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetUnsignedI) = PackedArrayGetUnsignedDispatcher(instruction)

fun ArrayGetUnsignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetUnsignedS) = PackedArrayGetUnsignedDispatcher(instruction)

fun ArrayLenDispatcher(instruction: AggregateSuperInstruction.ArrayLenS) = dispatchInstruction { vstack, context -> ArrayLenExecutor(vstack, context, instruction) }

fun ArrayNewDispatcher(instruction: AggregateSuperInstruction.ArrayNewIi) = dispatchInstruction { vstack, context -> ArrayNewExecutor(vstack, context, instruction) }

fun ArrayNewDispatcher(instruction: AggregateSuperInstruction.ArrayNewIs) = dispatchInstruction { vstack, context -> ArrayNewExecutor(vstack, context, instruction) }

fun ArrayNewDispatcher(instruction: AggregateSuperInstruction.ArrayNewSi) = dispatchInstruction { vstack, context -> ArrayNewExecutor(vstack, context, instruction) }

fun ArrayNewDispatcher(instruction: AggregateSuperInstruction.ArrayNewSs) = dispatchInstruction { vstack, context -> ArrayNewExecutor(vstack, context, instruction) }

fun ArrayNewFixedDispatcher(instruction: AggregateSuperInstruction.ArrayNewFixedS) = dispatchInstruction { vstack, context -> ArrayNewFixedExecutor(vstack, context, instruction) }

fun ArraySetDispatcher(instruction: AggregateSuperInstruction.ArraySetIi) = dispatchInstruction { vstack, context -> ArraySetExecutor(vstack, context, instruction) }

fun ArraySetDispatcher(instruction: AggregateSuperInstruction.ArraySetIs) = dispatchInstruction { vstack, context -> ArraySetExecutor(vstack, context, instruction) }

fun ArraySetDispatcher(instruction: AggregateSuperInstruction.ArraySetSi) = dispatchInstruction { vstack, context -> ArraySetExecutor(vstack, context, instruction) }

fun ArraySetDispatcher(instruction: AggregateSuperInstruction.ArraySetSs) = dispatchInstruction { vstack, context -> ArraySetExecutor(vstack, context, instruction) }

fun StructGetDispatcher(instruction: AggregateSuperInstruction.StructGetS) = dispatchInstruction { vstack, context -> StructGetExecutor(vstack, context, instruction) }

fun StructGetSignedDispatcher(instruction: AggregateSuperInstruction.StructGetSignedS) = PackedStructGetSignedDispatcher(instruction)

fun StructGetUnsignedDispatcher(instruction: AggregateSuperInstruction.StructGetUnsignedS) = PackedStructGetUnsignedDispatcher(instruction)

fun RefCastStructGetDispatcher(instruction: AggregateSuperInstruction.RefCastStructGetS) = dispatchInstruction { vstack, context -> RefCastStructGetExecutor(vstack, context, instruction) }

fun StructGetStructGetDispatcher(instruction: AggregateSuperInstruction.StructGetStructGetS) = dispatchInstruction { vstack, context -> StructGetStructGetExecutor(vstack, context, instruction) }

fun LocalSetStructGetDispatcher(instruction: AggregateSuperInstruction.LocalSetStructGetS) = dispatchInstruction { vstack, context -> LocalSetStructGetExecutor(vstack, context, instruction) }

fun StructNewDispatcher(instruction: AggregateSuperInstruction.StructNewS) = dispatchInstruction { vstack, context -> StructNewExecutor(vstack, context, instruction) }

fun StructNewDefaultDispatcher(instruction: AggregateSuperInstruction.StructNewDefaultS) = dispatchInstruction { vstack, context -> StructNewDefaultExecutor(vstack, context, instruction) }

fun StructSetDispatcher(instruction: AggregateSuperInstruction.StructSetI) = dispatchInstruction { vstack, context -> StructSetExecutor(vstack, context, instruction) }

fun StructSetDispatcher(instruction: AggregateSuperInstruction.StructSetS) = dispatchInstruction { vstack, context -> StructSetExecutor(vstack, context, instruction) }
