package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayLenExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewFixedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArraySetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.LocalSetStructGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.RefCastStructGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructGetStructGetExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructNewDefaultExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructNewExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.StructSetExecutor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopyIii,
) = dispatchInstruction { vstack, context ->
    ArrayCopyExecutor(vstack, context, instruction)
}

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopyIis,
) = dispatchInstruction { vstack, context ->
    ArrayCopyExecutor(vstack, context, instruction)
}

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopyIsi,
) = dispatchInstruction { vstack, context ->
    ArrayCopyExecutor(vstack, context, instruction)
}

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopyIss,
) = dispatchInstruction { vstack, context ->
    ArrayCopyExecutor(vstack, context, instruction)
}

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopySii,
) = dispatchInstruction { vstack, context ->
    ArrayCopyExecutor(vstack, context, instruction)
}

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopySis,
) = dispatchInstruction { vstack, context ->
    ArrayCopyExecutor(vstack, context, instruction)
}

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopySsi,
) = dispatchInstruction { vstack, context ->
    ArrayCopyExecutor(vstack, context, instruction)
}

fun ArrayCopyDispatcher(
    instruction: AggregateInstruction.ArrayCopySss,
) = dispatchInstruction { vstack, context ->
    ArrayCopyExecutor(vstack, context, instruction)
}

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFillIii,
) = dispatchInstruction { vstack, context ->
    ArrayFillExecutor(vstack, context, instruction)
}

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFillIis,
) = dispatchInstruction { vstack, context ->
    ArrayFillExecutor(vstack, context, instruction)
}

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFillIsi,
) = dispatchInstruction { vstack, context ->
    ArrayFillExecutor(vstack, context, instruction)
}

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFillIss,
) = dispatchInstruction { vstack, context ->
    ArrayFillExecutor(vstack, context, instruction)
}

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFillSii,
) = dispatchInstruction { vstack, context ->
    ArrayFillExecutor(vstack, context, instruction)
}

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFillSis,
) = dispatchInstruction { vstack, context ->
    ArrayFillExecutor(vstack, context, instruction)
}

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFillSsi,
) = dispatchInstruction { vstack, context ->
    ArrayFillExecutor(vstack, context, instruction)
}

fun ArrayFillDispatcher(
    instruction: AggregateInstruction.ArrayFillSss,
) = dispatchInstruction { vstack, context ->
    ArrayFillExecutor(vstack, context, instruction)
}

fun ArrayGetDispatcher(
    instruction: AggregateInstruction.ArrayGetI,
) = dispatchInstruction { vstack, context ->
    ArrayGetExecutor(vstack, context, instruction)
}

fun ArrayGetDispatcher(
    instruction: AggregateInstruction.ArrayGetS,
) = dispatchInstruction { vstack, context ->
    ArrayGetExecutor(vstack, context, instruction)
}

fun ArrayGetSignedDispatcher(
    instruction: AggregateInstruction.ArrayGetSignedI,
) = PackedArrayGetSignedDispatcher(instruction)

fun ArrayGetSignedDispatcher(
    instruction: AggregateInstruction.ArrayGetSignedS,
) = PackedArrayGetSignedDispatcher(instruction)

fun ArrayGetUnsignedDispatcher(
    instruction: AggregateInstruction.ArrayGetUnsignedI,
) = PackedArrayGetUnsignedDispatcher(instruction)

fun ArrayGetUnsignedDispatcher(
    instruction: AggregateInstruction.ArrayGetUnsignedS,
) = PackedArrayGetUnsignedDispatcher(instruction)

fun ArrayLenDispatcher(
    instruction: AggregateInstruction.ArrayLenS,
) = dispatchInstruction { vstack, context ->
    ArrayLenExecutor(vstack, context, instruction)
}

fun ArrayNewDispatcher(
    instruction: AggregateInstruction.ArrayNewIi,
) = dispatchInstruction { vstack, context ->
    ArrayNewExecutor(vstack, context, instruction)
}

fun ArrayNewDispatcher(
    instruction: AggregateInstruction.ArrayNewIs,
) = dispatchInstruction { vstack, context ->
    ArrayNewExecutor(vstack, context, instruction)
}

fun ArrayNewDispatcher(
    instruction: AggregateInstruction.ArrayNewSi,
) = dispatchInstruction { vstack, context ->
    ArrayNewExecutor(vstack, context, instruction)
}

fun ArrayNewDispatcher(
    instruction: AggregateInstruction.ArrayNewSs,
) = dispatchInstruction { vstack, context ->
    ArrayNewExecutor(vstack, context, instruction)
}

fun ArrayNewFixedDispatcher(
    instruction: AggregateInstruction.ArrayNewFixedS,
) = dispatchInstruction { vstack, context ->
    ArrayNewFixedExecutor(vstack, context, instruction)
}

fun ArraySetDispatcher(
    instruction: AggregateInstruction.ArraySetIi,
) = dispatchInstruction { vstack, context ->
    ArraySetExecutor(vstack, context, instruction)
}

fun ArraySetDispatcher(
    instruction: AggregateInstruction.ArraySetIs,
) = dispatchInstruction { vstack, context ->
    ArraySetExecutor(vstack, context, instruction)
}

fun ArraySetDispatcher(
    instruction: AggregateInstruction.ArraySetSi,
) = dispatchInstruction { vstack, context ->
    ArraySetExecutor(vstack, context, instruction)
}

fun ArraySetDispatcher(
    instruction: AggregateInstruction.ArraySetSs,
) = dispatchInstruction { vstack, context ->
    ArraySetExecutor(vstack, context, instruction)
}

fun StructGetDispatcher(
    instruction: AggregateInstruction.StructGetS,
) = dispatchInstruction { vstack, context ->
    StructGetExecutor(vstack, context, instruction)
}

fun StructGetSignedDispatcher(
    instruction: AggregateInstruction.StructGetSignedS,
) = PackedStructGetSignedDispatcher(instruction)

fun StructGetUnsignedDispatcher(
    instruction: AggregateInstruction.StructGetUnsignedS,
) = PackedStructGetUnsignedDispatcher(instruction)

fun RefCastStructGetDispatcher(
    instruction: AggregateInstruction.RefCastStructGetS,
) = dispatchInstruction { vstack, context ->
    RefCastStructGetExecutor(vstack, context, instruction)
}

fun StructGetStructGetDispatcher(
    instruction: AggregateInstruction.StructGetStructGetS,
) = dispatchInstruction { vstack, context ->
    StructGetStructGetExecutor(vstack, context, instruction)
}

fun LocalSetStructGetDispatcher(
    instruction: AggregateInstruction.LocalSetStructGetS,
) = dispatchInstruction { vstack, context ->
    LocalSetStructGetExecutor(vstack, context, instruction)
}

fun StructNewDispatcher(
    instruction: AggregateInstruction.StructNewS,
) = dispatchInstruction { vstack, context ->
    StructNewExecutor(vstack, context, instruction)
}

fun StructNewDefaultDispatcher(
    instruction: AggregateInstruction.StructNewDefaultS,
) = dispatchInstruction { vstack, context ->
    StructNewDefaultExecutor(vstack, context, instruction)
}

fun StructSetDispatcher(
    instruction: AggregateInstruction.StructSetI,
) = dispatchInstruction { vstack, context ->
    StructSetExecutor(vstack, context, instruction)
}

fun StructSetDispatcher(
    instruction: AggregateInstruction.StructSetS,
) = dispatchInstruction { vstack, context ->
    StructSetExecutor(vstack, context, instruction)
}
