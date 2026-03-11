package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.F32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.F64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32Load16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32Load16UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32Load8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32Load8UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load16SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load16UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load32SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load32UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load8SExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64Load8UExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.I64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.load.MemorySizeExecutor
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction

fun I32LoadDispatcher(instruction: MemorySuperInstruction.I32LoadI) = dispatchInstruction(instruction, ::I32LoadExecutor)

fun I32LoadDispatcher(instruction: MemorySuperInstruction.I32LoadS) = dispatchInstruction(instruction, ::I32LoadExecutor)

fun I64LoadDispatcher(instruction: MemorySuperInstruction.I64LoadI) = dispatchInstruction(instruction, ::I64LoadExecutor)

fun I64LoadDispatcher(instruction: MemorySuperInstruction.I64LoadS) = dispatchInstruction(instruction, ::I64LoadExecutor)

fun F32LoadDispatcher(instruction: MemorySuperInstruction.F32LoadI) = dispatchInstruction(instruction, ::F32LoadExecutor)

fun F32LoadDispatcher(instruction: MemorySuperInstruction.F32LoadS) = dispatchInstruction(instruction, ::F32LoadExecutor)

fun F64LoadDispatcher(instruction: MemorySuperInstruction.F64LoadI) = dispatchInstruction(instruction, ::F64LoadExecutor)

fun F64LoadDispatcher(instruction: MemorySuperInstruction.F64LoadS) = dispatchInstruction(instruction, ::F64LoadExecutor)

fun I32Load8SDispatcher(instruction: MemorySuperInstruction.I32Load8SI) = dispatchInstruction(instruction, ::I32Load8SExecutor)

fun I32Load8SDispatcher(instruction: MemorySuperInstruction.I32Load8SS) = dispatchInstruction(instruction, ::I32Load8SExecutor)

fun I32Load8UDispatcher(instruction: MemorySuperInstruction.I32Load8UI) = dispatchInstruction(instruction, ::I32Load8UExecutor)

fun I32Load8UDispatcher(instruction: MemorySuperInstruction.I32Load8US) = dispatchInstruction(instruction, ::I32Load8UExecutor)

fun I32Load16SDispatcher(instruction: MemorySuperInstruction.I32Load16SI) = dispatchInstruction(instruction, ::I32Load16SExecutor)

fun I32Load16SDispatcher(instruction: MemorySuperInstruction.I32Load16SS) = dispatchInstruction(instruction, ::I32Load16SExecutor)

fun I32Load16UDispatcher(instruction: MemorySuperInstruction.I32Load16UI) = dispatchInstruction(instruction, ::I32Load16UExecutor)

fun I32Load16UDispatcher(instruction: MemorySuperInstruction.I32Load16US) = dispatchInstruction(instruction, ::I32Load16UExecutor)

fun I64Load8SDispatcher(instruction: MemorySuperInstruction.I64Load8SI) = dispatchInstruction(instruction, ::I64Load8SExecutor)

fun I64Load8SDispatcher(instruction: MemorySuperInstruction.I64Load8SS) = dispatchInstruction(instruction, ::I64Load8SExecutor)

fun I64Load8UDispatcher(instruction: MemorySuperInstruction.I64Load8UI) = dispatchInstruction(instruction, ::I64Load8UExecutor)

fun I64Load8UDispatcher(instruction: MemorySuperInstruction.I64Load8US) = dispatchInstruction(instruction, ::I64Load8UExecutor)

fun I64Load16SDispatcher(instruction: MemorySuperInstruction.I64Load16SI) = dispatchInstruction(instruction, ::I64Load16SExecutor)

fun I64Load16SDispatcher(instruction: MemorySuperInstruction.I64Load16SS) = dispatchInstruction(instruction, ::I64Load16SExecutor)

fun I64Load16UDispatcher(instruction: MemorySuperInstruction.I64Load16UI) = dispatchInstruction(instruction, ::I64Load16UExecutor)

fun I64Load16UDispatcher(instruction: MemorySuperInstruction.I64Load16US) = dispatchInstruction(instruction, ::I64Load16UExecutor)

fun I64Load32SDispatcher(instruction: MemorySuperInstruction.I64Load32SI) = dispatchInstruction(instruction, ::I64Load32SExecutor)

fun I64Load32SDispatcher(instruction: MemorySuperInstruction.I64Load32SS) = dispatchInstruction(instruction, ::I64Load32SExecutor)

fun I64Load32UDispatcher(instruction: MemorySuperInstruction.I64Load32UI) = dispatchInstruction(instruction, ::I64Load32UExecutor)

fun I64Load32UDispatcher(instruction: MemorySuperInstruction.I64Load32US) = dispatchInstruction(instruction, ::I64Load32UExecutor)

fun MemorySizeDispatcher(instruction: MemorySuperInstruction.MemorySizeS) = dispatchInstruction(instruction, ::MemorySizeExecutor)
