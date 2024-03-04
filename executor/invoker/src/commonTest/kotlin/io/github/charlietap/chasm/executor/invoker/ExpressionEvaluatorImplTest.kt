package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.Configuration
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.Thread
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.instance.moduleInstance
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
            Stack.Entry.ActivationFrame(
                Arity(1),
                Stack.Entry.ActivationFrame.State(
                    mutableListOf(),
                    instance,
                ),
            ),
            expression.instructions,
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
            arity = Arity(1),
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
            Stack.Entry.ActivationFrame(
                Arity.SIDE_EFFECT,
                Stack.Entry.ActivationFrame.State(
                    mutableListOf(),
                    instance,
                ),
            ),
            expression.instructions,
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
            arity = Arity.SIDE_EFFECT,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(null), actual)
    }
}
