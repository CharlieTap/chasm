package io.github.charlietap.chasm.executor.invoker.instruction.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
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

        val i64Load32SExecutor: Executor<MemoryInstruction.I64Load32S> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = memoryInstructionExecutor(
            context = context,
            instruction = instruction,
            i64Load32SExecutor = i64Load32SExecutor,
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

        val i64Load32UExecutor: Executor<MemoryInstruction.I64Load32U> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = memoryInstructionExecutor(
            context = context,
            instruction = instruction,
            i64Load32UExecutor = i64Load32UExecutor,
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

        val i64StoreSizedExecutor: Executor<MemoryInstruction.I64Store32> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(instruction, _instruction)

            Ok(Unit)
        }

        val actual = memoryInstructionExecutor(
            context = context,
            instruction = instruction,
            i64Store32Executor = i64StoreSizedExecutor,
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

        fun i32Load8SExecutor(): Executor<MemoryInstruction.I32Load8S> = { _, _ ->
            fail("I32Load8SExecutor should not be called in this scenario")
        }

        fun i32Load8UExecutor(): Executor<MemoryInstruction.I32Load8U> = { _, _ ->
            fail("I32Load8UExecutor should not be called in this scenario")
        }

        fun i32Load16SExecutor(): Executor<MemoryInstruction.I32Load16S> = { _, _ ->
            fail("I32Load16SExecutor should not be called in this scenario")
        }

        fun i32Load16UExecutor(): Executor<MemoryInstruction.I32Load16U> = { _, _ ->
            fail("I32Load16UExecutor should not be called in this scenario")
        }

        fun i64LoadExecutor(): Executor<MemoryInstruction.I64Load> = { _, _ ->
            fail("I64LoadExecutor should not be called in this scenario")
        }

        fun i64Load8SExecutor(): Executor<MemoryInstruction.I64Load8S> = { _, _ ->
            fail("I64Load8SExecutor should not be called in this scenario")
        }

        fun i64Load8UExecutor(): Executor<MemoryInstruction.I64Load8U> = { _, _ ->
            fail("I64Load8UExecutor should not be called in this scenario")
        }

        fun i64Load16SExecutor(): Executor<MemoryInstruction.I64Load16S> = { _, _ ->
            fail("I64Load16SExecutor should not be called in this scenario")
        }

        fun i64Load16UExecutor(): Executor<MemoryInstruction.I64Load16U> = { _, _ ->
            fail("I64Load16UExecutor should not be called in this scenario")
        }

        fun i64Load32SExecutor(): Executor<MemoryInstruction.I64Load32S> = { _, _ ->
            fail("I64Load32SExecutor should not be called in this scenario")
        }

        fun i64Load32UExecutor(): Executor<MemoryInstruction.I64Load32U> = { _, _ ->
            fail("I64Load32UExecutor should not be called in this scenario")
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

        fun i32Store8Executor(): Executor<MemoryInstruction.I32Store8> = { _, _ ->
            fail("I32Store8Executor should not be called in this scenario")
        }

        fun i32Store16Executor(): Executor<MemoryInstruction.I32Store16> = { _, _ ->
            fail("I32Store16Executor should not be called in this scenario")
        }

        fun i64StoreExecutor(): Executor<MemoryInstruction.I64Store> = { _, _ ->
            fail("I64StoreExecutor should not be called in this scenario")
        }

        fun i64Store8Executor(): Executor<MemoryInstruction.I64Store8> = { _, _ ->
            fail("I64Store8Executor should not be called in this scenario")
        }

        fun i64Store16Executor(): Executor<MemoryInstruction.I64Store16> = { _, _ ->
            fail("I64Store16Executor should not be called in this scenario")
        }

        fun i64Store32Executor(): Executor<MemoryInstruction.I64Store32> = { _, _ ->
            fail("I64Store32Executor should not be called in this scenario")
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
            i32Load8SExecutor: Executor<MemoryInstruction.I32Load8S> = i32Load8SExecutor(),
            i32Load8UExecutor: Executor<MemoryInstruction.I32Load8U> = i32Load8UExecutor(),
            i32Load16SExecutor: Executor<MemoryInstruction.I32Load16S> = i32Load16SExecutor(),
            i32Load16UExecutor: Executor<MemoryInstruction.I32Load16U> = i32Load16UExecutor(),
            i64LoadExecutor: Executor<MemoryInstruction.I64Load> = i64LoadExecutor(),
            i64Load8SExecutor: Executor<MemoryInstruction.I64Load8S> = i64Load8SExecutor(),
            i64Load8UExecutor: Executor<MemoryInstruction.I64Load8U> = i64Load8UExecutor(),
            i64Load16SExecutor: Executor<MemoryInstruction.I64Load16S> = i64Load16SExecutor(),
            i64Load16UExecutor: Executor<MemoryInstruction.I64Load16U> = i64Load16UExecutor(),
            i64Load32SExecutor: Executor<MemoryInstruction.I64Load32S> = i64Load32SExecutor(),
            i64Load32UExecutor: Executor<MemoryInstruction.I64Load32U> = i64Load32UExecutor(),
            f32LoadExecutor: Executor<MemoryInstruction.F32Load> = f32LoadExecutor(),
            f64LoadExecutor: Executor<MemoryInstruction.F64Load> = f64LoadExecutor(),
            i32StoreExecutor: Executor<MemoryInstruction.I32Store> = i32StoreExecutor(),
            i32Store8Executor: Executor<MemoryInstruction.I32Store8> = i32Store8Executor(),
            i32Store16Executor: Executor<MemoryInstruction.I32Store16> = i32Store16Executor(),
            i64StoreExecutor: Executor<MemoryInstruction.I64Store> = i64StoreExecutor(),
            i64Store8Executor: Executor<MemoryInstruction.I64Store8> = i64Store8Executor(),
            i64Store16Executor: Executor<MemoryInstruction.I64Store16> = i64Store16Executor(),
            i64Store32Executor: Executor<MemoryInstruction.I64Store32> = i64Store32Executor(),
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
                i32Load8SExecutor = i32Load8SExecutor,
                i32Load8UExecutor = i32Load8UExecutor,
                i32Load16SExecutor = i32Load16SExecutor,
                i32Load16UExecutor = i32Load16UExecutor,
                i64LoadExecutor = i64LoadExecutor,
                i64Load8SExecutor = i64Load8SExecutor,
                i64Load8UExecutor = i64Load8UExecutor,
                i64Load16SExecutor = i64Load16SExecutor,
                i64Load16UExecutor = i64Load16UExecutor,
                i64Load32SExecutor = i64Load32SExecutor,
                i64Load32UExecutor = i64Load32UExecutor,
                f32LoadExecutor = f32LoadExecutor,
                f64LoadExecutor = f64LoadExecutor,
                i32StoreExecutor = i32StoreExecutor,
                i32Store8Executor = i32Store8Executor,
                i32Store16Executor = i32Store16Executor,
                i64StoreExecutor = i64StoreExecutor,
                i64Store8Executor = i64Store8Executor,
                i64Store16Executor = i64Store16Executor,
                i64Store32Executor = i64Store32Executor,
                f32StoreExecutor = f32StoreExecutor,
                f64StoreExecutor = f64StoreExecutor,
            )
        }
    }
}
