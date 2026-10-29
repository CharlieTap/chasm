package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.AtomicMemoryInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import kotlin.test.Test
import kotlin.test.assertEquals

class UncompilableFunctionsTest {

    @Test
    fun `reports every function that cannot compile by its index`() {
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(
                function(body = Expression(ControlInstruction.Nop)),
                function(idx = Index.FunctionIndex(1u), body = Expression(AtomicMemoryInstruction.Fence)),
                function(idx = Index.FunctionIndex(2u), body = Expression(VectorInstruction.V128Const(ByteArray(16)))),
            ),
        )

        val expected = mapOf(
            1 to CompilationError.UnsupportedThreads,
            2 to CompilationError.UnsupportedSimd,
        )

        assertEquals(expected, UncompilableFunctions(module))
    }

    @Test
    fun `reports nothing when every function compiles`() {
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(function(body = Expression(ControlInstruction.Nop))),
        )

        assertEquals(emptyMap(), UncompilableFunctions(module))
    }
}
