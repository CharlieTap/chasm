package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.fixture.executor.runtime.configuration
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.returnArity
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.thread
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionEvaluatorTest {

    @Test
    fun `can evaluate an expression and return a result`() {

        val store = store()
        val instance = moduleInstance()

        val expression = runtimeExpression()

        val thread = thread(
            frame(
                arity = returnArity(1),
                locals = mutableListOf(),
                instance = instance,
            ),
            expression.instructions,
        )
        val expectedConfig = configuration(store, thread)

        val threadExecutor: ThreadExecutor = { config ->
            assertEquals(expectedConfig, config)
            Ok(listOf(i32(117)))
        }

        val actual = ExpressionEvaluator(
            store = store,
            instance = instance,
            expression = expression,
            arity = returnArity(1),
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(i32(117)), actual)
    }

    @Test
    fun `can evaluate an expression for a side effect`() {

        val store = store()
        val instance = moduleInstance()

        val expression = runtimeExpression()

        val thread = thread(
            frame(
                arity = Arity.Return.SIDE_EFFECT,
                locals = mutableListOf(),
                instance = instance,
            ),
            expression.instructions,
        )
        val expectedConfig = configuration(store, thread)

        val threadExecutor: ThreadExecutor = { config ->
            assertEquals(expectedConfig, config)
            Ok(emptyList())
        }

        val actual = ExpressionEvaluator(
            store = store,
            instance = instance,
            expression = expression,
            arity = Arity.Return.SIDE_EFFECT,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(null), actual)
    }
}
