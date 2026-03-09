package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryCopyExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryFillExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryGrowExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.bulk.MemoryInitExecutor
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction

fun MemoryGrowDispatcher(instruction: FusedMemoryInstruction.MemoryGrowI) = dispatchInstruction(instruction, ::MemoryGrowExecutor)

fun MemoryGrowDispatcher(instruction: FusedMemoryInstruction.MemoryGrowS) = dispatchInstruction(instruction, ::MemoryGrowExecutor)

fun MemoryInitDispatcher(instruction: FusedMemoryInstruction.MemoryInitIii) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: FusedMemoryInstruction.MemoryInitIis) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: FusedMemoryInstruction.MemoryInitIsi) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: FusedMemoryInstruction.MemoryInitIss) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: FusedMemoryInstruction.MemoryInitSii) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: FusedMemoryInstruction.MemoryInitSis) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: FusedMemoryInstruction.MemoryInitSsi) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryInitDispatcher(instruction: FusedMemoryInstruction.MemoryInitSss) = dispatchInstruction(instruction, ::MemoryInitExecutor)

fun MemoryCopyDispatcher(instruction: FusedMemoryInstruction.MemoryCopyIii) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: FusedMemoryInstruction.MemoryCopyIis) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: FusedMemoryInstruction.MemoryCopyIsi) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: FusedMemoryInstruction.MemoryCopyIss) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: FusedMemoryInstruction.MemoryCopySii) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: FusedMemoryInstruction.MemoryCopySis) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: FusedMemoryInstruction.MemoryCopySsi) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryCopyDispatcher(instruction: FusedMemoryInstruction.MemoryCopySss) = dispatchInstruction(instruction, ::MemoryCopyExecutor)

fun MemoryFillDispatcher(instruction: FusedMemoryInstruction.MemoryFillIii) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: FusedMemoryInstruction.MemoryFillIis) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: FusedMemoryInstruction.MemoryFillIsi) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: FusedMemoryInstruction.MemoryFillIss) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: FusedMemoryInstruction.MemoryFillSii) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: FusedMemoryInstruction.MemoryFillSis) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: FusedMemoryInstruction.MemoryFillSsi) = dispatchInstruction(instruction, ::MemoryFillExecutor)

fun MemoryFillDispatcher(instruction: FusedMemoryInstruction.MemoryFillSss) = dispatchInstruction(instruction, ::MemoryFillExecutor)
