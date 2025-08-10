package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.fixture.config.moduleConfig
import io.github.charlietap.chasm.fake.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.ast.instruction.expression
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.functionIndex
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.table
import io.github.charlietap.chasm.fixture.ast.module.tableIndex
import io.github.charlietap.chasm.fixture.ast.module.type
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.functionCompositeType
import io.github.charlietap.chasm.fixture.type.i32AddressType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.test.Test
import kotlin.test.assertEquals

class ElementModuleTest {

    @Test
    fun `can decode an element module section`() {

        val byteStream = Resource("src/commonTest/resources/element.wasm").readBytes()

        val config = moduleConfig()
        val reader = FakeSourceReader(byteStream)

        val expectedFunctionType = FunctionType(
            params = resultType(emptyList()),
            results = resultType(emptyList()),
        )
        val expectedRecursiveType = recursiveType(
            subTypes = listOf(
                finalSubType(emptyList(), functionCompositeType(expectedFunctionType)),
            ),
            state = RecursiveType.State.SYNTAX,
        )
        val expectedType = type(typeIndex(0u), expectedRecursiveType)
        val expectedDefinedType = definedType(
            expectedRecursiveType.copy(state = RecursiveType.State.CLOSED),
        )

        val expectedFunction = function(
            idx = functionIndex(0u),
            typeIndex = typeIndex(0u),
            locals = emptyList(),
            body = expression(emptyList()),
        )

        val table = table(
            idx = tableIndex(0u),
            type = tableType(i32AddressType(), refNullReferenceType(AbstractHeapType.Func), Limits(1u)),
            initExpression = expression(listOf(ReferenceInstruction.RefNull(AbstractHeapType.Func))),
        )

        val elementSegment = ElementSegment(
            idx = Index.ElementIndex(0u),
            type = ReferenceType.Ref(AbstractHeapType.Func),
            initExpressions = listOf(Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(0u)))),
            mode = ElementSegment.Mode.Active(
                tableIndex = Index.TableIndex(0u),
                offsetExpr = Expression(listOf(NumericInstruction.I32Const(0))),
            ),
        )

        val expected = Ok(
            module(
                version = Version.One,
                types = listOf(expectedType),
                definedTypes = listOf(expectedDefinedType),
                functions = listOf(expectedFunction),
                tables = listOf(table),
                elementSegments = listOf(elementSegment),
            ),
        )

        val actual = WasmModuleDecoder(
            config = config,
            source = reader,
        )

        assertEquals(expected, actual)
    }
}
