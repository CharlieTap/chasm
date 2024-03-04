package io.github.charlietap.chasm.executor.instantiator.runtime.initialization

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializerImpl
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.dataSegment
import io.github.charlietap.chasm.fixture.module.module
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryInitializerImplTest {

    @Test
    fun `can initialize a memory on a module instance`() {

        val activeDataIndex = Index.DataIndex(0u)
        val activeExpression = Expression(
            listOf(
                NumericInstruction.I32Const(117),
            ),
        )
        val activeSegment = dataSegment(
            idx = activeDataIndex,
            mode = DataSegment.Mode.Active(
                memoryIndex = Index.MemoryIndex(0u),
                offset = activeExpression,
            ),
        )

        val store = store()
        val instance = moduleInstance()
        val module = module(
            dataSegments = listOf(activeSegment),
        )

        val expression = Expression(
            activeExpression.instructions + listOf(
                NumericInstruction.I32Const(0),
                NumericInstruction.I32Const(activeSegment.initData.size),
                MemoryInstruction.MemoryInit(activeSegment.idx),
                MemoryInstruction.DataDrop(activeSegment.idx),
            ),
        )

        val evaluator: ExpressionEvaluator = { eStore, eInstance, eExpression, eArity ->
            assertEquals(store, eStore)
            assertEquals(instance, eInstance)
            assertEquals(expression, eExpression)
            assertEquals(Arity.SIDE_EFFECT, eArity)

            Ok(null)
        }

        val actual = MemoryInitializerImpl(
            store = store,
            instance = instance,
            module = module,
            evaluator = evaluator,
        )

        assertEquals(Ok(Unit), actual)
    }
}
