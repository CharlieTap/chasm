package io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.F32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.F64StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I32Store16Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I32Store8Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I64Store16Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I64Store32Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I64Store8Executor
import io.github.charlietap.chasm.executor.invoker.instruction.memoryfused.store.I64StoreExecutor
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction

fun I32StoreDispatcher(instruction: MemorySuperInstruction.I32StoreIi) = dispatchInstruction(instruction, ::I32StoreExecutor)

fun I32StoreDispatcher(instruction: MemorySuperInstruction.I32StoreIs) = dispatchInstruction(instruction, ::I32StoreExecutor)

fun I32StoreDispatcher(instruction: MemorySuperInstruction.I32StoreSi) = dispatchInstruction(instruction, ::I32StoreExecutor)

fun I32StoreDispatcher(instruction: MemorySuperInstruction.I32StoreSs) = dispatchInstruction(instruction, ::I32StoreExecutor)

fun I64StoreDispatcher(instruction: MemorySuperInstruction.I64StoreIi) = dispatchInstruction(instruction, ::I64StoreExecutor)

fun I64StoreDispatcher(instruction: MemorySuperInstruction.I64StoreIs) = dispatchInstruction(instruction, ::I64StoreExecutor)

fun I64StoreDispatcher(instruction: MemorySuperInstruction.I64StoreSi) = dispatchInstruction(instruction, ::I64StoreExecutor)

fun I64StoreDispatcher(instruction: MemorySuperInstruction.I64StoreSs) = dispatchInstruction(instruction, ::I64StoreExecutor)

fun F32StoreDispatcher(instruction: MemorySuperInstruction.F32StoreIi) = dispatchInstruction(instruction, ::F32StoreExecutor)

fun F32StoreDispatcher(instruction: MemorySuperInstruction.F32StoreIs) = dispatchInstruction(instruction, ::F32StoreExecutor)

fun F32StoreDispatcher(instruction: MemorySuperInstruction.F32StoreSi) = dispatchInstruction(instruction, ::F32StoreExecutor)

fun F32StoreDispatcher(instruction: MemorySuperInstruction.F32StoreSs) = dispatchInstruction(instruction, ::F32StoreExecutor)

fun F64StoreDispatcher(instruction: MemorySuperInstruction.F64StoreIi) = dispatchInstruction(instruction, ::F64StoreExecutor)

fun F64StoreDispatcher(instruction: MemorySuperInstruction.F64StoreIs) = dispatchInstruction(instruction, ::F64StoreExecutor)

fun F64StoreDispatcher(instruction: MemorySuperInstruction.F64StoreSi) = dispatchInstruction(instruction, ::F64StoreExecutor)

fun F64StoreDispatcher(instruction: MemorySuperInstruction.F64StoreSs) = dispatchInstruction(instruction, ::F64StoreExecutor)

fun I32Store8Dispatcher(instruction: MemorySuperInstruction.I32Store8Ii) = dispatchInstruction(instruction, ::I32Store8Executor)

fun I32Store8Dispatcher(instruction: MemorySuperInstruction.I32Store8Is) = dispatchInstruction(instruction, ::I32Store8Executor)

fun I32Store8Dispatcher(instruction: MemorySuperInstruction.I32Store8Si) = dispatchInstruction(instruction, ::I32Store8Executor)

fun I32Store8Dispatcher(instruction: MemorySuperInstruction.I32Store8Ss) = dispatchInstruction(instruction, ::I32Store8Executor)

fun I32Store16Dispatcher(instruction: MemorySuperInstruction.I32Store16Ii) = dispatchInstruction(instruction, ::I32Store16Executor)

fun I32Store16Dispatcher(instruction: MemorySuperInstruction.I32Store16Is) = dispatchInstruction(instruction, ::I32Store16Executor)

fun I32Store16Dispatcher(instruction: MemorySuperInstruction.I32Store16Si) = dispatchInstruction(instruction, ::I32Store16Executor)

fun I32Store16Dispatcher(instruction: MemorySuperInstruction.I32Store16Ss) = dispatchInstruction(instruction, ::I32Store16Executor)

fun I64Store8Dispatcher(instruction: MemorySuperInstruction.I64Store8Ii) = dispatchInstruction(instruction, ::I64Store8Executor)

fun I64Store8Dispatcher(instruction: MemorySuperInstruction.I64Store8Is) = dispatchInstruction(instruction, ::I64Store8Executor)

fun I64Store8Dispatcher(instruction: MemorySuperInstruction.I64Store8Si) = dispatchInstruction(instruction, ::I64Store8Executor)

fun I64Store8Dispatcher(instruction: MemorySuperInstruction.I64Store8Ss) = dispatchInstruction(instruction, ::I64Store8Executor)

fun I64Store16Dispatcher(instruction: MemorySuperInstruction.I64Store16Ii) = dispatchInstruction(instruction, ::I64Store16Executor)

fun I64Store16Dispatcher(instruction: MemorySuperInstruction.I64Store16Is) = dispatchInstruction(instruction, ::I64Store16Executor)

fun I64Store16Dispatcher(instruction: MemorySuperInstruction.I64Store16Si) = dispatchInstruction(instruction, ::I64Store16Executor)

fun I64Store16Dispatcher(instruction: MemorySuperInstruction.I64Store16Ss) = dispatchInstruction(instruction, ::I64Store16Executor)

fun I64Store32Dispatcher(instruction: MemorySuperInstruction.I64Store32Ii) = dispatchInstruction(instruction, ::I64Store32Executor)

fun I64Store32Dispatcher(instruction: MemorySuperInstruction.I64Store32Is) = dispatchInstruction(instruction, ::I64Store32Executor)

fun I64Store32Dispatcher(instruction: MemorySuperInstruction.I64Store32Si) = dispatchInstruction(instruction, ::I64Store32Executor)

fun I64Store32Dispatcher(instruction: MemorySuperInstruction.I64Store32Ss) = dispatchInstruction(instruction, ::I64Store32Executor)
