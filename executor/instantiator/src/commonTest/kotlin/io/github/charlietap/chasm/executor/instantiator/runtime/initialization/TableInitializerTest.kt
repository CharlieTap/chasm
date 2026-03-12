package io.github.charlietap.chasm.executor.instantiator.runtime.initialization

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.instantiator.ConstantExpressionEvaluator
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.instruction.i32ConstInstruction
import io.github.charlietap.chasm.fixture.ir.module.activeElementSegmentMode
import io.github.charlietap.chasm.fixture.ir.module.declarativeElementSegmentMode
import io.github.charlietap.chasm.fixture.ir.module.elementIndex
import io.github.charlietap.chasm.fixture.ir.module.elementSegment
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.tableIndex
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
        val tableInst = tableInstance(elements = LongArray(256))
        val activeElemInst = elementInstance()
        val declarativeElemInst = elementInstance()
        val store = store(
            tables = mutableListOf(tableInst),
            elements = mutableListOf(activeElemInst, declarativeElemInst),
        )
        val module = module(
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

        val constantExpressionEvaluator: ConstantExpressionEvaluator = { _store, _instance, _expression ->
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
