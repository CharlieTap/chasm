package io.github.charlietap.chasm.compiler.backend.chasm.link

import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.compiler.backend.chasm.ext.toRuntime
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopyIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopyIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopyIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopyIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopySiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopySisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopySsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayCopySssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillSiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillSisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillSsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayFillSssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetSignedIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetSignedSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetUnsignedIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayGetUnsignedSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayLenSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.aggregate.ArrayNewIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.DataDropDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32StoreIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32StoreIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32StoreSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F32StoreSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F64StoreIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F64StoreIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F64StoreSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.F64StoreSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store16IiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store16IsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store16SiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store16SsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store8IiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store8IsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store8SiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32Store8SsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32StoreIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32StoreIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32StoreSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I32StoreSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load16SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load16SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load16UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load16USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load32SIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load32SSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load32UIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Load32USDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store16IiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store16IsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store16SiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store16SsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store32IiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store32IsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store32SiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store32SsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store8IiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store8IsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store8SiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64Store8SsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64StoreIiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64StoreIsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64StoreSiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.I64StoreSsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopyIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopyIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopyIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopyIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopySiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopySisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopySsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryCopySssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillSiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillSisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillSsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryFillSssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryGrowIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryGrowSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitIiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitIisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitIsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitIssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitSiiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitSisDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitSsiDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemoryInitSssDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memory.MemorySizeSDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction

internal fun linkInstructions10(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    480 -> {
        val i = instruction as MemoryInstruction.I64Load16SI
        I64Load16SIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    481 -> {
        val i = instruction as MemoryInstruction.I64Load16SS
        I64Load16SSDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    482 -> {
        val i = instruction as MemoryInstruction.I64Load16UI
        I64Load16UIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    483 -> {
        val i = instruction as MemoryInstruction.I64Load16US
        I64Load16USDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    484 -> {
        val i = instruction as MemoryInstruction.I64Load32SI
        I64Load32SIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    485 -> {
        val i = instruction as MemoryInstruction.I64Load32SS
        I64Load32SSDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    486 -> {
        val i = instruction as MemoryInstruction.I64Load32UI
        I64Load32UIDispatcher(i.address, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    487 -> {
        val i = instruction as MemoryInstruction.I64Load32US
        I64Load32USDispatcher(i.addressSlot, i.destinationSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    488 -> {
        val i = instruction as MemoryInstruction.MemorySizeS
        MemorySizeSDispatcher(i.destinationSlot, context.memories[i.memory.index])
    }
    489 -> {
        val i = instruction as MemoryInstruction.MemoryGrowI
        MemoryGrowIDispatcher(i.pagesToAdd, i.destinationSlot, context.memories[i.memory.index])
    }
    490 -> {
        val i = instruction as MemoryInstruction.MemoryGrowS
        MemoryGrowSDispatcher(i.pagesToAddSlot, i.destinationSlot, context.memories[i.memory.index])
    }
    491 -> {
        val i = instruction as MemoryInstruction.MemoryInitIii
        MemoryInitIiiDispatcher(i.bytesToCopy, i.sourceOffset, i.destinationOffset, context.memories[i.memory.index], context.data[i.data.index])
    }
    492 -> {
        val i = instruction as MemoryInstruction.MemoryInitIis
        MemoryInitIisDispatcher(i.bytesToCopy, i.sourceOffset, i.destinationOffsetSlot, context.memories[i.memory.index], context.data[i.data.index])
    }
    493 -> {
        val i = instruction as MemoryInstruction.MemoryInitIsi
        MemoryInitIsiDispatcher(i.bytesToCopy, i.sourceOffsetSlot, i.destinationOffset, context.memories[i.memory.index], context.data[i.data.index])
    }
    494 -> {
        val i = instruction as MemoryInstruction.MemoryInitIss
        MemoryInitIssDispatcher(i.bytesToCopy, i.sourceOffsetSlot, i.destinationOffsetSlot, context.memories[i.memory.index], context.data[i.data.index])
    }
    495 -> {
        val i = instruction as MemoryInstruction.MemoryInitSii
        MemoryInitSiiDispatcher(i.bytesToCopySlot, i.sourceOffset, i.destinationOffset, context.memories[i.memory.index], context.data[i.data.index])
    }
    496 -> {
        val i = instruction as MemoryInstruction.MemoryInitSis
        MemoryInitSisDispatcher(i.bytesToCopySlot, i.sourceOffset, i.destinationOffsetSlot, context.memories[i.memory.index], context.data[i.data.index])
    }
    497 -> {
        val i = instruction as MemoryInstruction.MemoryInitSsi
        MemoryInitSsiDispatcher(i.bytesToCopySlot, i.sourceOffsetSlot, i.destinationOffset, context.memories[i.memory.index], context.data[i.data.index])
    }
    498 -> {
        val i = instruction as MemoryInstruction.MemoryInitSss
        MemoryInitSssDispatcher(i.bytesToCopySlot, i.sourceOffsetSlot, i.destinationOffsetSlot, context.memories[i.memory.index], context.data[i.data.index])
    }
    499 -> {
        val i = instruction as MemoryInstruction.MemoryCopyIii
        MemoryCopyIiiDispatcher(i.bytesToCopy, i.sourceOffset, i.destinationOffset, context.memories[i.srcMemory.index], context.memories[i.dstMemory.index])
    }
    500 -> {
        val i = instruction as MemoryInstruction.MemoryCopyIis
        MemoryCopyIisDispatcher(i.bytesToCopy, i.sourceOffset, i.destinationOffsetSlot, context.memories[i.srcMemory.index], context.memories[i.dstMemory.index])
    }
    501 -> {
        val i = instruction as MemoryInstruction.MemoryCopyIsi
        MemoryCopyIsiDispatcher(i.bytesToCopy, i.sourceOffsetSlot, i.destinationOffset, context.memories[i.srcMemory.index], context.memories[i.dstMemory.index])
    }
    502 -> {
        val i = instruction as MemoryInstruction.MemoryCopyIss
        MemoryCopyIssDispatcher(i.bytesToCopy, i.sourceOffsetSlot, i.destinationOffsetSlot, context.memories[i.srcMemory.index], context.memories[i.dstMemory.index])
    }
    503 -> {
        val i = instruction as MemoryInstruction.MemoryCopySii
        MemoryCopySiiDispatcher(i.bytesToCopySlot, i.sourceOffset, i.destinationOffset, context.memories[i.srcMemory.index], context.memories[i.dstMemory.index])
    }
    504 -> {
        val i = instruction as MemoryInstruction.MemoryCopySis
        MemoryCopySisDispatcher(i.bytesToCopySlot, i.sourceOffset, i.destinationOffsetSlot, context.memories[i.srcMemory.index], context.memories[i.dstMemory.index])
    }
    505 -> {
        val i = instruction as MemoryInstruction.MemoryCopySsi
        MemoryCopySsiDispatcher(i.bytesToCopySlot, i.sourceOffsetSlot, i.destinationOffset, context.memories[i.srcMemory.index], context.memories[i.dstMemory.index])
    }
    506 -> {
        val i = instruction as MemoryInstruction.MemoryCopySss
        MemoryCopySssDispatcher(i.bytesToCopySlot, i.sourceOffsetSlot, i.destinationOffsetSlot, context.memories[i.srcMemory.index], context.memories[i.dstMemory.index])
    }
    507 -> {
        val i = instruction as MemoryInstruction.MemoryFillIii
        MemoryFillIiiDispatcher(i.bytesToFill, i.fillValue, i.offset, context.memories[i.memory.index])
    }
    508 -> {
        val i = instruction as MemoryInstruction.MemoryFillIis
        MemoryFillIisDispatcher(i.bytesToFill, i.fillValue, i.offsetSlot, context.memories[i.memory.index])
    }
    509 -> {
        val i = instruction as MemoryInstruction.MemoryFillIsi
        MemoryFillIsiDispatcher(i.bytesToFill, i.fillValueSlot, i.offset, context.memories[i.memory.index])
    }
    510 -> {
        val i = instruction as MemoryInstruction.MemoryFillIss
        MemoryFillIssDispatcher(i.bytesToFill, i.fillValueSlot, i.offsetSlot, context.memories[i.memory.index])
    }
    511 -> {
        val i = instruction as MemoryInstruction.MemoryFillSii
        MemoryFillSiiDispatcher(i.bytesToFillSlot, i.fillValue, i.offset, context.memories[i.memory.index])
    }
    512 -> {
        val i = instruction as MemoryInstruction.MemoryFillSis
        MemoryFillSisDispatcher(i.bytesToFillSlot, i.fillValue, i.offsetSlot, context.memories[i.memory.index])
    }
    513 -> {
        val i = instruction as MemoryInstruction.MemoryFillSsi
        MemoryFillSsiDispatcher(i.bytesToFillSlot, i.fillValueSlot, i.offset, context.memories[i.memory.index])
    }
    514 -> {
        val i = instruction as MemoryInstruction.MemoryFillSss
        MemoryFillSssDispatcher(i.bytesToFillSlot, i.fillValueSlot, i.offsetSlot, context.memories[i.memory.index])
    }
    515 -> {
        val i = instruction as MemoryInstruction.I32StoreIi
        I32StoreIiDispatcher(i.value, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    516 -> {
        val i = instruction as MemoryInstruction.I32StoreIs
        I32StoreIsDispatcher(i.value, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    517 -> {
        val i = instruction as MemoryInstruction.I32StoreSi
        I32StoreSiDispatcher(i.valueSlot, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    518 -> {
        val i = instruction as MemoryInstruction.I32StoreSs
        I32StoreSsDispatcher(i.valueSlot, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    519 -> {
        val i = instruction as MemoryInstruction.I64StoreIi
        I64StoreIiDispatcher(i.value, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    520 -> {
        val i = instruction as MemoryInstruction.I64StoreIs
        I64StoreIsDispatcher(i.value, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    521 -> {
        val i = instruction as MemoryInstruction.I64StoreSi
        I64StoreSiDispatcher(i.valueSlot, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    522 -> {
        val i = instruction as MemoryInstruction.I64StoreSs
        I64StoreSsDispatcher(i.valueSlot, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    523 -> {
        val i = instruction as MemoryInstruction.F32StoreIi
        F32StoreIiDispatcher(i.value, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    524 -> {
        val i = instruction as MemoryInstruction.F32StoreIs
        F32StoreIsDispatcher(i.value, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    525 -> {
        val i = instruction as MemoryInstruction.F32StoreSi
        F32StoreSiDispatcher(i.valueSlot, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    526 -> {
        val i = instruction as MemoryInstruction.F32StoreSs
        F32StoreSsDispatcher(i.valueSlot, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    527 -> {
        val i = instruction as MemoryInstruction.F64StoreIi
        F64StoreIiDispatcher(i.value, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    else -> error("opcode $opcode is not linked here")
}

internal fun linkInstructions11(
    instruction: Instruction,
    opcode: Int,
    context: LinkContext,
): DispatchableInstruction = when (opcode) {
    528 -> {
        val i = instruction as MemoryInstruction.F64StoreIs
        F64StoreIsDispatcher(i.value, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    529 -> {
        val i = instruction as MemoryInstruction.F64StoreSi
        F64StoreSiDispatcher(i.valueSlot, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    530 -> {
        val i = instruction as MemoryInstruction.F64StoreSs
        F64StoreSsDispatcher(i.valueSlot, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    531 -> {
        val i = instruction as MemoryInstruction.I32Store8Ii
        I32Store8IiDispatcher(i.value, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    532 -> {
        val i = instruction as MemoryInstruction.I32Store8Is
        I32Store8IsDispatcher(i.value, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    533 -> {
        val i = instruction as MemoryInstruction.I32Store8Si
        I32Store8SiDispatcher(i.valueSlot, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    534 -> {
        val i = instruction as MemoryInstruction.I32Store8Ss
        I32Store8SsDispatcher(i.valueSlot, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    535 -> {
        val i = instruction as MemoryInstruction.I32Store16Ii
        I32Store16IiDispatcher(i.value, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    536 -> {
        val i = instruction as MemoryInstruction.I32Store16Is
        I32Store16IsDispatcher(i.value, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    537 -> {
        val i = instruction as MemoryInstruction.I32Store16Si
        I32Store16SiDispatcher(i.valueSlot, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    538 -> {
        val i = instruction as MemoryInstruction.I32Store16Ss
        I32Store16SsDispatcher(i.valueSlot, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    539 -> {
        val i = instruction as MemoryInstruction.I64Store8Ii
        I64Store8IiDispatcher(i.value, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    540 -> {
        val i = instruction as MemoryInstruction.I64Store8Is
        I64Store8IsDispatcher(i.value, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    541 -> {
        val i = instruction as MemoryInstruction.I64Store8Si
        I64Store8SiDispatcher(i.valueSlot, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    542 -> {
        val i = instruction as MemoryInstruction.I64Store8Ss
        I64Store8SsDispatcher(i.valueSlot, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    543 -> {
        val i = instruction as MemoryInstruction.I64Store16Ii
        I64Store16IiDispatcher(i.value, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    544 -> {
        val i = instruction as MemoryInstruction.I64Store16Is
        I64Store16IsDispatcher(i.value, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    545 -> {
        val i = instruction as MemoryInstruction.I64Store16Si
        I64Store16SiDispatcher(i.valueSlot, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    546 -> {
        val i = instruction as MemoryInstruction.I64Store16Ss
        I64Store16SsDispatcher(i.valueSlot, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    547 -> {
        val i = instruction as MemoryInstruction.I64Store32Ii
        I64Store32IiDispatcher(i.value, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    548 -> {
        val i = instruction as MemoryInstruction.I64Store32Is
        I64Store32IsDispatcher(i.value, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    549 -> {
        val i = instruction as MemoryInstruction.I64Store32Si
        I64Store32SiDispatcher(i.valueSlot, i.address, context.memories[i.memory.index], i.memArg.offset)
    }
    550 -> {
        val i = instruction as MemoryInstruction.I64Store32Ss
        I64Store32SsDispatcher(i.valueSlot, i.addressSlot, context.memories[i.memory.index], i.memArg.offset)
    }
    551 -> {
        val i = instruction as MemoryInstruction.DataDrop
        DataDropDispatcher(context.data[i.data.index])
    }
    552 -> {
        val i = instruction as AggregateInstruction.ArrayCopyIii
        ArrayCopyIiiDispatcher(i.elementsToCopy, i.sourceOffset, i.destinationOffset, i.sourceAddressSlot, i.destinationAddressSlot)
    }
    553 -> {
        val i = instruction as AggregateInstruction.ArrayCopyIis
        ArrayCopyIisDispatcher(i.elementsToCopy, i.sourceOffset, i.destinationOffsetSlot, i.sourceAddressSlot, i.destinationAddressSlot)
    }
    554 -> {
        val i = instruction as AggregateInstruction.ArrayCopyIsi
        ArrayCopyIsiDispatcher(i.elementsToCopy, i.sourceOffsetSlot, i.destinationOffset, i.sourceAddressSlot, i.destinationAddressSlot)
    }
    555 -> {
        val i = instruction as AggregateInstruction.ArrayCopyIss
        ArrayCopyIssDispatcher(i.elementsToCopy, i.sourceOffsetSlot, i.destinationOffsetSlot, i.sourceAddressSlot, i.destinationAddressSlot)
    }
    556 -> {
        val i = instruction as AggregateInstruction.ArrayCopySii
        ArrayCopySiiDispatcher(i.elementsToCopySlot, i.sourceOffset, i.destinationOffset, i.sourceAddressSlot, i.destinationAddressSlot)
    }
    557 -> {
        val i = instruction as AggregateInstruction.ArrayCopySis
        ArrayCopySisDispatcher(i.elementsToCopySlot, i.sourceOffset, i.destinationOffsetSlot, i.sourceAddressSlot, i.destinationAddressSlot)
    }
    558 -> {
        val i = instruction as AggregateInstruction.ArrayCopySsi
        ArrayCopySsiDispatcher(i.elementsToCopySlot, i.sourceOffsetSlot, i.destinationOffset, i.sourceAddressSlot, i.destinationAddressSlot)
    }
    559 -> {
        val i = instruction as AggregateInstruction.ArrayCopySss
        ArrayCopySssDispatcher(i.elementsToCopySlot, i.sourceOffsetSlot, i.destinationOffsetSlot, i.sourceAddressSlot, i.destinationAddressSlot)
    }
    560 -> {
        val i = instruction as AggregateInstruction.ArrayFillIii
        ArrayFillIiiDispatcher(i.elementsToFill, i.fillValue, i.arrayElementOffset, i.addressSlot)
    }
    561 -> {
        val i = instruction as AggregateInstruction.ArrayFillIis
        ArrayFillIisDispatcher(i.elementsToFill, i.fillValue, i.arrayElementOffsetSlot, i.addressSlot)
    }
    562 -> {
        val i = instruction as AggregateInstruction.ArrayFillIsi
        ArrayFillIsiDispatcher(i.elementsToFill, i.fillValueSlot, i.arrayElementOffset, i.addressSlot)
    }
    563 -> {
        val i = instruction as AggregateInstruction.ArrayFillIss
        ArrayFillIssDispatcher(i.elementsToFill, i.fillValueSlot, i.arrayElementOffsetSlot, i.addressSlot)
    }
    564 -> {
        val i = instruction as AggregateInstruction.ArrayFillSii
        ArrayFillSiiDispatcher(i.elementsToFillSlot, i.fillValue, i.arrayElementOffset, i.addressSlot)
    }
    565 -> {
        val i = instruction as AggregateInstruction.ArrayFillSis
        ArrayFillSisDispatcher(i.elementsToFillSlot, i.fillValue, i.arrayElementOffsetSlot, i.addressSlot)
    }
    566 -> {
        val i = instruction as AggregateInstruction.ArrayFillSsi
        ArrayFillSsiDispatcher(i.elementsToFillSlot, i.fillValueSlot, i.arrayElementOffset, i.addressSlot)
    }
    567 -> {
        val i = instruction as AggregateInstruction.ArrayFillSss
        ArrayFillSssDispatcher(i.elementsToFillSlot, i.fillValueSlot, i.arrayElementOffsetSlot, i.addressSlot)
    }
    568 -> {
        val i = instruction as AggregateInstruction.ArrayGetI
        ArrayGetIDispatcher(i.addressSlot, i.field, i.destinationSlot)
    }
    569 -> {
        val i = instruction as AggregateInstruction.ArrayGetS
        ArrayGetSDispatcher(i.addressSlot, i.fieldSlot, i.destinationSlot)
    }
    570 -> {
        val i = instruction as AggregateInstruction.ArrayGetSignedI
        ArrayGetSignedIDispatcher(i.addressSlot, i.field, i.destinationSlot, i.packedType.toRuntime())
    }
    571 -> {
        val i = instruction as AggregateInstruction.ArrayGetSignedS
        ArrayGetSignedSDispatcher(i.addressSlot, i.fieldSlot, i.destinationSlot, i.packedType.toRuntime())
    }
    572 -> {
        val i = instruction as AggregateInstruction.ArrayGetUnsignedI
        ArrayGetUnsignedIDispatcher(i.addressSlot, i.field, i.destinationSlot, i.packedType.toRuntime())
    }
    573 -> {
        val i = instruction as AggregateInstruction.ArrayGetUnsignedS
        ArrayGetUnsignedSDispatcher(i.addressSlot, i.fieldSlot, i.destinationSlot, i.packedType.toRuntime())
    }
    574 -> {
        val i = instruction as AggregateInstruction.ArrayLenS
        ArrayLenSDispatcher(i.addressSlot, i.destinationSlot)
    }
    575 -> {
        val i = instruction as AggregateInstruction.ArrayNewIi
        ArrayNewIiDispatcher(i.size, i.value, i.destinationSlot, context.runtimeTypes[i.type.index])
    }
    else -> error("opcode $opcode is not linked here")
}
