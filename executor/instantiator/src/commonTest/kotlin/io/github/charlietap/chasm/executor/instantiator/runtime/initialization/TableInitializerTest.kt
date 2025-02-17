package io.github.charlietap.chasm.executor.instantiator.runtime.initialization

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.instantiator.predecoding.Predecoder
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.ir.instruction.elemDropInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.instruction.tableInitInstruction
import io.github.charlietap.chasm.fixture.ir.module.activeElementSegmentMode
import io.github.charlietap.chasm.fixture.ir.module.declarativeElementSegmentMode
import io.github.charlietap.chasm.fixture.ir.module.elementIndex
import io.github.charlietap.chasm.fixture.ir.module.elementSegment
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.tableIndex
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.ElementSegment
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.executor.runtime.function.Expression as RuntimeExpression

class TableInitializerTest {

    @Test
    fun `can initialize a table on a module instance`() {

        val activeTableIndex = tableIndex(0)
        val activeOffsetExpression = expression(
            listOf(
                i32ConstInstruction(117),
            ),
        )
        val activeSegment = elementSegment(
            idx = elementIndex(0),
            mode = activeElementSegmentMode(
                tableIndex = activeTableIndex,
                offsetExpr = activeOffsetExpression,
            ),
        )
        val declarativeSegment = elementSegment(
            idx = elementIndex(1),
            mode = declarativeElementSegmentMode(),
        )

        val config = runtimeConfig()
        val store = store()
        val module = module(
            elementSegments = listOf(activeSegment, declarativeSegment),
        )
        val context = instantiationContext(
            store = store,
            module = module,
            config = config,
        )
        val instance = moduleInstance()

        val expression1 = expression(
            activeOffsetExpression.instructions + listOf(
                i32ConstInstruction(0),
                i32ConstInstruction(activeSegment.initExpressions.size),
                tableInitInstruction(activeSegment.idx, (activeSegment.mode as ElementSegment.Mode.Active).tableIndex),
                elemDropInstruction(activeSegment.idx),
            ),
        )
        val expression2 = expression(
            listOf(elemDropInstruction(declarativeSegment.idx)),
        )
        val expressions = sequenceOf(expression1, expression2).iterator()

        val runtimeExpression1 = runtimeExpression()
        val runtimeExpression2 = runtimeExpression()
        val runtimeExpressions = sequenceOf(
            runtimeExpression1,
            runtimeExpression1,
            runtimeExpression2,
            runtimeExpression2,
        ).iterator()

        val expressionPredecoder: Predecoder<Expression, RuntimeExpression> = { _context, _expression ->
            assertEquals(context, _context)
            assertEquals(expressions.next(), _expression)

            Ok(runtimeExpressions.next())
        }

        val evaluator: ExpressionEvaluator = { _config, _store, _instance, _expression, _arity ->
            assertEquals(config, _config)
            assertEquals(store, _store)
            assertEquals(instance, _instance)
            assertEquals(runtimeExpressions.next(), _expression)
            assertEquals(Arity.Return.SIDE_EFFECT, _arity)

            Ok(null)
        }

        val actual = TableInitializer(
            context = context,
            instance = instance,
            evaluator = evaluator,
            expressionPredecoder = expressionPredecoder,
        )

        assertEquals(Ok(Unit), actual)
    }
}
