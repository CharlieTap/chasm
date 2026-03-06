package io.github.charlietap.chasm.predecoder.instruction.memoryfused

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.F32LoadDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.F32StoreDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.F64LoadDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.F64StoreDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32Load16SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32Load16UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32Load8SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32Load8UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32LoadDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32Store16Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32Store8Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I32StoreDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64Load16SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64Load16UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64Load32SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64Load32UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64Load8SDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64Load8UDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64LoadDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64Store16Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64Store32Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64Store8Dispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.I64StoreDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.MemoryCopyDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.MemoryFillDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.MemoryGrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.MemoryInitDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.memoryfused.MemorySizeDispatcher
import io.github.charlietap.chasm.ir.instruction.FusedDestination
import io.github.charlietap.chasm.ir.instruction.FusedMemoryInstruction
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.predecoder.PredecodingContext
import io.github.charlietap.chasm.predecoder.ext.dataAddress
import io.github.charlietap.chasm.predecoder.ext.memoryAddress
import io.github.charlietap.chasm.predecoder.instruction.MemArgPredecoder
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.ir.instruction.MemArg as IrMemArg
import io.github.charlietap.chasm.runtime.instruction.FusedMemoryInstruction as RuntimeFusedMemoryInstruction
import io.github.charlietap.chasm.runtime.instruction.MemArg as RuntimeMemArg

internal fun FusedMemoryInstructionPredecoder(
    context: PredecodingContext,
    instruction: FusedMemoryInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    when (instruction) {
        is FusedMemoryInstruction.I32Load -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I32LoadDispatcher(RuntimeFusedMemoryInstruction.I32LoadI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I32LoadDispatcher(RuntimeFusedMemoryInstruction.I32LoadS(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Load -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I64LoadDispatcher(RuntimeFusedMemoryInstruction.I64LoadI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I64LoadDispatcher(RuntimeFusedMemoryInstruction.I64LoadS(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.F32Load -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                F32LoadDispatcher(RuntimeFusedMemoryInstruction.F32LoadI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                F32LoadDispatcher(RuntimeFusedMemoryInstruction.F32LoadS(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.F64Load -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                F64LoadDispatcher(RuntimeFusedMemoryInstruction.F64LoadI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                F64LoadDispatcher(RuntimeFusedMemoryInstruction.F64LoadS(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I32Load8S -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I32Load8SDispatcher(RuntimeFusedMemoryInstruction.I32Load8SI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I32Load8SDispatcher(RuntimeFusedMemoryInstruction.I32Load8SS(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I32Load8U -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I32Load8UDispatcher(RuntimeFusedMemoryInstruction.I32Load8UI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I32Load8UDispatcher(RuntimeFusedMemoryInstruction.I32Load8US(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I32Load16S -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I32Load16SDispatcher(RuntimeFusedMemoryInstruction.I32Load16SI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I32Load16SDispatcher(RuntimeFusedMemoryInstruction.I32Load16SS(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I32Load16U -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I32Load16UDispatcher(RuntimeFusedMemoryInstruction.I32Load16UI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I32Load16UDispatcher(RuntimeFusedMemoryInstruction.I32Load16US(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Load8S -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I64Load8SDispatcher(RuntimeFusedMemoryInstruction.I64Load8SI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I64Load8SDispatcher(RuntimeFusedMemoryInstruction.I64Load8SS(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Load8U -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I64Load8UDispatcher(RuntimeFusedMemoryInstruction.I64Load8UI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I64Load8UDispatcher(RuntimeFusedMemoryInstruction.I64Load8US(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Load16S -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I64Load16SDispatcher(RuntimeFusedMemoryInstruction.I64Load16SI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I64Load16SDispatcher(RuntimeFusedMemoryInstruction.I64Load16SS(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Load16U -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I64Load16UDispatcher(RuntimeFusedMemoryInstruction.I64Load16UI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I64Load16UDispatcher(RuntimeFusedMemoryInstruction.I64Load16US(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Load32S -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I64Load32SDispatcher(RuntimeFusedMemoryInstruction.I64Load32SI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I64Load32SDispatcher(RuntimeFusedMemoryInstruction.I64Load32SS(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Load32U -> strictMemoryLoad(
            context = context,
            addressOperand = instruction.addressOperand,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            i = { address, destinationSlot, memory, runtimeMemArg ->
                I64Load32UDispatcher(RuntimeFusedMemoryInstruction.I64Load32UI(address, destinationSlot, memory, runtimeMemArg))
            },
            s = { addressSlot, destinationSlot, memory, runtimeMemArg ->
                I64Load32UDispatcher(RuntimeFusedMemoryInstruction.I64Load32US(addressSlot, destinationSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.MemorySize -> strictMemorySizeInstruction(
            context = context,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
        ).bind()
        is FusedMemoryInstruction.MemoryGrow -> strictMemoryGrowInstruction(
            context = context,
            pagesToAdd = instruction.pagesToAdd,
            destination = instruction.destination,
            memoryIndex = instruction.memoryIndex,
        ).bind()
        is FusedMemoryInstruction.MemoryInit -> strictMemoryInitInstruction(
            context = context,
            bytesToCopy = instruction.bytesToCopy,
            sourceOffset = instruction.sourceOffset,
            destinationOffset = instruction.destinationOffset,
            memoryIndex = instruction.memoryIndex,
            dataIndex = instruction.dataIndex,
        ).bind()
        is FusedMemoryInstruction.MemoryCopy -> strictMemoryCopyInstruction(
            context = context,
            bytesToCopy = instruction.bytesToCopy,
            sourceOffset = instruction.sourceOffset,
            destinationOffset = instruction.destinationOffset,
            srcIndex = instruction.srcIndex,
            dstIndex = instruction.dstIndex,
        ).bind()
        is FusedMemoryInstruction.MemoryFill -> strictMemoryFillInstruction(
            context = context,
            bytesToFill = instruction.bytesToFill,
            fillValue = instruction.fillValue,
            offset = instruction.offset,
            memoryIndex = instruction.memoryIndex,
        ).bind()
        is FusedMemoryInstruction.I32Store -> strictI32MemoryStore(
            context = context,
            valueOperand = instruction.valueOperand,
            addressOperand = instruction.addressOperand,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            ii = { value, address, memory, runtimeMemArg ->
                I32StoreDispatcher(RuntimeFusedMemoryInstruction.I32StoreIi(value, address, memory, runtimeMemArg))
            },
            `is` = { value, addressSlot, memory, runtimeMemArg ->
                I32StoreDispatcher(RuntimeFusedMemoryInstruction.I32StoreIs(value, addressSlot, memory, runtimeMemArg))
            },
            si = { valueSlot, address, memory, runtimeMemArg ->
                I32StoreDispatcher(RuntimeFusedMemoryInstruction.I32StoreSi(valueSlot, address, memory, runtimeMemArg))
            },
            ss = { valueSlot, addressSlot, memory, runtimeMemArg ->
                I32StoreDispatcher(RuntimeFusedMemoryInstruction.I32StoreSs(valueSlot, addressSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Store -> strictI64MemoryStore(
            context = context,
            valueOperand = instruction.valueOperand,
            addressOperand = instruction.addressOperand,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            ii = { value, address, memory, runtimeMemArg ->
                I64StoreDispatcher(RuntimeFusedMemoryInstruction.I64StoreIi(value, address, memory, runtimeMemArg))
            },
            `is` = { value, addressSlot, memory, runtimeMemArg ->
                I64StoreDispatcher(RuntimeFusedMemoryInstruction.I64StoreIs(value, addressSlot, memory, runtimeMemArg))
            },
            si = { valueSlot, address, memory, runtimeMemArg ->
                I64StoreDispatcher(RuntimeFusedMemoryInstruction.I64StoreSi(valueSlot, address, memory, runtimeMemArg))
            },
            ss = { valueSlot, addressSlot, memory, runtimeMemArg ->
                I64StoreDispatcher(RuntimeFusedMemoryInstruction.I64StoreSs(valueSlot, addressSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.F32Store -> strictF32MemoryStore(
            context = context,
            valueOperand = instruction.valueOperand,
            addressOperand = instruction.addressOperand,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            ii = { value, address, memory, runtimeMemArg ->
                F32StoreDispatcher(RuntimeFusedMemoryInstruction.F32StoreIi(value, address, memory, runtimeMemArg))
            },
            `is` = { value, addressSlot, memory, runtimeMemArg ->
                F32StoreDispatcher(RuntimeFusedMemoryInstruction.F32StoreIs(value, addressSlot, memory, runtimeMemArg))
            },
            si = { valueSlot, address, memory, runtimeMemArg ->
                F32StoreDispatcher(RuntimeFusedMemoryInstruction.F32StoreSi(valueSlot, address, memory, runtimeMemArg))
            },
            ss = { valueSlot, addressSlot, memory, runtimeMemArg ->
                F32StoreDispatcher(RuntimeFusedMemoryInstruction.F32StoreSs(valueSlot, addressSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.F64Store -> strictF64MemoryStore(
            context = context,
            valueOperand = instruction.valueOperand,
            addressOperand = instruction.addressOperand,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            ii = { value, address, memory, runtimeMemArg ->
                F64StoreDispatcher(RuntimeFusedMemoryInstruction.F64StoreIi(value, address, memory, runtimeMemArg))
            },
            `is` = { value, addressSlot, memory, runtimeMemArg ->
                F64StoreDispatcher(RuntimeFusedMemoryInstruction.F64StoreIs(value, addressSlot, memory, runtimeMemArg))
            },
            si = { valueSlot, address, memory, runtimeMemArg ->
                F64StoreDispatcher(RuntimeFusedMemoryInstruction.F64StoreSi(valueSlot, address, memory, runtimeMemArg))
            },
            ss = { valueSlot, addressSlot, memory, runtimeMemArg ->
                F64StoreDispatcher(RuntimeFusedMemoryInstruction.F64StoreSs(valueSlot, addressSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I32Store8 -> strictI32MemoryStore(
            context = context,
            valueOperand = instruction.valueOperand,
            addressOperand = instruction.addressOperand,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            ii = { value, address, memory, runtimeMemArg ->
                I32Store8Dispatcher(RuntimeFusedMemoryInstruction.I32Store8Ii(value, address, memory, runtimeMemArg))
            },
            `is` = { value, addressSlot, memory, runtimeMemArg ->
                I32Store8Dispatcher(RuntimeFusedMemoryInstruction.I32Store8Is(value, addressSlot, memory, runtimeMemArg))
            },
            si = { valueSlot, address, memory, runtimeMemArg ->
                I32Store8Dispatcher(RuntimeFusedMemoryInstruction.I32Store8Si(valueSlot, address, memory, runtimeMemArg))
            },
            ss = { valueSlot, addressSlot, memory, runtimeMemArg ->
                I32Store8Dispatcher(RuntimeFusedMemoryInstruction.I32Store8Ss(valueSlot, addressSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I32Store16 -> strictI32MemoryStore(
            context = context,
            valueOperand = instruction.valueOperand,
            addressOperand = instruction.addressOperand,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            ii = { value, address, memory, runtimeMemArg ->
                I32Store16Dispatcher(RuntimeFusedMemoryInstruction.I32Store16Ii(value, address, memory, runtimeMemArg))
            },
            `is` = { value, addressSlot, memory, runtimeMemArg ->
                I32Store16Dispatcher(RuntimeFusedMemoryInstruction.I32Store16Is(value, addressSlot, memory, runtimeMemArg))
            },
            si = { valueSlot, address, memory, runtimeMemArg ->
                I32Store16Dispatcher(RuntimeFusedMemoryInstruction.I32Store16Si(valueSlot, address, memory, runtimeMemArg))
            },
            ss = { valueSlot, addressSlot, memory, runtimeMemArg ->
                I32Store16Dispatcher(RuntimeFusedMemoryInstruction.I32Store16Ss(valueSlot, addressSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Store8 -> strictI64MemoryStore(
            context = context,
            valueOperand = instruction.valueOperand,
            addressOperand = instruction.addressOperand,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            ii = { value, address, memory, runtimeMemArg ->
                I64Store8Dispatcher(RuntimeFusedMemoryInstruction.I64Store8Ii(value, address, memory, runtimeMemArg))
            },
            `is` = { value, addressSlot, memory, runtimeMemArg ->
                I64Store8Dispatcher(RuntimeFusedMemoryInstruction.I64Store8Is(value, addressSlot, memory, runtimeMemArg))
            },
            si = { valueSlot, address, memory, runtimeMemArg ->
                I64Store8Dispatcher(RuntimeFusedMemoryInstruction.I64Store8Si(valueSlot, address, memory, runtimeMemArg))
            },
            ss = { valueSlot, addressSlot, memory, runtimeMemArg ->
                I64Store8Dispatcher(RuntimeFusedMemoryInstruction.I64Store8Ss(valueSlot, addressSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Store16 -> strictI64MemoryStore(
            context = context,
            valueOperand = instruction.valueOperand,
            addressOperand = instruction.addressOperand,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            ii = { value, address, memory, runtimeMemArg ->
                I64Store16Dispatcher(RuntimeFusedMemoryInstruction.I64Store16Ii(value, address, memory, runtimeMemArg))
            },
            `is` = { value, addressSlot, memory, runtimeMemArg ->
                I64Store16Dispatcher(RuntimeFusedMemoryInstruction.I64Store16Is(value, addressSlot, memory, runtimeMemArg))
            },
            si = { valueSlot, address, memory, runtimeMemArg ->
                I64Store16Dispatcher(RuntimeFusedMemoryInstruction.I64Store16Si(valueSlot, address, memory, runtimeMemArg))
            },
            ss = { valueSlot, addressSlot, memory, runtimeMemArg ->
                I64Store16Dispatcher(RuntimeFusedMemoryInstruction.I64Store16Ss(valueSlot, addressSlot, memory, runtimeMemArg))
            },
        ).bind()
        is FusedMemoryInstruction.I64Store32 -> strictI64MemoryStore(
            context = context,
            valueOperand = instruction.valueOperand,
            addressOperand = instruction.addressOperand,
            memoryIndex = instruction.memoryIndex,
            memArg = instruction.memArg,
            ii = { value, address, memory, runtimeMemArg ->
                I64Store32Dispatcher(RuntimeFusedMemoryInstruction.I64Store32Ii(value, address, memory, runtimeMemArg))
            },
            `is` = { value, addressSlot, memory, runtimeMemArg ->
                I64Store32Dispatcher(RuntimeFusedMemoryInstruction.I64Store32Is(value, addressSlot, memory, runtimeMemArg))
            },
            si = { valueSlot, address, memory, runtimeMemArg ->
                I64Store32Dispatcher(RuntimeFusedMemoryInstruction.I64Store32Si(valueSlot, address, memory, runtimeMemArg))
            },
            ss = { valueSlot, addressSlot, memory, runtimeMemArg ->
                I64Store32Dispatcher(RuntimeFusedMemoryInstruction.I64Store32Ss(valueSlot, addressSlot, memory, runtimeMemArg))
            },
        ).bind()
    }
}

private fun strictMemorySizeInstruction(
    context: PredecodingContext,
    destination: FusedDestination,
    memoryIndex: Index.MemoryIndex,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memory = resolveMemory(context, memoryIndex).bind()
    val destinationSlot =
        strictMemoryDestinationSlot(destination)
            ?: error("memory.size destination must lower to frame slot before predecode: destination=$destination")

    MemorySizeDispatcher(RuntimeFusedMemoryInstruction.MemorySizeS(destinationSlot, memory))
}

private fun strictMemoryGrowInstruction(
    context: PredecodingContext,
    pagesToAdd: FusedOperand,
    destination: FusedDestination,
    memoryIndex: Index.MemoryIndex,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memory = resolveMemory(context, memoryIndex).bind()
    val destinationSlot =
        strictMemoryDestinationSlot(destination)
            ?: error("memory.grow destination must lower to frame slot before predecode: destination=$destination pagesToAdd=$pagesToAdd")
    val pagesToAddSlot = strictMemoryOperandSlot(pagesToAdd)
    val max = memory.type.limits.max?.toInt() ?: LinearMemory.MAX_PAGES

    when {
        pagesToAdd is FusedOperand.I32Const -> {
            MemoryGrowDispatcher(RuntimeFusedMemoryInstruction.MemoryGrowI(pagesToAdd.const, destinationSlot, memory, max))
        }
        pagesToAddSlot != null -> {
            MemoryGrowDispatcher(RuntimeFusedMemoryInstruction.MemoryGrowS(pagesToAddSlot, destinationSlot, memory, max))
        }
        else -> error("memory.grow operand must lower to i32 immediate/frame-slot before predecode: pagesToAdd=$pagesToAdd destination=$destination")
    }
}

private fun strictMemoryInitInstruction(
    context: PredecodingContext,
    bytesToCopy: FusedOperand,
    sourceOffset: FusedOperand,
    destinationOffset: FusedOperand,
    memoryIndex: Index.MemoryIndex,
    dataIndex: Index.DataIndex,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memory = resolveMemory(context, memoryIndex).bind()
    val dataAddress = context.instance.dataAddress(dataIndex).bind()
    val data = context.store.data(dataAddress)

    strictI32TernaryOperands(
        first = bytesToCopy,
        second = sourceOffset,
        third = destinationOffset,
        instructionName = "memory.init",
        iii = { bytes, source, destination ->
            MemoryInitDispatcher(RuntimeFusedMemoryInstruction.MemoryInitIii(bytes, source, destination, memory, data))
        },
        iis = { bytes, source, destinationSlot ->
            MemoryInitDispatcher(RuntimeFusedMemoryInstruction.MemoryInitIis(bytes, source, destinationSlot, memory, data))
        },
        isi = { bytes, sourceSlot, destination ->
            MemoryInitDispatcher(RuntimeFusedMemoryInstruction.MemoryInitIsi(bytes, sourceSlot, destination, memory, data))
        },
        iss = { bytes, sourceSlot, destinationSlot ->
            MemoryInitDispatcher(RuntimeFusedMemoryInstruction.MemoryInitIss(bytes, sourceSlot, destinationSlot, memory, data))
        },
        sii = { bytesSlot, source, destination ->
            MemoryInitDispatcher(RuntimeFusedMemoryInstruction.MemoryInitSii(bytesSlot, source, destination, memory, data))
        },
        sis = { bytesSlot, source, destinationSlot ->
            MemoryInitDispatcher(RuntimeFusedMemoryInstruction.MemoryInitSis(bytesSlot, source, destinationSlot, memory, data))
        },
        ssi = { bytesSlot, sourceSlot, destination ->
            MemoryInitDispatcher(RuntimeFusedMemoryInstruction.MemoryInitSsi(bytesSlot, sourceSlot, destination, memory, data))
        },
        sss = { bytesSlot, sourceSlot, destinationSlot ->
            MemoryInitDispatcher(RuntimeFusedMemoryInstruction.MemoryInitSss(bytesSlot, sourceSlot, destinationSlot, memory, data))
        },
    )
}

private fun strictMemoryCopyInstruction(
    context: PredecodingContext,
    bytesToCopy: FusedOperand,
    sourceOffset: FusedOperand,
    destinationOffset: FusedOperand,
    srcIndex: Index.MemoryIndex,
    dstIndex: Index.MemoryIndex,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val srcMemory = resolveMemory(context, srcIndex).bind()
    val dstMemory = resolveMemory(context, dstIndex).bind()

    strictI32TernaryOperands(
        first = bytesToCopy,
        second = sourceOffset,
        third = destinationOffset,
        instructionName = "memory.copy",
        iii = { bytes, source, destination ->
            MemoryCopyDispatcher(RuntimeFusedMemoryInstruction.MemoryCopyIii(bytes, source, destination, srcMemory, dstMemory))
        },
        iis = { bytes, source, destinationSlot ->
            MemoryCopyDispatcher(RuntimeFusedMemoryInstruction.MemoryCopyIis(bytes, source, destinationSlot, srcMemory, dstMemory))
        },
        isi = { bytes, sourceSlot, destination ->
            MemoryCopyDispatcher(RuntimeFusedMemoryInstruction.MemoryCopyIsi(bytes, sourceSlot, destination, srcMemory, dstMemory))
        },
        iss = { bytes, sourceSlot, destinationSlot ->
            MemoryCopyDispatcher(RuntimeFusedMemoryInstruction.MemoryCopyIss(bytes, sourceSlot, destinationSlot, srcMemory, dstMemory))
        },
        sii = { bytesSlot, source, destination ->
            MemoryCopyDispatcher(RuntimeFusedMemoryInstruction.MemoryCopySii(bytesSlot, source, destination, srcMemory, dstMemory))
        },
        sis = { bytesSlot, source, destinationSlot ->
            MemoryCopyDispatcher(RuntimeFusedMemoryInstruction.MemoryCopySis(bytesSlot, source, destinationSlot, srcMemory, dstMemory))
        },
        ssi = { bytesSlot, sourceSlot, destination ->
            MemoryCopyDispatcher(RuntimeFusedMemoryInstruction.MemoryCopySsi(bytesSlot, sourceSlot, destination, srcMemory, dstMemory))
        },
        sss = { bytesSlot, sourceSlot, destinationSlot ->
            MemoryCopyDispatcher(RuntimeFusedMemoryInstruction.MemoryCopySss(bytesSlot, sourceSlot, destinationSlot, srcMemory, dstMemory))
        },
    )
}

private fun strictMemoryFillInstruction(
    context: PredecodingContext,
    bytesToFill: FusedOperand,
    fillValue: FusedOperand,
    offset: FusedOperand,
    memoryIndex: Index.MemoryIndex,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val memory = resolveMemory(context, memoryIndex).bind()

    strictI32TernaryOperands(
        first = bytesToFill,
        second = fillValue,
        third = offset,
        instructionName = "memory.fill",
        iii = { bytes, fill, destination ->
            MemoryFillDispatcher(RuntimeFusedMemoryInstruction.MemoryFillIii(bytes, fill, destination, memory))
        },
        iis = { bytes, fill, destinationSlot ->
            MemoryFillDispatcher(RuntimeFusedMemoryInstruction.MemoryFillIis(bytes, fill, destinationSlot, memory))
        },
        isi = { bytes, fillSlot, destination ->
            MemoryFillDispatcher(RuntimeFusedMemoryInstruction.MemoryFillIsi(bytes, fillSlot, destination, memory))
        },
        iss = { bytes, fillSlot, destinationSlot ->
            MemoryFillDispatcher(RuntimeFusedMemoryInstruction.MemoryFillIss(bytes, fillSlot, destinationSlot, memory))
        },
        sii = { bytesSlot, fill, destination ->
            MemoryFillDispatcher(RuntimeFusedMemoryInstruction.MemoryFillSii(bytesSlot, fill, destination, memory))
        },
        sis = { bytesSlot, fill, destinationSlot ->
            MemoryFillDispatcher(RuntimeFusedMemoryInstruction.MemoryFillSis(bytesSlot, fill, destinationSlot, memory))
        },
        ssi = { bytesSlot, fillSlot, destination ->
            MemoryFillDispatcher(RuntimeFusedMemoryInstruction.MemoryFillSsi(bytesSlot, fillSlot, destination, memory))
        },
        sss = { bytesSlot, fillSlot, destinationSlot ->
            MemoryFillDispatcher(RuntimeFusedMemoryInstruction.MemoryFillSss(bytesSlot, fillSlot, destinationSlot, memory))
        },
    )
}

private inline fun strictMemoryLoad(
    context: PredecodingContext,
    addressOperand: FusedOperand,
    destination: FusedDestination,
    memoryIndex: Index.MemoryIndex,
    memArg: IrMemArg,
    crossinline i: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline s: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val resolved = resolveMemoryInstruction(context, memoryIndex, memArg).bind()
    val destinationSlot =
        strictMemoryDestinationSlot(destination)
            ?: error("memory load destination must lower to frame slot before predecode: destination=$destination addressOperand=$addressOperand")
    val addressSlot = strictMemoryOperandSlot(addressOperand)

    when {
        addressOperand is FusedOperand.I32Const -> i(addressOperand.const, destinationSlot, resolved.memory, resolved.memArg)
        addressSlot != null -> s(addressSlot, destinationSlot, resolved.memory, resolved.memArg)
        else -> error("memory load operands must lower to i32 immediate/frame slot before predecode: addressOperand=$addressOperand destination=$destination")
    }
}

private inline fun strictI32MemoryStore(
    context: PredecodingContext,
    valueOperand: FusedOperand,
    addressOperand: FusedOperand,
    memoryIndex: Index.MemoryIndex,
    memArg: IrMemArg,
    crossinline ii: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline `is`: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline si: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline ss: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val resolved = resolveMemoryInstruction(context, memoryIndex, memArg).bind()
    val valueSlot = strictMemoryOperandSlot(valueOperand)
    val addressSlot = strictMemoryOperandSlot(addressOperand)

    when {
        valueOperand is FusedOperand.I32Const && addressOperand is FusedOperand.I32Const -> ii(valueOperand.const, addressOperand.const, resolved.memory, resolved.memArg)
        valueOperand is FusedOperand.I32Const && addressSlot != null -> `is`(valueOperand.const, addressSlot, resolved.memory, resolved.memArg)
        valueSlot != null && addressOperand is FusedOperand.I32Const -> si(valueSlot, addressOperand.const, resolved.memory, resolved.memArg)
        valueSlot != null && addressSlot != null -> ss(valueSlot, addressSlot, resolved.memory, resolved.memArg)
        else -> error("i32 memory store operands must lower to immediate/frame-slot shapes before predecode: valueOperand=$valueOperand addressOperand=$addressOperand")
    }
}

private inline fun strictI64MemoryStore(
    context: PredecodingContext,
    valueOperand: FusedOperand,
    addressOperand: FusedOperand,
    memoryIndex: Index.MemoryIndex,
    memArg: IrMemArg,
    crossinline ii: (Long, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline `is`: (Long, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline si: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline ss: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val resolved = resolveMemoryInstruction(context, memoryIndex, memArg).bind()
    val valueSlot = strictMemoryOperandSlot(valueOperand)
    val addressSlot = strictMemoryOperandSlot(addressOperand)

    when {
        valueOperand is FusedOperand.I64Const && addressOperand is FusedOperand.I32Const -> ii(valueOperand.const, addressOperand.const, resolved.memory, resolved.memArg)
        valueOperand is FusedOperand.I64Const && addressSlot != null -> `is`(valueOperand.const, addressSlot, resolved.memory, resolved.memArg)
        valueSlot != null && addressOperand is FusedOperand.I32Const -> si(valueSlot, addressOperand.const, resolved.memory, resolved.memArg)
        valueSlot != null && addressSlot != null -> ss(valueSlot, addressSlot, resolved.memory, resolved.memArg)
        else -> error("i64 memory store operands must lower to immediate/frame-slot shapes before predecode: valueOperand=$valueOperand addressOperand=$addressOperand")
    }
}

private inline fun strictF32MemoryStore(
    context: PredecodingContext,
    valueOperand: FusedOperand,
    addressOperand: FusedOperand,
    memoryIndex: Index.MemoryIndex,
    memArg: IrMemArg,
    crossinline ii: (Float, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline `is`: (Float, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline si: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline ss: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val resolved = resolveMemoryInstruction(context, memoryIndex, memArg).bind()
    val valueSlot = strictMemoryOperandSlot(valueOperand)
    val addressSlot = strictMemoryOperandSlot(addressOperand)

    when {
        valueOperand is FusedOperand.F32Const && addressOperand is FusedOperand.I32Const -> ii(valueOperand.const, addressOperand.const, resolved.memory, resolved.memArg)
        valueOperand is FusedOperand.F32Const && addressSlot != null -> `is`(valueOperand.const, addressSlot, resolved.memory, resolved.memArg)
        valueSlot != null && addressOperand is FusedOperand.I32Const -> si(valueSlot, addressOperand.const, resolved.memory, resolved.memArg)
        valueSlot != null && addressSlot != null -> ss(valueSlot, addressSlot, resolved.memory, resolved.memArg)
        else -> error("f32 memory store operands must lower to immediate/frame-slot shapes before predecode: valueOperand=$valueOperand addressOperand=$addressOperand")
    }
}

private inline fun strictF64MemoryStore(
    context: PredecodingContext,
    valueOperand: FusedOperand,
    addressOperand: FusedOperand,
    memoryIndex: Index.MemoryIndex,
    memArg: IrMemArg,
    crossinline ii: (Double, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline `is`: (Double, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline si: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
    crossinline ss: (Int, Int, MemoryInstance, RuntimeMemArg) -> DispatchableInstruction,
): Result<DispatchableInstruction, ModuleTrapError> = binding {
    val resolved = resolveMemoryInstruction(context, memoryIndex, memArg).bind()
    val valueSlot = strictMemoryOperandSlot(valueOperand)
    val addressSlot = strictMemoryOperandSlot(addressOperand)

    when {
        valueOperand is FusedOperand.F64Const && addressOperand is FusedOperand.I32Const -> ii(valueOperand.const, addressOperand.const, resolved.memory, resolved.memArg)
        valueOperand is FusedOperand.F64Const && addressSlot != null -> `is`(valueOperand.const, addressSlot, resolved.memory, resolved.memArg)
        valueSlot != null && addressOperand is FusedOperand.I32Const -> si(valueSlot, addressOperand.const, resolved.memory, resolved.memArg)
        valueSlot != null && addressSlot != null -> ss(valueSlot, addressSlot, resolved.memory, resolved.memArg)
        else -> error("f64 memory store operands must lower to immediate/frame-slot shapes before predecode: valueOperand=$valueOperand addressOperand=$addressOperand")
    }
}

private inline fun <T> strictI32TernaryOperands(
    first: FusedOperand,
    second: FusedOperand,
    third: FusedOperand,
    instructionName: String,
    crossinline iii: (Int, Int, Int) -> T,
    crossinline iis: (Int, Int, Int) -> T,
    crossinline isi: (Int, Int, Int) -> T,
    crossinline iss: (Int, Int, Int) -> T,
    crossinline sii: (Int, Int, Int) -> T,
    crossinline sis: (Int, Int, Int) -> T,
    crossinline ssi: (Int, Int, Int) -> T,
    crossinline sss: (Int, Int, Int) -> T,
): T {
    val firstSlot = strictMemoryOperandSlot(first)
    val secondSlot = strictMemoryOperandSlot(second)
    val thirdSlot = strictMemoryOperandSlot(third)

    return when {
        first is FusedOperand.I32Const && second is FusedOperand.I32Const && third is FusedOperand.I32Const -> iii(first.const, second.const, third.const)
        first is FusedOperand.I32Const && second is FusedOperand.I32Const && thirdSlot != null -> iis(first.const, second.const, thirdSlot)
        first is FusedOperand.I32Const && secondSlot != null && third is FusedOperand.I32Const -> isi(first.const, secondSlot, third.const)
        first is FusedOperand.I32Const && secondSlot != null && thirdSlot != null -> iss(first.const, secondSlot, thirdSlot)
        firstSlot != null && second is FusedOperand.I32Const && third is FusedOperand.I32Const -> sii(firstSlot, second.const, third.const)
        firstSlot != null && second is FusedOperand.I32Const && thirdSlot != null -> sis(firstSlot, second.const, thirdSlot)
        firstSlot != null && secondSlot != null && third is FusedOperand.I32Const -> ssi(firstSlot, secondSlot, third.const)
        firstSlot != null && secondSlot != null && thirdSlot != null -> sss(firstSlot, secondSlot, thirdSlot)
        else -> error("$instructionName operands must lower to i32 immediate/frame-slot shapes before predecode: first=$first second=$second third=$third")
    }
}

private fun strictMemoryOperandSlot(
    operand: FusedOperand,
): Int? = when (operand) {
    is FusedOperand.FrameSlot -> operand.offset
    is FusedOperand.LocalGet -> operand.index.idx
    else -> null
}

private fun strictMemoryDestinationSlot(
    destination: FusedDestination,
): Int? = when (destination) {
    is FusedDestination.FrameSlot -> destination.offset
    is FusedDestination.LocalSet -> destination.index.idx
    else -> null
}

private fun resolveMemoryInstruction(
    context: PredecodingContext,
    memoryIndex: Index.MemoryIndex,
    memArg: IrMemArg,
): Result<ResolvedMemoryInstruction, ModuleTrapError> = binding {
    val memory = resolveMemory(context, memoryIndex).bind()
    val runtimeMemArg = MemArgPredecoder(memArg)
    ResolvedMemoryInstruction(memory, runtimeMemArg)
}

private fun resolveMemory(
    context: PredecodingContext,
    memoryIndex: Index.MemoryIndex,
): Result<MemoryInstance, ModuleTrapError> = binding {
    val memoryAddress = context.instance.memoryAddress(memoryIndex).bind()
    context.store.memory(memoryAddress)
}

private data class ResolvedMemoryInstruction(
    val memory: MemoryInstance,
    val memArg: RuntimeMemArg,
)
