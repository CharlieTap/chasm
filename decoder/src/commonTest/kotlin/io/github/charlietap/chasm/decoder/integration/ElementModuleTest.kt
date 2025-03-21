package io.github.charlietap.chasm.decoder.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.config.moduleConfig
import io.github.charlietap.chasm.decoder.FakeSourceReader
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.TableType
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
                SubType.Final(emptyList(), CompositeType.Function(expectedFunctionType)),
            ),
            state = RecursiveType.State.SYNTAX,
        )
        val expectedType = Type(Index.TypeIndex(0u), expectedRecursiveType)

        val expectedFunction = Function(
            idx = Index.FunctionIndex(0u),
            typeIndex = Index.TypeIndex(0u),
            locals = emptyList(),
            body = Expression(emptyList()),
        )

        val table = Table(
            idx = Index.TableIndex(0u),
            type = TableType(ReferenceType.RefNull(AbstractHeapType.Func), Limits(1u)),
            initExpression = Expression(listOf(ReferenceInstruction.RefNull(AbstractHeapType.Func))),
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
