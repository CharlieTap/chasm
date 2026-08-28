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
import io.github.charlietap.chasm.memory.read.I3216SReader
import io.github.charlietap.chasm.memory.read.I3216UReader
import io.github.charlietap.chasm.memory.read.I328UReader
import io.github.charlietap.chasm.memory.read.I32Reader
import io.github.charlietap.chasm.memory.read.I64Reader
import io.github.charlietap.chasm.runtime.instruction.MemorySuperInstruction

fun I32LoadDispatcher(instruction: MemorySuperInstruction.I32LoadI) = if (instruction.memArg.offset == 0) {
    val address = instruction.address
    memoryLoadNoOffsetDispatcher(instruction.memory, instruction.destinationSlot, Int.SIZE_BYTES, { address }) { data, effectiveAddress ->
        I32Reader(data, effectiveAddress).toLong()
    }
} else {
    dispatchInstruction { vstack, context -> I32LoadExecutor(vstack, context, instruction) }
}

fun I32LoadDispatcher(instruction: MemorySuperInstruction.I32LoadS) = if (instruction.memArg.offset == 0) {
    val memory = instruction.memory
    val addressSlot = instruction.addressSlot
    memoryLoadNoOffsetDispatcher(memory, instruction.destinationSlot, Int.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { data, address ->
        I32Reader(data, address).toLong()
    }
} else {
    dispatchInstruction { vstack, context -> I32LoadExecutor(vstack, context, instruction) }
}

fun I64LoadDispatcher(instruction: MemorySuperInstruction.I64LoadI) = if (instruction.memArg.offset == 0) {
    val address = instruction.address
    memoryLoadNoOffsetDispatcher(instruction.memory, instruction.destinationSlot, Long.SIZE_BYTES, { address }) { data, effectiveAddress ->
        I64Reader(data, effectiveAddress)
    }
} else {
    dispatchInstruction { vstack, context -> I64LoadExecutor(vstack, context, instruction) }
}

fun I64LoadDispatcher(instruction: MemorySuperInstruction.I64LoadS) = dispatchInstruction { vstack, context -> I64LoadExecutor(vstack, context, instruction) }

fun F32LoadDispatcher(instruction: MemorySuperInstruction.F32LoadI) = dispatchInstruction { vstack, context -> F32LoadExecutor(vstack, context, instruction) }

fun F32LoadDispatcher(instruction: MemorySuperInstruction.F32LoadS) = dispatchInstruction { vstack, context -> F32LoadExecutor(vstack, context, instruction) }

fun F64LoadDispatcher(instruction: MemorySuperInstruction.F64LoadI) = dispatchInstruction { vstack, context -> F64LoadExecutor(vstack, context, instruction) }

fun F64LoadDispatcher(instruction: MemorySuperInstruction.F64LoadS) = dispatchInstruction { vstack, context -> F64LoadExecutor(vstack, context, instruction) }

fun I32Load8SDispatcher(instruction: MemorySuperInstruction.I32Load8SI) = dispatchInstruction { vstack, context -> I32Load8SExecutor(vstack, context, instruction) }

fun I32Load8SDispatcher(instruction: MemorySuperInstruction.I32Load8SS) = dispatchInstruction { vstack, context -> I32Load8SExecutor(vstack, context, instruction) }

fun I32Load8UDispatcher(instruction: MemorySuperInstruction.I32Load8UI) = dispatchInstruction { vstack, context -> I32Load8UExecutor(vstack, context, instruction) }

fun I32Load8UDispatcher(instruction: MemorySuperInstruction.I32Load8US) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    memoryLoadNoOffsetDispatcher(instruction.memory, instruction.destinationSlot, Byte.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { data, address ->
        I328UReader(data, address).toLong()
    }
} else {
    dispatchInstruction { vstack, context -> I32Load8UExecutor(vstack, context, instruction) }
}

fun I32Load16SDispatcher(instruction: MemorySuperInstruction.I32Load16SI) = dispatchInstruction { vstack, context -> I32Load16SExecutor(vstack, context, instruction) }

fun I32Load16SDispatcher(instruction: MemorySuperInstruction.I32Load16SS) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    memoryLoadNoOffsetDispatcher(instruction.memory, instruction.destinationSlot, Short.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { data, address ->
        I3216SReader(data, address).toLong()
    }
} else {
    dispatchInstruction { vstack, context -> I32Load16SExecutor(vstack, context, instruction) }
}

fun I32Load16UDispatcher(instruction: MemorySuperInstruction.I32Load16UI) = dispatchInstruction { vstack, context -> I32Load16UExecutor(vstack, context, instruction) }

fun I32Load16UDispatcher(instruction: MemorySuperInstruction.I32Load16US) = if (instruction.memArg.offset == 0) {
    val addressSlot = instruction.addressSlot
    memoryLoadNoOffsetDispatcher(instruction.memory, instruction.destinationSlot, Short.SIZE_BYTES, { vstack ->
        vstack.getFrameSlot(addressSlot).toInt()
    }) { data, address ->
        I3216UReader(data, address).toLong()
    }
} else {
    dispatchInstruction { vstack, context -> I32Load16UExecutor(vstack, context, instruction) }
}

fun I64Load8SDispatcher(instruction: MemorySuperInstruction.I64Load8SI) = dispatchInstruction { vstack, context -> I64Load8SExecutor(vstack, context, instruction) }

fun I64Load8SDispatcher(instruction: MemorySuperInstruction.I64Load8SS) = dispatchInstruction { vstack, context -> I64Load8SExecutor(vstack, context, instruction) }

fun I64Load8UDispatcher(instruction: MemorySuperInstruction.I64Load8UI) = dispatchInstruction { vstack, context -> I64Load8UExecutor(vstack, context, instruction) }

fun I64Load8UDispatcher(instruction: MemorySuperInstruction.I64Load8US) = dispatchInstruction { vstack, context -> I64Load8UExecutor(vstack, context, instruction) }

fun I64Load16SDispatcher(instruction: MemorySuperInstruction.I64Load16SI) = dispatchInstruction { vstack, context -> I64Load16SExecutor(vstack, context, instruction) }

fun I64Load16SDispatcher(instruction: MemorySuperInstruction.I64Load16SS) = dispatchInstruction { vstack, context -> I64Load16SExecutor(vstack, context, instruction) }

fun I64Load16UDispatcher(instruction: MemorySuperInstruction.I64Load16UI) = dispatchInstruction { vstack, context -> I64Load16UExecutor(vstack, context, instruction) }

fun I64Load16UDispatcher(instruction: MemorySuperInstruction.I64Load16US) = dispatchInstruction { vstack, context -> I64Load16UExecutor(vstack, context, instruction) }

fun I64Load32SDispatcher(instruction: MemorySuperInstruction.I64Load32SI) = dispatchInstruction { vstack, context -> I64Load32SExecutor(vstack, context, instruction) }

fun I64Load32SDispatcher(instruction: MemorySuperInstruction.I64Load32SS) = dispatchInstruction { vstack, context -> I64Load32SExecutor(vstack, context, instruction) }

fun I64Load32UDispatcher(instruction: MemorySuperInstruction.I64Load32UI) = dispatchInstruction { vstack, context -> I64Load32UExecutor(vstack, context, instruction) }

fun I64Load32UDispatcher(instruction: MemorySuperInstruction.I64Load32US) = dispatchInstruction { vstack, context -> I64Load32UExecutor(vstack, context, instruction) }

fun MemorySizeDispatcher(instruction: MemorySuperInstruction.MemorySizeS) = dispatchInstruction { vstack, context -> MemorySizeExecutor(vstack, context, instruction) }
