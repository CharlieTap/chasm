package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedSignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I32SizedUnsignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedSignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.load.I64SizedUnsignedLoadExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I32StoreSizedExecutor
import io.github.charlietap.chasm.executor.invoker.instruction.memory.store.I64StoreSizedExecutor
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
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

class MemoryInstructionExecutorTest {

    @Test
    fun `i64 load signed 32 delegates with 4 bytes to i64 signed load executor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val memoryIndex = memoryIndex(117u)
        val memArg = memArg(117u, 118u)
        val instruction = i64Load32SInstruction(
            memoryIndex,
            memArg,
        )

        val i64SizedSignedLoadExecutor: I64SizedSignedLoadExecutor = { _context, _memIndex, _memarg, _bytes ->
            assertEquals(context, _context)
            assertEquals(memoryIndex, _memIndex)
            assertEquals(memArg, _memarg)
            assertEquals(4, _bytes)

            Ok(Unit)
        }

        val actual = memoryInstructionExecutor(
            context = context,
            instruction = instruction,
            i64SizedSignedLoadExecutor = i64SizedSignedLoadExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `i64 load unsigned 32 delegates with 4 bytes to i64 unsigned load executor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val memoryIndex = memoryIndex(117u)
        val memArg = memArg(117u, 118u)
        val instruction = i64Load32UInstruction(
            memoryIndex,
            memArg,
        )

        val i64SizedUnsignedLoadExecutor: I64SizedUnsignedLoadExecutor = { _context, _memIndex, _memarg, _bytes ->
            assertEquals(context, _context)
            assertEquals(memoryIndex, _memIndex)
            assertEquals(memArg, _memarg)
            assertEquals(4, _bytes)

            Ok(Unit)
        }

        val actual = memoryInstructionExecutor(
            context = context,
            instruction = instruction,
            i64SizedUnsignedLoadExecutor = i64SizedUnsignedLoadExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    @Test
    fun `i64 store 32 delegates with 4 bytes to i64 sized store executor`() {

        val store = store()
        val stack = stack()
        val context = executionContext(stack, store)

        val memoryIndex = memoryIndex(117u)
        val memArg = memArg(117u, 118u)
        val instruction = i64Store32Instruction(
            memoryIndex,
            memArg,
        )

        val i64StoreSizedExecutor: I64StoreSizedExecutor = { _context, _memIdx, _memarg, _bytes ->
            assertEquals(context, _context)
            assertEquals(memoryIndex, _memIdx)
            assertEquals(memArg, _memarg)
            assertEquals(4, _bytes)

            Ok(Unit)
        }

        val actual = memoryInstructionExecutor(
            context = context,
            instruction = instruction,
            i64StoreSizedExecutor = i64StoreSizedExecutor,
        )

        assertEquals(Ok(Unit), actual)
    }

    companion object {
        fun memoryInitExecutor(): Executor<MemoryInstruction.MemoryInit> = { _, _ ->
            fail("MemoryInitExecutor should not be called in this scenario")
        }

        fun memoryGrowExecutor(): Executor<MemoryInstruction.MemoryGrow> = { _, _ ->
            fail("MemoryGrowExecutor should not be called in this scenario")
        }

        fun memorySizeExecutor(): Executor<MemoryInstruction.MemorySize> = { _, _ ->
            fail("MemorySizeExecutor should not be called in this scenario")
        }

        fun memoryFillExecutor(): Executor<MemoryInstruction.MemoryFill> = { _, _ ->
            fail("MemoryFillExecutor should not be called in this scenario")
        }

        fun memoryCopyExecutor(): Executor<MemoryInstruction.MemoryCopy> = { _, _ ->
            fail("MemoryCopyExecutor should not be called in this scenario")
        }

        fun dataDropExecutor(): Executor<MemoryInstruction.DataDrop> = { _, _ ->
            fail("DataDropExecutor should not be called in this scenario")
        }

        fun i32LoadExecutor(): Executor<MemoryInstruction.I32Load> = { _, _ ->
            fail("I32LoadExecutor should not be called in this scenario")
        }

        fun i32SizedSignedLoadExecutor(): I32SizedSignedLoadExecutor = { _, _, _, _ ->
            fail("I32SizedSignedLoadExecutor should not be called in this scenario")
        }

        fun i32SizedUnsignedLoadExecutor(): I32SizedUnsignedLoadExecutor = { _, _, _, _ ->
            fail("I32SizedUnsignedLoadExecutor should not be called in this scenario")
        }

        fun i64LoadExecutor(): Executor<MemoryInstruction.I64Load> = { _, _ ->
            fail("I64LoadExecutor should not be called in this scenario")
        }

        fun i64SizedSignedLoadExecutor(): I64SizedSignedLoadExecutor = { _, _, _, _ ->
            fail("I64SizedSignedLoadExecutor should not be called in this scenario")
        }

        fun i64SizedUnsignedLoadExecutor(): I64SizedUnsignedLoadExecutor = { _, _, _, _ ->
            fail("I64SizedUnsignedLoadExecutor should not be called in this scenario")
        }

        fun f32LoadExecutor(): Executor<MemoryInstruction.F32Load> = { _, _ ->
            fail("F32LoadExecutor should not be called in this scenario")
        }

        fun f64LoadExecutor(): Executor<MemoryInstruction.F64Load> = { _, _ ->
            fail("F64LoadExecutor should not be called in this scenario")
        }

        fun i32StoreExecutor(): Executor<MemoryInstruction.I32Store> = { _, _ ->
            fail("I32StoreExecutor should not be called in this scenario")
        }

        fun i32StoreSizedExecutor(): I32StoreSizedExecutor = { _, _, _, _ ->
            fail("I32StoreSizedExecutor should not be called in this scenario")
        }

        fun i64StoreExecutor(): Executor<MemoryInstruction.I64Store> = { _, _ ->
            fail("I64StoreExecutor should not be called in this scenario")
        }

        fun i64StoreSizedExecutor(): I64StoreSizedExecutor = { _, _, _, _ ->
            fail("I64StoreSizedExecutor should not be called in this scenario")
        }

        fun f32StoreExecutor(): Executor<MemoryInstruction.F32Store> = { _, _ ->
            fail("F32StoreExecutor should not be called in this scenario")
        }

        fun f64StoreExecutor(): Executor<MemoryInstruction.F64Store> = { _, _ ->
            fail("F64StoreExecutor should not be called in this scenario")
        }

        fun memoryInstructionExecutor(
            context: ExecutionContext,
            instruction: MemoryInstruction,
            memoryInitExecutor: Executor<MemoryInstruction.MemoryInit> = memoryInitExecutor(),
            memoryGrowExecutor: Executor<MemoryInstruction.MemoryGrow> = memoryGrowExecutor(),
            memorySizeExecutor: Executor<MemoryInstruction.MemorySize> = memorySizeExecutor(),
            memoryFillExecutor: Executor<MemoryInstruction.MemoryFill> = memoryFillExecutor(),
            memoryCopyExecutor: Executor<MemoryInstruction.MemoryCopy> = memoryCopyExecutor(),
            dataDropExecutor: Executor<MemoryInstruction.DataDrop> = dataDropExecutor(),
            i32LoadExecutor: Executor<MemoryInstruction.I32Load> = i32LoadExecutor(),
            i32SizedSignedLoadExecutor: I32SizedSignedLoadExecutor = i32SizedSignedLoadExecutor(),
            i32SizedUnsignedLoadExecutor: I32SizedUnsignedLoadExecutor = i32SizedUnsignedLoadExecutor(),
            i64LoadExecutor: Executor<MemoryInstruction.I64Load> = i64LoadExecutor(),
            i64SizedSignedLoadExecutor: I64SizedSignedLoadExecutor = i64SizedSignedLoadExecutor(),
            i64SizedUnsignedLoadExecutor: I64SizedUnsignedLoadExecutor = i64SizedUnsignedLoadExecutor(),
            f32LoadExecutor: Executor<MemoryInstruction.F32Load> = f32LoadExecutor(),
            f64LoadExecutor: Executor<MemoryInstruction.F64Load> = f64LoadExecutor(),
            i32StoreExecutor: Executor<MemoryInstruction.I32Store> = i32StoreExecutor(),
            i32StoreSizedExecutor: I32StoreSizedExecutor = i32StoreSizedExecutor(),
            i64StoreExecutor: Executor<MemoryInstruction.I64Store> = i64StoreExecutor(),
            i64StoreSizedExecutor: I64StoreSizedExecutor = i64StoreSizedExecutor(),
            f32StoreExecutor: Executor<MemoryInstruction.F32Store> = f32StoreExecutor(),
            f64StoreExecutor: Executor<MemoryInstruction.F64Store> = f64StoreExecutor(),
        ): Result<Unit, InvocationError> {
            return MemoryInstructionExecutor(
                context = context,
                instruction = instruction,
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
