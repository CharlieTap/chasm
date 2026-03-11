package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryGrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryInitExecutor
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction

fun MemoryGrowDispatcher(instruction: MemorySuperInstruction.MemoryGrowI) = dispatchInstruction(instruction, ::MemoryGrowExecutor)

fun MemoryGrowDispatcher(instruction: MemorySuperInstruction.MemoryGrowS) = dispatchInstruction(instruction, ::MemoryGrowExecutor)

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitIii) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitIis) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitIsi) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitIss) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitSii) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitSis) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitSsi) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: MemorySuperInstruction.MemoryInitSss) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopyIii) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopyIis) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopyIsi) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopyIss) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopySii) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopySis) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopySsi) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: MemorySuperInstruction.MemoryCopySss) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillIii) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillIis) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillIsi) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillIss) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillSii) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillSis) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillSsi) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: MemorySuperInstruction.MemoryFillSss) = dispatchInstruction(instruction, ::MemoryFillExecutor)
