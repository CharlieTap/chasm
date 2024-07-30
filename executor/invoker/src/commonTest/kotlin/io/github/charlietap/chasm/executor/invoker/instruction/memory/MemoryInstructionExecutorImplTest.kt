package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.F64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedSignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedUnsignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64LoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedSignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedUnsignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.F64StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreSizedExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.fixture.instruction.i64Load32SInstruction
import io.github.charlietap.chasm.fixture.instruction.i64Load32UInstruction
import io.github.charlietap.chasm.fixture.instruction.i64Store32Instruction
import io.github.charlietap.chasm.fixture.instruction.memArg
import io.github.charlietap.chasm.fixture.module.memoryIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class MemoryInstructionExecutorImplTest {

    @Test
    fun `i64 load signed 32 delegates with 4 bytes to i64 signed load executor`() {

        val store = store()
        val stack = stack()

        val memoryIndex = memoryIndex(117u)
        val memArg = memArg(117u, 118u)
        val instruction = i64Load32SInstruction(
            memoryIndex,
            memArg,
        )

        val i64SizedSignedLoadExecutor: I64SizedSignedLoadExecutor = { _store, _stack, _memIndex, _memarg, _bytes ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(memoryIndex, _memIndex)
            assertEquals(memArg, _memarg)
            assertEquals(4, _bytes)

            Ok(Unit)
        }

        val actual = createMemoryInstructionExecutor(
            instruction = instruction,
            stack = stack,
            store = store,
            i64SizedSignedLoadExecutor = i64SizedSignedLoadExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `i64 load unsigned 32 delegates with 4 bytes to i64 unsigned load executor`() {

        val store = store()
        val stack = stack()

        val memoryIndex = memoryIndex(117u)
        val memArg = memArg(117u, 118u)
        val instruction = i64Load32UInstruction(
            memoryIndex,
            memArg,
        )

        val i64SizedUnsignedLoadExecutor: I64SizedUnsignedLoadExecutor = { _store, _stack, _memIndex, _memarg, _bytes ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(memoryIndex, _memIndex)
            assertEquals(memArg, _memarg)
            assertEquals(4, _bytes)

            Ok(Unit)
        }

        val actual = createMemoryInstructionExecutor(
            instruction = instruction,
            stack = stack,
            store = store,
            i64SizedUnsignedLoadExecutor = i64SizedUnsignedLoadExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `i64 store 32 delegates with 4 bytes to i64 sized store executor`() {

        val store = store()
        val stack = stack()

        val memoryIndex = memoryIndex(117u)
        val memArg = memArg(117u, 118u)
        val instruction = i64Store32Instruction(
            memoryIndex,
            memArg,
        )

        val i64StoreSizedExecutor: I64StoreSizedExecutor = { _store, _stack, _memIdx, _memarg, _bytes ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(memoryIndex, _memIdx)
            assertEquals(memArg, _memarg)
            assertEquals(4, _bytes)

            Ok(Unit)
        }

        val actual = createMemoryInstructionExecutor(
            instruction = instruction,
            stack = stack,
            store = store,
            i64StoreSizedExecutor = i64StoreSizedExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun memoryInitExecutor(): MemoryInitExecutor = { _, _, _ ->
            fail("MemoryInitExecutor should not be called in this scenario")
        }

        fun memoryGrowExecutor(): MemoryGrowExecutor = { _, _, _ ->
            fail("MemoryGrowExecutor should not be called in this scenario")
        }

        fun memorySizeExecutor(): MemorySizeExecutor = { _, _, _ ->
            fail("MemorySizeExecutor should not be called in this scenario")
        }

        fun memoryFillExecutor(): MemoryFillExecutor = { _, _, _ ->
            fail("MemoryFillExecutor should not be called in this scenario")
        }

        fun memoryCopyExecutor(): MemoryCopyExecutor = { _, _, _ ->
            fail("MemoryCopyExecutor should not be called in this scenario")
        }

        fun dataDropExecutor(): DataDropExecutor = { _, _, _ ->
            fail("DataDropExecutor should not be called in this scenario")
        }

        fun i32LoadExecutor(): I32LoadExecutor = { _, _, _ ->
            fail("I32LoadExecutor should not be called in this scenario")
        }

        fun i32SizedSignedLoadExecutor(): I32SizedSignedLoadExecutor = { _, _, _, _, _ ->
            fail("I32SizedSignedLoadExecutor should not be called in this scenario")
        }

        fun i32SizedUnsignedLoadExecutor(): I32SizedUnsignedLoadExecutor = { _, _, _, _, _ ->
            fail("I32SizedUnsignedLoadExecutor should not be called in this scenario")
        }

        fun i64LoadExecutor(): I64LoadExecutor = { _, _, _ ->
            fail("I64LoadExecutor should not be called in this scenario")
        }

        fun i64SizedSignedLoadExecutor(): I64SizedSignedLoadExecutor = { _, _, _, _, _ ->
            fail("I64SizedSignedLoadExecutor should not be called in this scenario")
        }

        fun i64SizedUnsignedLoadExecutor(): I64SizedUnsignedLoadExecutor = { _, _, _, _, _ ->
            fail("I64SizedUnsignedLoadExecutor should not be called in this scenario")
        }

        fun f32LoadExecutor(): F32LoadExecutor = { _, _, _ ->
            fail("F32LoadExecutor should not be called in this scenario")
        }

        fun f64LoadExecutor(): F64LoadExecutor = { _, _, _ ->
            fail("F64LoadExecutor should not be called in this scenario")
        }

        fun i32StoreExecutor(): I32StoreExecutor = { _, _, _ ->
            fail("I32StoreExecutor should not be called in this scenario")
        }

        fun i32StoreSizedExecutor(): I32StoreSizedExecutor = { _, _, _, _, _ ->
            fail("I32StoreSizedExecutor should not be called in this scenario")
        }

        fun i64StoreExecutor(): I64StoreExecutor = { _, _, _ ->
            fail("I64StoreExecutor should not be called in this scenario")
        }

        fun i64StoreSizedExecutor(): I64StoreSizedExecutor = { _, _, _, _, _ ->
            fail("I64StoreSizedExecutor should not be called in this scenario")
        }

        fun f32StoreExecutor(): F32StoreExecutor = { _, _, _ ->
            fail("F32StoreExecutor should not be called in this scenario")
        }

        fun f64StoreExecutor(): F64StoreExecutor = { _, _, _ ->
            fail("F64StoreExecutor should not be called in this scenario")
        }

        fun createMemoryInstructionExecutor(
            instruction: MemoryInstruction,
            stack: Stack,
            store: Store,
            memoryInitExecutor: MemoryInitExecutor = memoryInitExecutor(),
            memoryGrowExecutor: MemoryGrowExecutor = memoryGrowExecutor(),
            memorySizeExecutor: MemorySizeExecutor = memorySizeExecutor(),
            memoryFillExecutor: MemoryFillExecutor = memoryFillExecutor(),
            memoryCopyExecutor: MemoryCopyExecutor = memoryCopyExecutor(),
            dataDropExecutor: DataDropExecutor = dataDropExecutor(),
            i32LoadExecutor: I32LoadExecutor = i32LoadExecutor(),
            i32SizedSignedLoadExecutor: I32SizedSignedLoadExecutor = i32SizedSignedLoadExecutor(),
            i32SizedUnsignedLoadExecutor: I32SizedUnsignedLoadExecutor = i32SizedUnsignedLoadExecutor(),
            i64LoadExecutor: I64LoadExecutor = i64LoadExecutor(),
            i64SizedSignedLoadExecutor: I64SizedSignedLoadExecutor = i64SizedSignedLoadExecutor(),
            i64SizedUnsignedLoadExecutor: I64SizedUnsignedLoadExecutor = i64SizedUnsignedLoadExecutor(),
            f32LoadExecutor: F32LoadExecutor = f32LoadExecutor(),
            f64LoadExecutor: F64LoadExecutor = f64LoadExecutor(),
            i32StoreExecutor: I32StoreExecutor = i32StoreExecutor(),
            i32StoreSizedExecutor: I32StoreSizedExecutor = i32StoreSizedExecutor(),
            i64StoreExecutor: I64StoreExecutor = i64StoreExecutor(),
            i64StoreSizedExecutor: I64StoreSizedExecutor = i64StoreSizedExecutor(),
            f32StoreExecutor: F32StoreExecutor = f32StoreExecutor(),
            f64StoreExecutor: F64StoreExecutor = f64StoreExecutor(),
        ): Result<Unit, InvocationError> {
            return MemoryInstructionExecutorImpl(
                instruction = instruction,
                stack = stack,
                store = store,
                memoryInitExecutor = memoryInitExecutor,
                memoryGrowExecutor = memoryGrowExecutor,
                memorySizeExecutor = memorySizeExecutor,
                memoryFillExecutor = memoryFillExecutor,
                memoryCopyExecutor = memoryCopyExecutor,
                dataDropExecutor = dataDropExecutor,
                i32LoadExecutor = i32LoadExecutor,
                i32SizedSignedLoadExecutor = i32SizedSignedLoadExecutor,
                i32SizedUnsignedLoadExecutor = i32SizedUnsignedLoadExecutor,
                i64LoadExecutor = i64LoadExecutor,
                i64SizedSignedLoadExecutor = i64SizedSignedLoadExecutor,
                i64SizedUnsignedLoadExecutor = i64SizedUnsignedLoadExecutor,
                f32LoadExecutor = f32LoadExecutor,
                f64LoadExecutor = f64LoadExecutor,
                i32StoreExecutor = i32StoreExecutor,
                i32StoreSizedExecutor = i32StoreSizedExecutor,
                i64StoreExecutor = i64StoreExecutor,
                i64StoreSizedExecutor = i64StoreSizedExecutor,
                f32StoreExecutor = f32StoreExecutor,
                f64StoreExecutor = f64StoreExecutor,
            )
        }
    }
}
