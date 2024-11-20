package io.github.charlietap.chasm.decoder.decoder.instruction.atomic

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.decoder.decoder.Decoder
import io.github.charlietap.chasm.decoder.decoder.instruction.memory.MemArgWithIndex
import io.github.charlietap.chasm.decoder.error.InstructionDecodeError
import io.github.charlietap.chasm.decoder.error.WasmDecodeError
import io.github.charlietap.chasm.decoder.fixture.decoderContext
import io.github.charlietap.chasm.decoder.reader.FakeUIntReader
import io.github.charlietap.chasm.decoder.reader.FakeWasmBinaryReader
import io.github.charlietap.chasm.fixture.instruction.atomicFenceInstruction
import io.github.charlietap.chasm.fixture.instruction.atomicNotifyInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicCompareExchange16Instruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicCompareExchange8Instruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicCompareExchangeInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicLoad16UInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicLoad8UInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicLoadInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite16AddInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite16AndInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite16ExchangeInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite16OrInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite16SubInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite16XorInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite8AddInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite8AndInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite8ExchangeInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite8OrInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite8SubInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWrite8XorInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWriteAddInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWriteAndInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWriteExchangeInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWriteOrInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWriteSubInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicReadModifyWriteXorInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicStore16Instruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicStore8Instruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicStoreInstruction
import io.github.charlietap.chasm.fixture.instruction.i32AtomicWaitInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicCompareExchange16Instruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicCompareExchange32Instruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicCompareExchange8Instruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicCompareExchangeInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicLoad16UInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicLoad32UInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicLoad8UInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicLoadInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite16AddInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite16AndInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite16ExchangeInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite16OrInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite16SubInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite16XorInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite32AddInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite32AndInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite32ExchangeInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite32OrInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite32SubInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite32XorInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite8AddInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite8AndInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite8ExchangeInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite8OrInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite8SubInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWrite8XorInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWriteAddInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWriteAndInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWriteExchangeInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWriteOrInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWriteSubInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicReadModifyWriteXorInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicStore16Instruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicStore32Instruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicStore8Instruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicStoreInstruction
import io.github.charlietap.chasm.fixture.instruction.i64AtomicWaitInstruction
import io.github.charlietap.chasm.fixture.instruction.memArg
import io.github.charlietap.chasm.fixture.module.memoryIndex
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class AtomicMemoryInstructionDecoderTest {

    @Test
    fun `can decode an ATOMIC_FENCE instruction`() {

        val opcode = ATOMIC_FENCE
        val expected = Ok(atomicFenceInstruction())

        val fakeOpcodeReader: () -> Result<UInt, WasmDecodeError> = {
            Ok(opcode)
        }
        val reader = FakeWasmBinaryReader(
            fakeUIntReader = fakeOpcodeReader,
        )
        val context = decoderContext(reader)

        val actual = AtomicMemoryInstructionDecoder(
            context = context,
            memArgWithIndexDecoder = neverMemArgWithIndexDecoder,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can decode memarg atomic memory instructions`() {

        val expectedMemoryIndex = memoryIndex(117u)
        val expectedMemArg = memArg(118u, 119u)

        val opcodeInstructionMap = mapOf(
            ATOMIC_NOTIFY to atomicNotifyInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_WAIT to i32AtomicWaitInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_WAIT to i64AtomicWaitInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_LOAD to i32AtomicLoadInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_LOAD to i64AtomicLoadInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_LOAD8_U to i32AtomicLoad8UInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_LOAD8_U to i64AtomicLoad8UInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_LOAD16_U to i32AtomicLoad16UInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_LOAD16_U to i64AtomicLoad16UInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_LOAD32_U to i64AtomicLoad32UInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_STORE to i32AtomicStoreInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_STORE to i64AtomicStoreInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_STORE8 to i32AtomicStore8Instruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_STORE8 to i64AtomicStore8Instruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_STORE16 to i32AtomicStore16Instruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_STORE16 to i64AtomicStore16Instruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_STORE32 to i64AtomicStore32Instruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW_ADD to i32AtomicReadModifyWriteAddInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW_ADD to i64AtomicReadModifyWriteAddInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW_SUB to i32AtomicReadModifyWriteSubInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW_SUB to i64AtomicReadModifyWriteSubInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW_AND to i32AtomicReadModifyWriteAndInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW_AND to i64AtomicReadModifyWriteAndInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW_OR to i32AtomicReadModifyWriteOrInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW_OR to i64AtomicReadModifyWriteOrInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW_XOR to i32AtomicReadModifyWriteXorInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW_XOR to i64AtomicReadModifyWriteXorInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW_XCHG to i32AtomicReadModifyWriteExchangeInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW_XCHG to i64AtomicReadModifyWriteExchangeInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW8_ADD_U to i32AtomicReadModifyWrite8AddInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW8_SUB_U to i32AtomicReadModifyWrite8SubInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW8_AND_U to i32AtomicReadModifyWrite8AndInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW8_OR_U to i32AtomicReadModifyWrite8OrInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW8_XOR_U to i32AtomicReadModifyWrite8XorInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW8_XCHG_U to i32AtomicReadModifyWrite8ExchangeInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW8_ADD_U to i64AtomicReadModifyWrite8AddInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW8_SUB_U to i64AtomicReadModifyWrite8SubInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW8_AND_U to i64AtomicReadModifyWrite8AndInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW8_OR_U to i64AtomicReadModifyWrite8OrInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW8_XOR_U to i64AtomicReadModifyWrite8XorInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW8_XCHG_U to i64AtomicReadModifyWrite8ExchangeInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW16_ADD_U to i32AtomicReadModifyWrite16AddInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW16_SUB_U to i32AtomicReadModifyWrite16SubInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW16_AND_U to i32AtomicReadModifyWrite16AndInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW16_OR_U to i32AtomicReadModifyWrite16OrInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW16_XOR_U to i32AtomicReadModifyWrite16XorInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW16_XCHG_U to i32AtomicReadModifyWrite16ExchangeInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW16_ADD_U to i64AtomicReadModifyWrite16AddInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW16_SUB_U to i64AtomicReadModifyWrite16SubInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW16_AND_U to i64AtomicReadModifyWrite16AndInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW16_OR_U to i64AtomicReadModifyWrite16OrInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW16_XOR_U to i64AtomicReadModifyWrite16XorInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW16_XCHG_U to i64AtomicReadModifyWrite16ExchangeInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW32_ADD_U to i64AtomicReadModifyWrite32AddInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW32_SUB_U to i64AtomicReadModifyWrite32SubInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW32_AND_U to i64AtomicReadModifyWrite32AndInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW32_OR_U to i64AtomicReadModifyWrite32OrInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW32_XOR_U to i64AtomicReadModifyWrite32XorInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW32_XCHG_U to i64AtomicReadModifyWrite32ExchangeInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW_CMPXCHG to i32AtomicCompareExchangeInstruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW_CMPXCHG to i64AtomicCompareExchangeInstruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW8_CMPXCHG_U to i32AtomicCompareExchange8Instruction(expectedMemoryIndex, expectedMemArg),
            I32_ATOMIC_RMW16_CMPXCHG_U to i32AtomicCompareExchange16Instruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW8_CMPXCHG_U to i64AtomicCompareExchange8Instruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW16_CMPXCHG_U to i64AtomicCompareExchange16Instruction(expectedMemoryIndex, expectedMemArg),
            I64_ATOMIC_RMW32_CMPXCHG_U to i64AtomicCompareExchange32Instruction(expectedMemoryIndex, expectedMemArg),
        )

        val memArgWithIndexDecoder: Decoder<MemArgWithIndex> = {
            Ok(MemArgWithIndex(expectedMemoryIndex, expectedMemArg))
        }

        var opcode = 0u
        val reader = FakeUIntReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        opcodeInstructionMap.map { (_opcode, _instruction) ->
            opcode = _opcode
            val expected = Ok(_instruction)

            val actual = AtomicMemoryInstructionDecoder(
                context = context,
                memArgWithIndexDecoder = memArgWithIndexDecoder,
            )
            assertEquals(expected, actual)
        }
    }

    @Test
    fun `returns an unknown atomic memory instruction error when the opcode doesn't match`() {

        val opcode = 117u
        val reader = FakeUIntReader {
            Ok(opcode)
        }
        val context = decoderContext(reader)

        val expected = Err(InstructionDecodeError.InvalidAtomicMemoryInstruction(opcode))
        val actual = AtomicMemoryInstructionDecoder(context)

        assertEquals(expected, actual)
    }

    private companion object {
        private val neverMemArgWithIndexDecoder: Decoder<MemArgWithIndex> = { _ ->
            fail("memarg with index decoder should not run in this scenario")
        }
    }
}
