package io.github.charlietap.chasm.compiler

import com.github.michaelbull.result.unwrap
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.compiler.context.CompilerContext
import io.github.charlietap.chasm.compiler.context.FunctionCompilerWorkspace
import io.github.charlietap.chasm.compiler.diagnostic.CompilerDiagnostics
import io.github.charlietap.chasm.compiler.diagnostic.CompilerInstructionObserver
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.tag
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ThrowCompilerTest {

    @Test
    fun `throw materializes one contiguous payload for executor preflight`() {
        val fixture = throwFixture(
            parameters = 2,
            body = Expression(
                VariableInstruction.LocalGet(Index.LocalIndex(0u)),
                NumericInstruction.I32Const(22),
                ControlInstruction.Throw(Index.TagIndex(0u)),
            ),
        )

        val throwIndex = fixture.instructions.indexOfFirst { it is ControlSuperInstruction.Throw }
        assertTrue(throwIndex >= 0)
        val linkedThrow = assertIs<ControlSuperInstruction.Throw>(fixture.instructions[throwIndex])
        assertTrue(linkedThrow.firstPayloadSlot >= 0)
    }

    @Test
    fun `throw reuses proven contiguous frame operands and supports zero payload`() {
        val contiguous = throwFixture(
            parameters = 2,
            body = Expression(
                VariableInstruction.LocalGet(Index.LocalIndex(0u)),
                NumericInstruction.I32Const(1),
                NumericInstruction.I32Add,
                VariableInstruction.LocalGet(Index.LocalIndex(1u)),
                NumericInstruction.I32Const(2),
                NumericInstruction.I32Add,
                ControlInstruction.Throw(Index.TagIndex(0u)),
            ),
        )
        val linkedContiguous = assertIs<ControlSuperInstruction.Throw>(
            contiguous.instructions.first { it is ControlSuperInstruction.Throw },
        )
        assertTrue(linkedContiguous.firstPayloadSlot >= 0)

        val empty = throwFixture(
            parameters = 0,
            body = Expression(ControlInstruction.Throw(Index.TagIndex(0u))),
        )
        val linkedEmpty = assertIs<ControlSuperInstruction.Throw>(
            empty.instructions.first { it is ControlSuperInstruction.Throw },
        )
        assertEquals(0, linkedEmpty.firstPayloadSlot)
    }

    private fun throwFixture(
        parameters: Int,
        body: Expression,
    ): ThrowFixture {
        val tagFunctionType = functionType(
            params = resultType(List(parameters) { i32ValueType() }),
        )
        val functionType = functionType(
            params = resultType(List(parameters) { i32ValueType() }),
        )
        val module = module(
            definedTypes = listOf(
                definedType(recursiveType = functionRecursiveType(tagFunctionType), typeIndex = 0),
                definedType(recursiveType = functionRecursiveType(functionType), typeIndex = 1),
            ),
            functions = listOf(function(typeIndex = Index.TypeIndex(1u), body = body)),
            tags = listOf(
                tag(
                    index = Index.TagIndex(0u),
                    type = tagType(typeIndex = 0, functionType = tagFunctionType),
                ),
            ),
        )
        val function = module.functions.single()
        val store = Store()
        val types = ModuleTypeResolver(module)
        val runtimeTypes = store.heap.registerRuntimeTypes(module.definedTypes)
        val stableTagType = types.resolve(module.tags.single().type)
        val tagAddress = store.heap.registerTag(runtimeTypes[stableTagType.typeIndex], stableTagType)
        val instructions = mutableListOf<LinkedInstruction>()
        val context = CompilerContext(
            module = module,
            types = types,
            runtimeTypes = runtimeTypes,
            diagnostics = CompilerDiagnostics(
                CompilerInstructionObserver { _, instruction -> instructions += instruction },
            ),
            tags = arrayOf(store.heap.tag(tagAddress)),
        )
        FunctionCompiler(context, function, Program(32)).unwrap()
        return ThrowFixture(context, function, instructions)
    }

    private class ThrowFixture(
        val context: CompilerContext,
        val function: Function,
        val instructions: List<LinkedInstruction>,
    )
}
