package io.github.charlietap.chasm.executor.invoker.dispatch.aggregate

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.AnyConvertExternExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayInitDataExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayInitElementExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewDataExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewDefaultExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ArrayNewElementExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.ExternConvertAnyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.I31GetSignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.I31GetUnsignedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.aggregate.RefI31Executor
import io.github.charlietap.chasm.runtime.instruction.AggregateInstruction

fun ArrayNewDefaultDispatcher(
    instruction: AggregateInstruction.ArrayNewDefaultI,
) = dispatchInstruction { vstack, context ->
    ArrayNewDefaultExecutor(vstack, context, instruction)
}

fun ArrayNewDefaultDispatcher(
    instruction: AggregateInstruction.ArrayNewDefaultS,
) = dispatchInstruction { vstack, context ->
    ArrayNewDefaultExecutor(vstack, context, instruction)
}

fun ArrayNewDataDispatcher(
    instruction: AggregateInstruction.ArrayNewDataIi,
) = dispatchInstruction { vstack, context ->
    ArrayNewDataExecutor(vstack, context, instruction)
}

fun ArrayNewDataDispatcher(
    instruction: AggregateInstruction.ArrayNewDataIs,
) = dispatchInstruction { vstack, context ->
    ArrayNewDataExecutor(vstack, context, instruction)
}

fun ArrayNewDataDispatcher(
    instruction: AggregateInstruction.ArrayNewDataSi,
) = dispatchInstruction { vstack, context ->
    ArrayNewDataExecutor(vstack, context, instruction)
}

fun ArrayNewDataDispatcher(
    instruction: AggregateInstruction.ArrayNewDataSs,
) = dispatchInstruction { vstack, context ->
    ArrayNewDataExecutor(vstack, context, instruction)
}

fun ArrayNewElementDispatcher(
    instruction: AggregateInstruction.ArrayNewElementIi,
) = dispatchInstruction { vstack, context ->
    ArrayNewElementExecutor(vstack, context, instruction)
}

fun ArrayNewElementDispatcher(
    instruction: AggregateInstruction.ArrayNewElementIs,
) = dispatchInstruction { vstack, context ->
    ArrayNewElementExecutor(vstack, context, instruction)
}

fun ArrayNewElementDispatcher(
    instruction: AggregateInstruction.ArrayNewElementSi,
) = dispatchInstruction { vstack, context ->
    ArrayNewElementExecutor(vstack, context, instruction)
}

fun ArrayNewElementDispatcher(
    instruction: AggregateInstruction.ArrayNewElementSs,
) = dispatchInstruction { vstack, context ->
    ArrayNewElementExecutor(vstack, context, instruction)
}

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitDataIii,
) = dispatchInstruction { vstack, context ->
    ArrayInitDataExecutor(vstack, context, instruction)
}

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitDataIis,
) = dispatchInstruction { vstack, context ->
    ArrayInitDataExecutor(vstack, context, instruction)
}

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitDataIsi,
) = dispatchInstruction { vstack, context ->
    ArrayInitDataExecutor(vstack, context, instruction)
}

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitDataIss,
) = dispatchInstruction { vstack, context ->
    ArrayInitDataExecutor(vstack, context, instruction)
}

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitDataSii,
) = dispatchInstruction { vstack, context ->
    ArrayInitDataExecutor(vstack, context, instruction)
}

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitDataSis,
) = dispatchInstruction { vstack, context ->
    ArrayInitDataExecutor(vstack, context, instruction)
}

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitDataSsi,
) = dispatchInstruction { vstack, context ->
    ArrayInitDataExecutor(vstack, context, instruction)
}

fun ArrayInitDataDispatcher(
    instruction: AggregateInstruction.ArrayInitDataSss,
) = dispatchInstruction { vstack, context ->
    ArrayInitDataExecutor(vstack, context, instruction)
}

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElementIii,
) = dispatchInstruction { vstack, context ->
    ArrayInitElementExecutor(vstack, context, instruction)
}

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElementIis,
) = dispatchInstruction { vstack, context ->
    ArrayInitElementExecutor(vstack, context, instruction)
}

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElementIsi,
) = dispatchInstruction { vstack, context ->
    ArrayInitElementExecutor(vstack, context, instruction)
}

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElementIss,
) = dispatchInstruction { vstack, context ->
    ArrayInitElementExecutor(vstack, context, instruction)
}

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElementSii,
) = dispatchInstruction { vstack, context ->
    ArrayInitElementExecutor(vstack, context, instruction)
}

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElementSis,
) = dispatchInstruction { vstack, context ->
    ArrayInitElementExecutor(vstack, context, instruction)
}

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElementSsi,
) = dispatchInstruction { vstack, context ->
    ArrayInitElementExecutor(vstack, context, instruction)
}

fun ArrayInitElementDispatcher(
    instruction: AggregateInstruction.ArrayInitElementSss,
) = dispatchInstruction { vstack, context ->
    ArrayInitElementExecutor(vstack, context, instruction)
}

fun RefI31Dispatcher(
    instruction: AggregateInstruction.RefI31I,
) = dispatchInstruction { vstack, context ->
    RefI31Executor(vstack, context, instruction)
}

fun RefI31Dispatcher(
    instruction: AggregateInstruction.RefI31S,
) = dispatchInstruction { vstack, context ->
    RefI31Executor(vstack, context, instruction)
}

fun I31GetSignedDispatcher(
    instruction: AggregateInstruction.I31GetSignedS,
) = dispatchInstruction { vstack, context ->
    I31GetSignedExecutor(vstack, context, instruction)
}

fun I31GetUnsignedDispatcher(
    instruction: AggregateInstruction.I31GetUnsignedS,
) = dispatchInstruction { vstack, context ->
    I31GetUnsignedExecutor(vstack, context, instruction)
}

fun AnyConvertExternDispatcher(
    instruction: AggregateInstruction.AnyConvertExternS,
) = dispatchInstruction { vstack, context ->
    AnyConvertExternExecutor(vstack, context, instruction)
}

fun ExternConvertAnyDispatcher(
    instruction: AggregateInstruction.ExternConvertAnyS,
) = dispatchInstruction { vstack, context ->
    ExternConvertAnyExecutor(vstack, context, instruction)
}
