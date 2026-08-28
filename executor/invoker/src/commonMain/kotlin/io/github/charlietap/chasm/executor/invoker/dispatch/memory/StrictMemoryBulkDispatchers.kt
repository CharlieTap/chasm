package io.github.charlietap.chasm.executor.invoker.dispatch.memory

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryGrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.bulk.MemoryInitExecutor
import io.github.charlietap.chasm.runtime.instruction.MemoryInstruction

fun MemoryGrowDispatcher(
    instruction: MemoryInstruction.MemoryGrowI,
) = dispatchInstruction { vstack, context ->
    MemoryGrowExecutor(vstack, context, instruction)
}

fun MemoryGrowDispatcher(
    instruction: MemoryInstruction.MemoryGrowS,
) = dispatchInstruction { vstack, context ->
    MemoryGrowExecutor(vstack, context, instruction)
}

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInitIii,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(vstack, context, instruction)
}

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInitIis,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(vstack, context, instruction)
}

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInitIsi,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(vstack, context, instruction)
}

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInitIss,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(vstack, context, instruction)
}

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInitSii,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(vstack, context, instruction)
}

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInitSis,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(vstack, context, instruction)
}

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInitSsi,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(vstack, context, instruction)
}

fun MemoryInitDispatcher(
    instruction: MemoryInstruction.MemoryInitSss,
) = dispatchInstruction { vstack, context ->
    MemoryInitExecutor(vstack, context, instruction)
}

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopyIii,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(vstack, context, instruction)
}

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopyIis,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(vstack, context, instruction)
}

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopyIsi,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(vstack, context, instruction)
}

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopyIss,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(vstack, context, instruction)
}

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopySii,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(vstack, context, instruction)
}

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopySis,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(vstack, context, instruction)
}

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopySsi,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(vstack, context, instruction)
}

fun MemoryCopyDispatcher(
    instruction: MemoryInstruction.MemoryCopySss,
) = dispatchInstruction { vstack, context ->
    MemoryCopyExecutor(vstack, context, instruction)
}

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFillIii,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(vstack, context, instruction)
}

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFillIis,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(vstack, context, instruction)
}

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFillIsi,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(vstack, context, instruction)
}

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFillIss,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(vstack, context, instruction)
}

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFillSii,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(vstack, context, instruction)
}

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFillSis,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(vstack, context, instruction)
}

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFillSsi,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(vstack, context, instruction)
}

fun MemoryFillDispatcher(
    instruction: MemoryInstruction.MemoryFillSss,
) = dispatchInstruction { vstack, context ->
    MemoryFillExecutor(vstack, context, instruction)
}
