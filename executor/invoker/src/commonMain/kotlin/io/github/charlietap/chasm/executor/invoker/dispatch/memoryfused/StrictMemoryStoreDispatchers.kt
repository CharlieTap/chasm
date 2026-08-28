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
import io.github.charlietap.chasm.memory.write.I32ToI16Writer
import io.github.charlietap.chasm.memory.write.I32ToI8Writer
import io.github.charlietap.chasm.memory.write.I32Writer
import io.github.charlietap.chasm.memory.write.I64ToI32Writer
import io.github.charlietap.chasm.memory.write.I64Writer
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction

fun I32StoreDispatcher(instruction: MemorySuperInstruction.I32StoreIi) = dispatchInstruction { vstack, context -> I32StoreExecutor(vstack, context, instruction) }

fun I32StoreDispatcher(instruction: MemorySuperInstruction.I32StoreIs) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    val value = instruction.value
    memoryStoreNoOffsetDispatcher(instruction.memory, Int.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { _, data, address ->
        I32Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context -> I32StoreExecutor(vstack, context, instruction) }
}

fun I32StoreDispatcher(instruction: MemorySuperInstruction.I32StoreSi) = dispatchInstruction { vstack, context -> I32StoreExecutor(vstack, context, instruction) }

fun I32StoreDispatcher(instruction: MemorySuperInstruction.I32StoreSs) = if (instruction.memArg.offset == 0) {
    val memory = instruction.memory
    val addressSlot = instruction.addressSlot
    val valueSlot = instruction.valueSlot
    memoryStoreNoOffsetDispatcher(memory, Int.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { vstack, data, address ->
        I32Writer(data, address, vstack.getFrameSlot(valueSlot).toInt())
    }
} else {
    dispatchInstruction { vstack, context -> I32StoreExecutor(vstack, context, instruction) }
}

fun I64StoreDispatcher(instruction: MemorySuperInstruction.I64StoreIi) = dispatchInstruction { vstack, context -> I64StoreExecutor(vstack, context, instruction) }

fun I64StoreDispatcher(instruction: MemorySuperInstruction.I64StoreIs) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    val value = instruction.value
    memoryStoreNoOffsetDispatcher(instruction.memory, Long.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { _, data, address ->
        I64Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context -> I64StoreExecutor(vstack, context, instruction) }
}

fun I64StoreDispatcher(instruction: MemorySuperInstruction.I64StoreSi) = if (instruction.memArg.offset == 0) {
    val address = instruction.address
    val valueSlot = instruction.valueSlot
    memoryStoreNoOffsetDispatcher(instruction.memory, Long.SIZE_BYTES, { address }) { vstack, data, effectiveAddress ->
        I64Writer(data, effectiveAddress, vstack.getFrameSlot(valueSlot))
    }
} else {
    dispatchInstruction { vstack, context -> I64StoreExecutor(vstack, context, instruction) }
}

fun I64StoreDispatcher(instruction: MemorySuperInstruction.I64StoreSs) = dispatchInstruction { vstack, context -> I64StoreExecutor(vstack, context, instruction) }

fun F32StoreDispatcher(instruction: MemorySuperInstruction.F32StoreIi) = dispatchInstruction { vstack, context -> F32StoreExecutor(vstack, context, instruction) }

fun F32StoreDispatcher(instruction: MemorySuperInstruction.F32StoreIs) = dispatchInstruction { vstack, context -> F32StoreExecutor(vstack, context, instruction) }

fun F32StoreDispatcher(instruction: MemorySuperInstruction.F32StoreSi) = dispatchInstruction { vstack, context -> F32StoreExecutor(vstack, context, instruction) }

fun F32StoreDispatcher(instruction: MemorySuperInstruction.F32StoreSs) = dispatchInstruction { vstack, context -> F32StoreExecutor(vstack, context, instruction) }

fun F64StoreDispatcher(instruction: MemorySuperInstruction.F64StoreIi) = dispatchInstruction { vstack, context -> F64StoreExecutor(vstack, context, instruction) }

fun F64StoreDispatcher(instruction: MemorySuperInstruction.F64StoreIs) = dispatchInstruction { vstack, context -> F64StoreExecutor(vstack, context, instruction) }

fun F64StoreDispatcher(instruction: MemorySuperInstruction.F64StoreSi) = dispatchInstruction { vstack, context -> F64StoreExecutor(vstack, context, instruction) }

fun F64StoreDispatcher(instruction: MemorySuperInstruction.F64StoreSs) = dispatchInstruction { vstack, context -> F64StoreExecutor(vstack, context, instruction) }

fun I32Store8Dispatcher(instruction: MemorySuperInstruction.I32Store8Ii) = dispatchInstruction { vstack, context -> I32Store8Executor(vstack, context, instruction) }

fun I32Store8Dispatcher(instruction: MemorySuperInstruction.I32Store8Is) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    val value = instruction.value
    memoryStoreNoOffsetDispatcher(instruction.memory, Byte.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { _, data, address ->
        I32ToI8Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context -> I32Store8Executor(vstack, context, instruction) }
}

fun I32Store8Dispatcher(instruction: MemorySuperInstruction.I32Store8Si) = dispatchInstruction { vstack, context -> I32Store8Executor(vstack, context, instruction) }

fun I32Store8Dispatcher(instruction: MemorySuperInstruction.I32Store8Ss) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    val valueSlot = instruction.valueSlot
    memoryStoreNoOffsetDispatcher(instruction.memory, Byte.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { vstack, data, address ->
        I32ToI8Writer(data, address, vstack.getFrameSlot(valueSlot).toInt())
    }
} else {
    dispatchInstruction { vstack, context -> I32Store8Executor(vstack, context, instruction) }
}

fun I32Store16Dispatcher(instruction: MemorySuperInstruction.I32Store16Ii) = dispatchInstruction { vstack, context -> I32Store16Executor(vstack, context, instruction) }

fun I32Store16Dispatcher(instruction: MemorySuperInstruction.I32Store16Is) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    val value = instruction.value
    memoryStoreNoOffsetDispatcher(instruction.memory, Short.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { _, data, address ->
        I32ToI16Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context -> I32Store16Executor(vstack, context, instruction) }
}

fun I32Store16Dispatcher(instruction: MemorySuperInstruction.I32Store16Si) = dispatchInstruction { vstack, context -> I32Store16Executor(vstack, context, instruction) }

fun I32Store16Dispatcher(instruction: MemorySuperInstruction.I32Store16Ss) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    val valueSlot = instruction.valueSlot
    memoryStoreNoOffsetDispatcher(instruction.memory, Short.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { vstack, data, address ->
        I32ToI16Writer(data, address, vstack.getFrameSlot(valueSlot).toInt())
    }
} else {
    dispatchInstruction { vstack, context -> I32Store16Executor(vstack, context, instruction) }
}

fun I64Store8Dispatcher(instruction: MemorySuperInstruction.I64Store8Ii) = dispatchInstruction { vstack, context -> I64Store8Executor(vstack, context, instruction) }

fun I64Store8Dispatcher(instruction: MemorySuperInstruction.I64Store8Is) = dispatchInstruction { vstack, context -> I64Store8Executor(vstack, context, instruction) }

fun I64Store8Dispatcher(instruction: MemorySuperInstruction.I64Store8Si) = dispatchInstruction { vstack, context -> I64Store8Executor(vstack, context, instruction) }

fun I64Store8Dispatcher(instruction: MemorySuperInstruction.I64Store8Ss) = dispatchInstruction { vstack, context -> I64Store8Executor(vstack, context, instruction) }

fun I64Store16Dispatcher(instruction: MemorySuperInstruction.I64Store16Ii) = dispatchInstruction { vstack, context -> I64Store16Executor(vstack, context, instruction) }

fun I64Store16Dispatcher(instruction: MemorySuperInstruction.I64Store16Is) = dispatchInstruction { vstack, context -> I64Store16Executor(vstack, context, instruction) }

fun I64Store16Dispatcher(instruction: MemorySuperInstruction.I64Store16Si) = dispatchInstruction { vstack, context -> I64Store16Executor(vstack, context, instruction) }

fun I64Store16Dispatcher(instruction: MemorySuperInstruction.I64Store16Ss) = dispatchInstruction { vstack, context -> I64Store16Executor(vstack, context, instruction) }

fun I64Store32Dispatcher(instruction: MemorySuperInstruction.I64Store32Ii) = dispatchInstruction { vstack, context -> I64Store32Executor(vstack, context, instruction) }

fun I64Store32Dispatcher(instruction: MemorySuperInstruction.I64Store32Is) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    val value = instruction.value
    memoryStoreNoOffsetDispatcher(instruction.memory, Int.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { _, data, address ->
        I64ToI32Writer(data, address, value)
    }
} else {
    dispatchInstruction { vstack, context -> I64Store32Executor(vstack, context, instruction) }
}

fun I64Store32Dispatcher(instruction: MemorySuperInstruction.I64Store32Si) = dispatchInstruction { vstack, context -> I64Store32Executor(vstack, context, instruction) }

fun I64Store32Dispatcher(instruction: MemorySuperInstruction.I64Store32Ss) = dispatchInstruction { vstack, context -> I64Store32Executor(vstack, context, instruction) }
