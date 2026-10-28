package io.github.charlietap.chasm.compiler.frontend

import com.github.michaelbull.result.unwrapError
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

class ModuleCompilerTest {

    @Test
    fun `fails with the unsupported threads error of a later function`() {
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(
                function(body = Expression(ControlInstruction.Nop)),
                function(idx = Index.FunctionIndex(1u), body = Expression(AtomicMemoryInstruction.Fence)),
            ),
        )

        assertEquals(CompilationError.UnsupportedThreads, ModuleCompiler(module).unwrapError())
    }

    @Test
    fun `fails with the unsupported SIMD error for a vector instruction`() {
        val module = module(
            definedTypes = listOf(definedType(recursiveType = functionRecursiveType())),
            functions = listOf(function(body = Expression(VectorInstruction.V128Const(ByteArray(16))))),
        )

        assertEquals(CompilationError.UnsupportedSimd, ModuleCompiler(module).unwrapError())
    }
}
