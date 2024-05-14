package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.returnArity
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionEvaluatorImplTest {

    @Test
    fun `can evaluate an expression and return a result`() {

        val store = store()
        val instance = moduleInstance()

        val expression = Expression(
            listOf(NumericInstruction.I32Const(177)),
        )

        val thread = Thread(
            frame(
                arity = returnArity(1),
                state = frameState(
                    locals = mutableListOf(),
                    moduleInstance = instance,
                ),
            ),
            expression.instructions.map(::ModuleInstruction),
        )
        val expectedConfig = Configuration(store, thread)

        val threadExecutor: ThreadExecutor = { config ->
            assertEquals(expectedConfig, config)
            Ok(listOf(NumberValue.I32(117)))
        }

        val actual = ExpressionEvaluatorImpl(
            store = store,
            instance = instance,
            expression = expression,
            arity = returnArity(1),
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(NumberValue.I32(117)), actual)
    }

    @Test
    fun `can evaluate an expression for a side effect`() {

        val store = store()
        val instance = moduleInstance()

        val expression = Expression(
            listOf(TableInstruction.ElemDrop(Index.ElementIndex(0u))),
        )

        val thread = Thread(
            frame(
                arity = Arity.Return.SIDE_EFFECT,
                state = frameState(
                    mutableListOf(),
                    instance,
                ),
            ),
            expression.instructions.map(::ModuleInstruction),
        )
        val expectedConfig = Configuration(store, thread)

        val threadExecutor: ThreadExecutor = { config ->
            assertEquals(expectedConfig, config)
            Ok(emptyList())
        }

        val actual = ExpressionEvaluatorImpl(
            store = store,
            instance = instance,
            expression = expression,
            arity = Arity.Return.SIDE_EFFECT,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(null), actual)
    }
}
