package io.github.charlietap.chasm.executor.instantiator.runtime.initialization

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.ElementSegmentDeclaration
import io.github.charlietap.chasm.executor.instantiator.ConstantExpressionEvaluator
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.executor.instantiator.constantExpression
import io.github.charlietap.chasm.fixture.executor.instantiator.elementSegmentDeclaration
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.instantiator.moduleDeclarations
import io.github.charlietap.chasm.fixture.runtime.instance.elementAddress
import io.github.charlietap.chasm.fixture.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals

class TableInitializerTest {

    @Test
    fun `can initialize a table on a module instance`() {

        val activeOffsetExpression = constantExpression(ConstantInstruction.I32Const(117))
        val activeSegment = elementSegmentDeclaration(
            mode = ElementSegmentDeclaration.Mode.Active(
                tableIndex = tableIndex(0u),
                offsetExpr = activeOffsetExpression,
            ),
        )
        val declarativeSegment = elementSegmentDeclaration(
            mode = ElementSegmentDeclaration.Mode.Declarative,
        )

        val config = runtimeConfig()
        val tableInst = tableInstance(elements = LongArray(256))
        val activeElemInst = elementInstance()
        val declarativeElemInst = elementInstance()
        val store = store(
            tables = mutableListOf(tableInst),
            elements = mutableListOf(activeElemInst, declarativeElemInst),
        )
        val module = moduleDeclarations(
            elementSegments = listOf(activeSegment, declarativeSegment),
        )
        val instance = moduleInstance(
            tableAddresses = mutableListOf(tableAddress(0)),
            elemAddresses = mutableListOf(elementAddress(0), elementAddress(1)),
        )
        val context = instantiationContext(
            store = store,
            module = module,
            config = config,
            instance = instance,
        )

        val constantExpressionEvaluator: ConstantExpressionEvaluator = { _store, _instance, _, _expression ->
            assertEquals(store, _store)
            assertEquals(instance, _instance)
            assertEquals(activeOffsetExpression, _expression)

            Ok(117L)
        }

        val actual = TableInitializer(
            context = context,
            instance = instance,
            constantExpressionEvaluator = constantExpressionEvaluator,
        )

        assertEquals(Ok(Unit), actual)
    }
}
