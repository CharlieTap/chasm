package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryGrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryInitExecutor
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction

fun MemoryGrowDispatcher(instruction: MemorySuperInstruction.MemoryGrowI) = dispatchInstruction { vstack, context -> MemoryGrowExecutor(vstack, context, instruction) }

fun MemoryGrowDispatcher(instruction: MemorySuperInstruction.MemoryGrowS) = dispatchInstruction { vstack, context -> MemoryGrowExecutor(vstack, context, instruction) }

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitIii) = dispatchInstruction { vstack, context -> MemoryInitExecutor(vstack, context, instruction) }

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitIis) = dispatchInstruction { vstack, context -> MemoryInitExecutor(vstack, context, instruction) }

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitIsi) = dispatchInstruction { vstack, context -> MemoryInitExecutor(vstack, context, instruction) }

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitIss) = dispatchInstruction { vstack, context -> MemoryInitExecutor(vstack, context, instruction) }

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitSii) = dispatchInstruction { vstack, context -> MemoryInitExecutor(vstack, context, instruction) }

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitSis) = dispatchInstruction { vstack, context -> MemoryInitExecutor(vstack, context, instruction) }

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitSsi) = dispatchInstruction { vstack, context -> MemoryInitExecutor(vstack, context, instruction) }

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitSss) = dispatchInstruction { vstack, context -> MemoryInitExecutor(vstack, context, instruction) }

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopyIii) = dispatchInstruction { vstack, context -> MemoryCopyExecutor(vstack, context, instruction) }

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopyIis) = dispatchInstruction { vstack, context -> MemoryCopyExecutor(vstack, context, instruction) }

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopyIsi) = dispatchInstruction { vstack, context -> MemoryCopyExecutor(vstack, context, instruction) }

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopyIss) = dispatchInstruction { vstack, context -> MemoryCopyExecutor(vstack, context, instruction) }

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopySii) = dispatchInstruction { vstack, context -> MemoryCopyExecutor(vstack, context, instruction) }

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopySis) = dispatchInstruction { vstack, context -> MemoryCopyExecutor(vstack, context, instruction) }

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopySsi) = dispatchInstruction { vstack, context -> MemoryCopyExecutor(vstack, context, instruction) }

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopySss) = dispatchInstruction { vstack, context -> MemoryCopyExecutor(vstack, context, instruction) }

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillIii) = dispatchInstruction { vstack, context -> MemoryFillExecutor(vstack, context, instruction) }

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillIis) = dispatchInstruction { vstack, context -> MemoryFillExecutor(vstack, context, instruction) }

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillIsi) = dispatchInstruction { vstack, context -> MemoryFillExecutor(vstack, context, instruction) }

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillIss) = dispatchInstruction { vstack, context -> MemoryFillExecutor(vstack, context, instruction) }

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillSii) = dispatchInstruction { vstack, context -> MemoryFillExecutor(vstack, context, instruction) }

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillSis) = dispatchInstruction { vstack, context -> MemoryFillExecutor(vstack, context, instruction) }

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillSsi) = dispatchInstruction { vstack, context -> MemoryFillExecutor(vstack, context, instruction) }

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillSss) = dispatchInstruction { vstack, context -> MemoryFillExecutor(vstack, context, instruction) }
