package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.local
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class FunctionScopeTest {

    @Test
    fun `reuses function scratch collections`() {
        val i32 = ValueType.Number(NumberType.I32)
        val type = functionType(params = ResultType(listOf(i32)))
        val context = ModuleValidationContext(
            config = ModuleConfig(),
            module = module(
                definedTypes = listOf(definedType(recursiveType = functionRecursiveType(type))),
            ),
        )
        val function = function(locals = listOf(local(type = i32)))
        val locals = context.locals
        val localChanges = context.localChanges
        val labels = context.labels
        val operands = context.operands

        repeat(2) {
            val result = FunctionScope(context, function) { scopedContext ->
                assertEquals(2, scopedContext.locals.size)
                Ok(Unit)
            }

            assertEquals(Ok(Unit), result)
            assertSame(locals, context.locals)
            assertSame(localChanges, context.localChanges)
            assertSame(labels, context.labels)
            assertSame(operands, context.operands)
            assertEquals(emptyList(), context.locals)
            assertEquals(emptyList(), context.localChanges)
            assertEquals(0, context.labels.depth())
            assertEquals(0, context.operands.depth())
        }
    }
}
