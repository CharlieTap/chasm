package io.github.charlietap.chasm.executor.instantiator.runtime.initialization

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.ir.instruction.dataDropInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.memoryInitInstruction
import io.github.charlietap.chasm.fixture.ir.module.activeDataSegmentMode
import io.github.charlietap.chasm.fixture.ir.module.dataIndex
import io.github.charlietap.chasm.fixture.ir.module.dataSegment
import io.github.charlietap.chasm.fixture.ir.module.memoryIndex
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.predecoder.Predecoder
import io.github.charlietap.chasm.runtime.Arity
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.runtime.function.Expression as RuntimeExpression

class MemoryInitializerTest {

    @Test
    fun `can initialize a memory on a module instance`() {

        val activeDataIndex = dataIndex(0)
        val offset = 117
        val activeOffsetExpression = expression(
            listOf(
                i32ConstInstruction(offset),
            ),
        )
        val memoryIndex = memoryIndex(0)
        val activeSegment = dataSegment(
            idx = activeDataIndex,
            mode = activeDataSegmentMode(
                memoryIndex = memoryIndex,
                offset = activeOffsetExpression,
            ),
        )

        val config = runtimeConfig()
        val store = store()
        val module = module(
            dataSegments = listOf(activeSegment),
        )

        val instance = moduleInstance()
        val context = instantiationContext(
            store = store,
            module = module,
            config = config,
            instance = instance,
        )

        val expression = expression(
            listOf(
                i32ConstInstruction(offset),
                i32ConstInstruction(0),
                i32ConstInstruction(activeSegment.initData.size),
                memoryInitInstruction(memoryIndex, activeDataIndex),
                dataDropInstruction(activeDataIndex),
            ),
        )
        val runtimeExpression = runtimeExpression()
        val expressionPredecoder: Predecoder<Expression, RuntimeExpression> = { _context, _expression ->
            assertEquals(expression, _expression)
            Ok(runtimeExpression)
        }

        val evaluator: ExpressionEvaluator = { _config, _store, _instance, _expression, _arity ->
            assertEquals(config, _config)
            assertEquals(store, _store)
            assertEquals(instance, _instance)
            assertEquals(runtimeExpression, _expression)
            assertEquals(Arity.Return.SIDE_EFFECT, _arity)

            Ok(null)
        }

        val actual = MemoryInitializer(
            context = context,
            instance = instance,
            evaluator = evaluator,
            expressionPredecoder = expressionPredecoder,
        )

        assertEquals(Ok(Unit), actual)
    }
}
