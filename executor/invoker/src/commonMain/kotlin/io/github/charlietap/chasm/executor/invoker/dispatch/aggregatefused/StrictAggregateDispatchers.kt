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
import io.github.charlietap.chasm.runtime.instruction.FusedAggregateInstruction

fun ArrayCopyDispatcher(instruction: FusedAggregateInstruction.ArrayCopyIii) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: FusedAggregateInstruction.ArrayCopyIis) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: FusedAggregateInstruction.ArrayCopyIsi) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: FusedAggregateInstruction.ArrayCopyIss) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: FusedAggregateInstruction.ArrayCopySii) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: FusedAggregateInstruction.ArrayCopySis) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: FusedAggregateInstruction.ArrayCopySsi) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayCopyDispatcher(instruction: FusedAggregateInstruction.ArrayCopySss) = dispatchInstruction(instruction, ::ArrayCopyExecutor)

fun ArrayFillDispatcher(instruction: FusedAggregateInstruction.ArrayFillIii) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: FusedAggregateInstruction.ArrayFillIis) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: FusedAggregateInstruction.ArrayFillIsi) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: FusedAggregateInstruction.ArrayFillIss) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: FusedAggregateInstruction.ArrayFillSii) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: FusedAggregateInstruction.ArrayFillSis) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: FusedAggregateInstruction.ArrayFillSsi) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayFillDispatcher(instruction: FusedAggregateInstruction.ArrayFillSss) = dispatchInstruction(instruction, ::ArrayFillExecutor)

fun ArrayGetDispatcher(instruction: FusedAggregateInstruction.ArrayGetI) = dispatchInstruction(instruction, ::ArrayGetExecutor)

fun ArrayGetDispatcher(instruction: FusedAggregateInstruction.ArrayGetS) = dispatchInstruction(instruction, ::ArrayGetExecutor)

fun ArrayGetSignedDispatcher(instruction: FusedAggregateInstruction.ArrayGetSignedI) = dispatchInstruction(instruction, ::ArrayGetSignedExecutor)

fun ArrayGetSignedDispatcher(instruction: FusedAggregateInstruction.ArrayGetSignedS) = dispatchInstruction(instruction, ::ArrayGetSignedExecutor)

fun ArrayGetUnsignedDispatcher(instruction: FusedAggregateInstruction.ArrayGetUnsignedI) = dispatchInstruction(instruction, ::ArrayGetUnsignedExecutor)

fun ArrayGetUnsignedDispatcher(instruction: FusedAggregateInstruction.ArrayGetUnsignedS) = dispatchInstruction(instruction, ::ArrayGetUnsignedExecutor)

fun ArrayLenDispatcher(instruction: FusedAggregateInstruction.ArrayLenS) = dispatchInstruction(instruction, ::ArrayLenExecutor)

fun ArrayNewDispatcher(instruction: FusedAggregateInstruction.ArrayNewIi) = dispatchInstruction(instruction, ::ArrayNewExecutor)

fun ArrayNewDispatcher(instruction: FusedAggregateInstruction.ArrayNewIs) = dispatchInstruction(instruction, ::ArrayNewExecutor)

fun ArrayNewDispatcher(instruction: FusedAggregateInstruction.ArrayNewSi) = dispatchInstruction(instruction, ::ArrayNewExecutor)

fun ArrayNewDispatcher(instruction: FusedAggregateInstruction.ArrayNewSs) = dispatchInstruction(instruction, ::ArrayNewExecutor)

fun ArrayNewFixedDispatcher(instruction: FusedAggregateInstruction.ArrayNewFixedS) = dispatchInstruction(instruction, ::ArrayNewFixedExecutor)

fun ArraySetDispatcher(instruction: FusedAggregateInstruction.ArraySetIi) = dispatchInstruction(instruction, ::ArraySetExecutor)

fun ArraySetDispatcher(instruction: FusedAggregateInstruction.ArraySetIs) = dispatchInstruction(instruction, ::ArraySetExecutor)

fun ArraySetDispatcher(instruction: FusedAggregateInstruction.ArraySetSi) = dispatchInstruction(instruction, ::ArraySetExecutor)

fun ArraySetDispatcher(instruction: FusedAggregateInstruction.ArraySetSs) = dispatchInstruction(instruction, ::ArraySetExecutor)

fun StructGetDispatcher(instruction: FusedAggregateInstruction.StructGetS) = dispatchInstruction(instruction, ::StructGetExecutor)

fun StructGetSignedDispatcher(instruction: FusedAggregateInstruction.StructGetSignedS) = dispatchInstruction(instruction, ::StructGetSignedExecutor)

fun StructGetUnsignedDispatcher(instruction: FusedAggregateInstruction.StructGetUnsignedS) = dispatchInstruction(instruction, ::StructGetUnsignedExecutor)

fun StructNewDispatcher(instruction: FusedAggregateInstruction.StructNewS) = dispatchInstruction(instruction, ::StructNewExecutor)

fun StructNewDefaultDispatcher(instruction: FusedAggregateInstruction.StructNewDefaultS) = dispatchInstruction(instruction, ::StructNewDefaultExecutor)

fun StructSetDispatcher(instruction: FusedAggregateInstruction.StructSetI) = dispatchInstruction(instruction, ::StructSetExecutor)

fun StructSetDispatcher(instruction: FusedAggregateInstruction.StructSetS) = dispatchInstruction(instruction, ::StructSetExecutor)
