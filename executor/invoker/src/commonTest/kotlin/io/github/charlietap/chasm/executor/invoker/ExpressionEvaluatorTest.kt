package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.fixture.executor.runtime.configuration
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.returnArity
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.thread
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
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
            Ok(listOf(i32(117)))
        }

        val actual = ExpressionEvaluator(
            config = config,
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
