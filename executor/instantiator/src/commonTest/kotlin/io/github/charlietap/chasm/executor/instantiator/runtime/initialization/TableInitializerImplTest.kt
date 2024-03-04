package io.github.charlietap.chasm.executor.instantiator.runtime.initialization

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializerImpl
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.elementSegment
import io.github.charlietap.chasm.fixture.module.module
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class TableInitializerImplTest {

    @Test
    fun `can initialize a table on a module instance`() {

        val activeTableIndex = Index.TableIndex(0u)
        val activeExpression = Expression(
            listOf(
                NumericInstruction.I32Const(117),
            ),
        )
        val activeSegment = elementSegment(
            idx = Index.ElementIndex(0u),
            mode = ElementSegment.Mode.Active(
                tableIndex = activeTableIndex,
                offsetExpr = activeExpression,
            ),
        )
        val declarativeSegment = elementSegment(
            idx = Index.ElementIndex(1u),
            mode = ElementSegment.Mode.Declarative,
        )

        val store = store()
        val instance = moduleInstance()
        val module = module(
            elementSegments = listOf(activeSegment, declarativeSegment),
        )

        val expression1 = Expression(
            activeExpression.instructions + listOf(
                NumericInstruction.I32Const(0),
                NumericInstruction.I32Const(activeSegment.initExpressions.size),
                TableInstruction.TableInit(activeSegment.idx, (activeSegment.mode as ElementSegment.Mode.Active).tableIndex),
                TableInstruction.ElemDrop(activeSegment.idx),
            ),
        )
        val expression2 = Expression(
            listOf(TableInstruction.ElemDrop(declarativeSegment.idx)),
        )
        val expressions = sequenceOf(expression1, expression2).iterator()

        val evaluator: ExpressionEvaluator = { eStore, eInstance, eExpression, eArity ->
            assertEquals(store, eStore)
            assertEquals(instance, eInstance)
            assertEquals(expressions.next(), eExpression)
            assertEquals(Arity.SIDE_EFFECT, eArity)

            Ok(null)
        }

        val actual = TableInitializerImpl(
            store = store,
            instance = instance,
            module = module,
            evaluator = evaluator,
        )

        assertEquals(Ok(Unit), actual)
    }
}
