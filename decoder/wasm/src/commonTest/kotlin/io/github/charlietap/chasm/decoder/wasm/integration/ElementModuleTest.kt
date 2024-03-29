package io.github.charlietap.chasm.decoder.wasm.integration

import com.github.michaelbull.result.Ok
import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Type
import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.HeapType
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.decoder.wasm.WasmModuleDecoder
import io.github.charlietap.chasm.decoder.wasm.reader.FakeSourceReader
import kotlin.test.Test
import kotlin.test.assertEquals

class ElementModuleTest {

    @Test
    fun `can decode an element module section`() {

        val byteStream = Resource("src/commonTest/resources/element.wasm").readBytes().asSequence()

        val reader = FakeSourceReader(byteStream)

        val expectedFunctionType = FunctionType(
            params = ResultType(emptyList()),
            results = ResultType(emptyList()),
        )
        val expectedType = Type(Index.TypeIndex(0u), expectedFunctionType)

        val expectedFunction = Function(
            idx = Index.FunctionIndex(0u),
            typeIndex = Index.TypeIndex(0u),
            locals = emptyList(),
            body = Expression(emptyList()),
        )

        val table = Table(
            idx = Index.TableIndex(0u),
            type = TableType(ReferenceType.RefNull(HeapType.Func), Limits(1u)),
            initExpression = Expression(listOf(ReferenceInstruction.RefNull(HeapType.Func))),
        )

        val elementSegment = ElementSegment(
            idx = Index.ElementIndex(0u),
            type = ReferenceType.RefNull(HeapType.Func),
            initExpressions = listOf(Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(0u)))),
            mode = ElementSegment.Mode.Active(
                tableIndex = Index.TableIndex(0u),
                offsetExpr = Expression(listOf(NumericInstruction.I32Const(0))),
            ),
        )

        val expected = Ok(
            Module(
                version = Version.One,
                types = listOf(expectedType),
                imports = emptyList(),
                functions = listOf(expectedFunction),
                tables = listOf(table),
                memories = emptyList(),
                globals = emptyList(),
                exports = emptyList(),
                startFunction = null,
                elementSegments = listOf(elementSegment),
                dataSegments = emptyList(),
                customs = emptyList(),
            ),
        )

        val actual = WasmModuleDecoder(reader)

        assertEquals(expected, actual)
    }
}
