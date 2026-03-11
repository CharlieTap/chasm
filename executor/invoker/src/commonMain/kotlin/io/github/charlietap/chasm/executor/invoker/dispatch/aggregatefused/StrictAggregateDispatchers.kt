package io.github.charlietap.chasm.executor.invoker.dispatch.aggregatefused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayGetSignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayGetUnsignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayLenExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayNewExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArrayNewFixedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.ArraySetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetSignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructGetUnsignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructNewDefaultExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructNewExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregatefused.StructSetExecutor
import io.github.charlietap.chasm.runtime.instruction.AggregateSuperInstruction

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopyIii) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopyIis) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopyIsi) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopyIss) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopySii) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopySis) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopySsi) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: AggregateSuperInstruction.ArrayCopySss) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillIii) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillIis) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillIsi) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillIss) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillSii) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillSis) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillSsi) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: AggregateSuperInstruction.ArrayFillSss) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayGetDispatcher(instruction: AggregateSuperInstruction.ArrayGetI) = dispatchInstruction(instruction, ::ArrayGetExecutor)

fun ArrayGetDispatcher(instruction: AggregateSuperInstruction.ArrayGetS) = dispatchInstruction(instruction, ::ArrayGetExecutor)

fun ArrayGetSignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetSignedI) = dispatchInstruction(instruction, ::ArrayGetSignedExecutor)

fun ArrayGetSignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetSignedS) = dispatchInstruction(instruction, ::ArrayGetSignedExecutor)

fun ArrayGetUnsignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetUnsignedI) = dispatchInstruction(instruction, ::ArrayGetUnsignedExecutor)

fun ArrayGetUnsignedDispatcher(instruction: AggregateSuperInstruction.ArrayGetUnsignedS) = dispatchInstruction(instruction, ::ArrayGetUnsignedExecutor)

fun ArrayLenDispatcher(instruction: AggregateSuperInstruction.ArrayLenS) = dispatchInstruction(instruction, ::ArrayLenExecutor)

fun ArrayNewDispatcher(instruction: AggregateSuperInstruction.ArrayNewIi) = dispatchInstruction(instruction, ::ArrayNewExecutor)

fun ArrayNewDispatcher(instruction: AggregateSuperInstruction.ArrayNewIs) = dispatchInstruction(instruction, ::ArrayNewExecutor)

fun ArrayNewDispatcher(instruction: AggregateSuperInstruction.ArrayNewSi) = dispatchInstruction(instruction, ::ArrayNewExecutor)

fun ArrayNewDispatcher(instruction: AggregateSuperInstruction.ArrayNewSs) = dispatchInstruction(instruction, ::ArrayNewExecutor)

fun ArrayNewFixedDispatcher(instruction: AggregateSuperInstruction.ArrayNewFixedS) = dispatchInstruction(instruction, ::ArrayNewFixedExecutor)

fun ArraySetDispatcher(instruction: AggregateSuperInstruction.ArraySetIi) = dispatchInstruction(instruction, ::ArraySetExecutor)

fun ArraySetDispatcher(instruction: AggregateSuperInstruction.ArraySetIs) = dispatchInstruction(instruction, ::ArraySetExecutor)

fun ArraySetDispatcher(instruction: AggregateSuperInstruction.ArraySetSi) = dispatchInstruction(instruction, ::ArraySetExecutor)

fun ArraySetDispatcher(instruction: AggregateSuperInstruction.ArraySetSs) = dispatchInstruction(instruction, ::ArraySetExecutor)

fun StructGetDispatcher(instruction: AggregateSuperInstruction.StructGetS) = dispatchInstruction(instruction, ::StructGetExecutor)

fun StructGetSignedDispatcher(instruction: AggregateSuperInstruction.StructGetSignedS) = dispatchInstruction(instruction, ::StructGetSignedExecutor)

fun StructGetUnsignedDispatcher(instruction: AggregateSuperInstruction.StructGetUnsignedS) = dispatchInstruction(instruction, ::StructGetUnsignedExecutor)

fun StructNewDispatcher(instruction: AggregateSuperInstruction.StructNewS) = dispatchInstruction(instruction, ::StructNewExecutor)

fun StructNewDefaultDispatcher(instruction: AggregateSuperInstruction.StructNewDefaultS) = dispatchInstruction(instruction, ::StructNewDefaultExecutor)

fun StructSetDispatcher(instruction: AggregateSuperInstruction.StructSetI) = dispatchInstruction(instruction, ::StructSetExecutor)

fun StructSetDispatcher(instruction: AggregateSuperInstruction.StructSetS) = dispatchInstruction(instruction, ::StructSetExecutor)
