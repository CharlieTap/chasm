package io.github.charlietap.chasm.compiler.backend.chasm

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.tag
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.store.Store
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import io.github.charlietap.chasm.cir.ControlInstruction as CirControlInstruction

class ThrowLinkerTest {

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

        assertTrue(fixture.throwInstruction.firstPayloadSlot >= 0)
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
        assertTrue(contiguous.throwInstruction.firstPayloadSlot >= 0)

        val empty = throwFixture(
            parameters = 0,
            body = Expression(ControlInstruction.Throw(Index.TagIndex(0u))),
        )
        assertEquals(0, empty.throwInstruction.firstPayloadSlot)
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
        val store = Store()
        val types = module.typeResolver()
        val runtimeTypes = store.heap.registerRuntimeTypes(module.definedTypes)
        val stableTagType = types.resolve(module.tags.single().type)
        val tagAddress = store.heap.registerTag(runtimeTypes[stableTagType.typeIndex], stableTagType)
        val linked = linkFunction(
            module = module,
            store = store,
            runtimeTypes = runtimeTypes,
            instance = moduleInstance(runtimeTypes = runtimeTypes, tagAddresses = mutableListOf(tagAddress)),
        )
        return ThrowFixture(linked.function.instructions.filterIsInstance<CirControlInstruction.Throw>().single())
    }

    private class ThrowFixture(
        val throwInstruction: CirControlInstruction.Throw,
    )
}
