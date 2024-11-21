package io.github.charlietap.chasm.decoder.decoder.instruction.atomic

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.decoder.context.DecoderContext
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.memory.MemArgWithIndex
import io.github.charlietap.chasm.decoder.decoder.instruction.memory.MemArgWithIndexDecoder
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError

internal fun AtomicMemoryInstructionDecoder(
    context: DecoderContext,
): Result<AtomicMemoryInstruction, WasmDecodeError> =
    AtomicMemoryInstructionDecoder(
        context = context,
        memArgWithIndexDecoder = ::MemArgWithIndexDecoder,
    )

internal inline fun AtomicMemoryInstructionDecoder(
    context: DecoderContext,
    crossinline memArgWithIndexDecoder: Decoder<MemArgWithIndex>,
): Result<AtomicMemoryInstruction, WasmDecodeError> = binding {
    when (val opcode = context.reader.uint().bind()) {
        ATOMIC_NOTIFY -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Notify(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_WAIT -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.I32Wait(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_WAIT -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.I64Wait(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        ATOMIC_FENCE -> AtomicMemoryInstruction.Fence
        I32_ATOMIC_LOAD -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Load.I32.I32Load(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_LOAD -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Load.I64.I64Load(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_LOAD8_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Load.I32.I32Load8U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_LOAD8_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Load.I64.I64Load8U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_LOAD16_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Load.I32.I32Load16U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_LOAD16_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Load.I64.I64Load16U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_LOAD32_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Load.I64.I64Load32U(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_STORE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Store.I32.I32Store(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_STORE -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Store.I64.I64Store(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_STORE8 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Store.I32.I32Store8(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_STORE8 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Store.I64.I64Store8(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_STORE16 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Store.I32.I32Store16(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_STORE16 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Store.I64.I64Store16(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_STORE32 -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.Store.I64.I64Store32(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW_ADD -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAdd(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW_ADD -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAdd(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW_SUB -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteSub(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW_SUB -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteSub(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW_AND -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteAnd(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW_AND -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteAnd(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW_OR -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteOr(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW_OR -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteOr(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW_XOR -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteXor(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW_XOR -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteXor(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW_XCHG -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWriteExchange(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW_XCHG -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWriteExchange(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW8_ADD_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Add(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW16_ADD_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Add(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW8_ADD_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Add(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW16_ADD_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Add(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW32_ADD_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Add(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW8_SUB_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Sub(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW16_SUB_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Sub(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW8_SUB_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Sub(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW16_SUB_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Sub(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW32_SUB_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Sub(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW8_AND_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8And(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW16_AND_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16And(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW8_AND_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8And(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW16_AND_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16And(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW32_AND_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32And(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW8_OR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Or(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW16_OR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Or(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW8_OR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Or(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW16_OR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Or(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW32_OR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Or(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW8_XOR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Xor(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW16_XOR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Xor(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW8_XOR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Xor(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW16_XOR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Xor(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW32_XOR_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Xor(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW8_XCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite8Exchange(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW16_XCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I32.I32ReadModifyWrite16Exchange(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW8_XCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite8Exchange(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW16_XCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite16Exchange(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW32_XCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.ReadModifyWrite.I64.I64ReadModifyWrite32Exchange(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW_CMPXCHG -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW_CMPXCHG -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW8_CMPXCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange8(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I32_ATOMIC_RMW16_CMPXCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.CompareExchange.I32.I32CompareExchange16(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW8_CMPXCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange8(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW16_CMPXCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange16(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        I64_ATOMIC_RMW32_CMPXCHG_U -> {
            val memArgWithIndex = memArgWithIndexDecoder(context).bind()
            AtomicMemoryInstruction.CompareExchange.I64.I64CompareExchange32(memArgWithIndex.memoryIndex, memArgWithIndex.memArg)
        }
        else -> Err(InstructionDecodeError.InvalidAtomicMemoryInstruction(opcode)).bind<AtomicMemoryInstruction>()
    }
}

internal const val ATOMIC_NOTIFY: UInt = 0u
internal const val I32_ATOMIC_WAIT: UInt = 1u
internal const val I64_ATOMIC_WAIT: UInt = 2u
internal const val ATOMIC_FENCE: UInt = 3u

internal const val I32_ATOMIC_LOAD: UInt = 16u
internal const val I64_ATOMIC_LOAD: UInt = 17u
internal const val I32_ATOMIC_LOAD8_U: UInt = 18u
internal const val I32_ATOMIC_LOAD16_U: UInt = 19u
internal const val I64_ATOMIC_LOAD8_U: UInt = 20u
internal const val I64_ATOMIC_LOAD16_U: UInt = 21u
internal const val I64_ATOMIC_LOAD32_U: UInt = 22u

internal const val I32_ATOMIC_STORE: UInt = 23u
internal const val I64_ATOMIC_STORE: UInt = 24u
internal const val I32_ATOMIC_STORE8: UInt = 25u
internal const val I32_ATOMIC_STORE16: UInt = 26u
internal const val I64_ATOMIC_STORE8: UInt = 27u
internal const val I64_ATOMIC_STORE16: UInt = 28u
internal const val I64_ATOMIC_STORE32: UInt = 29u

internal const val I32_ATOMIC_RMW_ADD: UInt = 30u
internal const val I64_ATOMIC_RMW_ADD: UInt = 31u
internal const val I32_ATOMIC_RMW8_ADD_U: UInt = 32u
internal const val I32_ATOMIC_RMW16_ADD_U: UInt = 33u
internal const val I64_ATOMIC_RMW8_ADD_U: UInt = 34u
internal const val I64_ATOMIC_RMW16_ADD_U: UInt = 35u
internal const val I64_ATOMIC_RMW32_ADD_U: UInt = 36u

internal const val I32_ATOMIC_RMW_SUB: UInt = 37u
internal const val I64_ATOMIC_RMW_SUB: UInt = 38u
internal const val I32_ATOMIC_RMW8_SUB_U: UInt = 39u
internal const val I32_ATOMIC_RMW16_SUB_U: UInt = 40u
internal const val I64_ATOMIC_RMW8_SUB_U: UInt = 41u
internal const val I64_ATOMIC_RMW16_SUB_U: UInt = 42u
internal const val I64_ATOMIC_RMW32_SUB_U: UInt = 43u

internal const val I32_ATOMIC_RMW_AND: UInt = 44u
internal const val I64_ATOMIC_RMW_AND: UInt = 45u
internal const val I32_ATOMIC_RMW8_AND_U: UInt = 46u
internal const val I32_ATOMIC_RMW16_AND_U: UInt = 47u
internal const val I64_ATOMIC_RMW8_AND_U: UInt = 48u
internal const val I64_ATOMIC_RMW16_AND_U: UInt = 49u
internal const val I64_ATOMIC_RMW32_AND_U: UInt = 50u

internal const val I32_ATOMIC_RMW_OR: UInt = 51u
internal const val I64_ATOMIC_RMW_OR: UInt = 52u
internal const val I32_ATOMIC_RMW8_OR_U: UInt = 53u
internal const val I32_ATOMIC_RMW16_OR_U: UInt = 54u
internal const val I64_ATOMIC_RMW8_OR_U: UInt = 55u
internal const val I64_ATOMIC_RMW16_OR_U: UInt = 56u
internal const val I64_ATOMIC_RMW32_OR_U: UInt = 57u

internal const val I32_ATOMIC_RMW_XOR: UInt = 58u
internal const val I64_ATOMIC_RMW_XOR: UInt = 59u
internal const val I32_ATOMIC_RMW8_XOR_U: UInt = 60u
internal const val I32_ATOMIC_RMW16_XOR_U: UInt = 61u
internal const val I64_ATOMIC_RMW8_XOR_U: UInt = 62u
internal const val I64_ATOMIC_RMW16_XOR_U: UInt = 63u
internal const val I64_ATOMIC_RMW32_XOR_U: UInt = 64u

internal const val I32_ATOMIC_RMW_XCHG: UInt = 65u
internal const val I64_ATOMIC_RMW_XCHG: UInt = 66u
internal const val I32_ATOMIC_RMW8_XCHG_U: UInt = 67u
internal const val I32_ATOMIC_RMW16_XCHG_U: UInt = 68u
internal const val I64_ATOMIC_RMW8_XCHG_U: UInt = 69u
internal const val I64_ATOMIC_RMW16_XCHG_U: UInt = 70u
internal const val I64_ATOMIC_RMW32_XCHG_U: UInt = 71u

internal const val I32_ATOMIC_RMW_CMPXCHG: UInt = 72u
internal const val I64_ATOMIC_RMW_CMPXCHG: UInt = 73u
internal const val I32_ATOMIC_RMW8_CMPXCHG_U: UInt = 74u
internal const val I32_ATOMIC_RMW16_CMPXCHG_U: UInt = 75u
internal const val I64_ATOMIC_RMW8_CMPXCHG_U: UInt = 76u
internal const val I64_ATOMIC_RMW16_CMPXCHG_U: UInt = 77u
internal const val I64_ATOMIC_RMW32_CMPXCHG_U: UInt = 78u
