package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.EndFunctionDispatcher
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.program.Program
import kotlin.test.Test
import kotlin.test.assertEquals

class ThreadExecutorTest {

    @Test
    fun `executes a program and returns its results`() {
        val program = Program()
        val entryIp = program.append(
            arrayOf(
                DispatchableInstruction { vstack, _, _, _, nextIp ->
                    vstack.setFrameSlot(0, 0L)
                    nextIp
                },
                EndFunctionDispatcher(AdminInstruction.EndFunction),
            ),
        )
        val module = moduleInstance()
        val function = wasmFunctionInstance(
            module = module,
            functionType = functionType(
                params = resultType(listOf(i32ValueType(), i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            function = runtimeFunction(
                body = runtimeExpression(entryIp),
                frameSlots = 2,
            ),
        )

        val actual = ThreadExecutor(
            config = runtimeConfig(),
            store = store(program = program),
            instance = function,
            values = listOf(i32(2), i32(3)),
        )

        assertEquals(Ok(listOf(0L)), actual)
    }
}
