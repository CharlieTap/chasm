package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.configuration
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.returnArity
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.thread
import io.github.charlietap.chasm.runtime.Arity
import kotlin.test.Test
import kotlin.test.assertEquals

class ExpressionEvaluatorTest {

    @Test
    fun `can evaluate an expression and return a result`() {

        val config = runtimeConfig()
        val store = store()
        val instance = moduleInstance()

        val expression = runtimeExpression()

        val thread = thread(
            frame(
                arity = 1,
                instance = instance,
            ),
            expression.instructions,
        )
        val expectedConfig = configuration(store, thread)

        val threadExecutor: ThreadExecutor = { config, params ->
            assertEquals(expectedConfig, config)
            assertEquals(emptyList(), params)
            Ok(listOf(117))
        }

        val actual = ExpressionEvaluator(
            config = config,
            store = store,
            instance = instance,
            expression = expression,
            arity = returnArity(1),
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(117L), actual)
    }

    @Test
    fun `can evaluate an expression for a side effect`() {

        val config = runtimeConfig()
        val store = store()
        val instance = moduleInstance()

        val expression = runtimeExpression()

        val thread = thread(
            frame(
                arity = 0,
                instance = instance,
            ),
            expression.instructions,
        )
        val expectedConfig = configuration(store, thread)

        val threadExecutor: ThreadExecutor = { config, params ->
            assertEquals(expectedConfig, config)
            assertEquals(emptyList(), params)
            Ok(emptyList())
        }

        val actual = ExpressionEvaluator(
            config = config,
            store = store,
            instance = instance,
            expression = expression,
            arity = Arity.Return.SIDE_EFFECT,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(null), actual)
    }
}
