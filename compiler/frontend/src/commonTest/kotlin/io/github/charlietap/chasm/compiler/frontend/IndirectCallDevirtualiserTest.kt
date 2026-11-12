package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ParametricInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.TableInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.typeResolver
import io.github.charlietap.chasm.fixture.ast.module.activeElementSegmentMode
import io.github.charlietap.chasm.fixture.ast.module.elementSegment
import io.github.charlietap.chasm.fixture.ast.module.export
import io.github.charlietap.chasm.fixture.ast.module.function
import io.github.charlietap.chasm.fixture.ast.module.import
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.ast.module.table
import io.github.charlietap.chasm.fixture.ast.module.tableExportDescriptor
import io.github.charlietap.chasm.fixture.ast.module.tableImportDescriptor
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.functionRecursiveType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64AddressType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.BlockType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class IndirectCallDevirtualiserTest {

    private val binary = definedType(
        recursiveType = functionRecursiveType(
            functionType(
                params = resultType(listOf(i32ValueType(), i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
        ),
    )
    private val nullary = definedType(recursiveType = functionRecursiveType(functionType(results = resultType(listOf(i32ValueType())))), typeIndex = 1)
    private val pair = definedType(
        recursiveType = functionRecursiveType(functionType(results = resultType(listOf(i32ValueType(), i32ValueType())))),
        typeIndex = 2,
    )
    private val binarySite = ControlInstruction.CallIndirect(Index.TypeIndex(0u), Index.TableIndex(0u))
    private val nullarySite = ControlInstruction.CallIndirect(Index.TypeIndex(1u), Index.TableIndex(0u))

    @Test
    fun `a call through a closed table becomes a compare chain over its matching entries`() {
        val module = fixture()

        val caller = IndirectCallDevirtualiser(module, module.typeResolver()).functions[3]

        val expected = Expression(
            NumericInstruction.I32Const(7),
            NumericInstruction.I32Const(1),
            NumericInstruction.I32Eq,
            ControlInstruction.If(BlockType.ValType(i32ValueType())),
            NumericInstruction.I32Const(1),
            NumericInstruction.I32Const(2),
            ControlInstruction.Call(Index.FunctionIndex(0u)),
            ControlInstruction.Else,
            NumericInstruction.I32Const(7),
            NumericInstruction.I32Const(2),
            NumericInstruction.I32Eq,
            ControlInstruction.If(BlockType.ValType(i32ValueType())),
            NumericInstruction.I32Const(1),
            NumericInstruction.I32Const(2),
            ControlInstruction.Call(Index.FunctionIndex(1u)),
            ControlInstruction.Else,
            NumericInstruction.I32Const(1),
            NumericInstruction.I32Const(2),
            NumericInstruction.I32Const(7),
            binarySite,
            ControlInstruction.End(2),
        )
        assertEquals(expected, caller.body)
        assertEquals(emptyList(), caller.locals)
    }

    @Test
    fun `computed operands are stored once and read in every arm`() {
        val sum = listOf(NumericInstruction.I32Const(3), NumericInstruction.I32Const(4), NumericInstruction.I32Add)
        val module = fixture(caller = sum + NumericInstruction.I32Const(5) + sum + binarySite)

        val caller = IndirectCallDevirtualiser(module, module.typeResolver()).functions[3]

        val expected = sum + NumericInstruction.I32Const(5) + sum + listOf(
            VariableInstruction.LocalSet(Index.LocalIndex(0u)),
            VariableInstruction.LocalSet(Index.LocalIndex(1u)),
            VariableInstruction.LocalSet(Index.LocalIndex(2u)),
            VariableInstruction.LocalGet(Index.LocalIndex(0u)),
            NumericInstruction.I32Const(1),
            NumericInstruction.I32Eq,
            ControlInstruction.If(BlockType.ValType(i32ValueType())),
            VariableInstruction.LocalGet(Index.LocalIndex(2u)),
            VariableInstruction.LocalGet(Index.LocalIndex(1u)),
            ControlInstruction.Call(Index.FunctionIndex(0u)),
            ControlInstruction.Else,
            VariableInstruction.LocalGet(Index.LocalIndex(0u)),
            NumericInstruction.I32Const(2),
            NumericInstruction.I32Eq,
            ControlInstruction.If(BlockType.ValType(i32ValueType())),
            VariableInstruction.LocalGet(Index.LocalIndex(2u)),
            VariableInstruction.LocalGet(Index.LocalIndex(1u)),
            ControlInstruction.Call(Index.FunctionIndex(1u)),
            ControlInstruction.Else,
            VariableInstruction.LocalGet(Index.LocalIndex(2u)),
            VariableInstruction.LocalGet(Index.LocalIndex(1u)),
            VariableInstruction.LocalGet(Index.LocalIndex(0u)),
            binarySite,
            ControlInstruction.End(2),
        )
        assertEquals(Expression(expected), caller.body)
        assertEquals(List(3) { index -> Local(Index.LocalIndex(index.toUInt()), i32ValueType()) }, caller.locals)
    }

    @Test
    fun `a site without parameters uses only entries of its own type`() {
        val module = fixture(caller = listOf(NumericInstruction.I32Const(7), nullarySite))

        val caller = IndirectCallDevirtualiser(module, module.typeResolver()).functions[3]

        val expected = Expression(
            NumericInstruction.I32Const(7),
            NumericInstruction.I32Const(3),
            NumericInstruction.I32Eq,
            ControlInstruction.If(BlockType.ValType(i32ValueType())),
            ControlInstruction.Call(Index.FunctionIndex(2u)),
            ControlInstruction.Else,
            NumericInstruction.I32Const(7),
            nullarySite,
            ControlInstruction.End(1),
        )
        assertEquals(expected, caller.body)
    }

    @Test
    fun `a site with several results is left alone`() {
        val site = ControlInstruction.CallIndirect(Index.TypeIndex(2u), Index.TableIndex(0u))

        assertUnchanged(fixture(segments = listOf(segment(1, 4)), caller = listOf(NumericInstruction.I32Const(7), site, ParametricInstruction.Drop)))
    }

    @Test
    fun `a later segment replaces the entries of an earlier one`() {
        val module = fixture(
            segments = listOf(
                segment(1, 0, 1, 2),
                segment(2, 0),
            ),
        )

        val body = IndirectCallDevirtualiser(module, module.typeResolver()).functions[3].body.instructions

        assertEquals(listOf(Index.FunctionIndex(0u), Index.FunctionIndex(0u)), body.filterIsInstance<ControlInstruction.Call>().map { it.functionIndex })
    }

    @Test
    fun `null and computed entries are left to the indirect call`() {
        val unknown = Expression(VariableInstruction.GlobalGet(Index.GlobalIndex(0u)))
        val absent = Expression(ReferenceInstruction.RefNull(AbstractHeapType.Func))
        val module = fixture(
            segments = listOf(
                elementSegment(
                    initExpressions = listOf(unknown, Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(1u))), absent),
                    mode = activeElementSegmentMode(offsetExpr = Expression(NumericInstruction.I32Const(1))),
                ),
            ),
        )

        val body = IndirectCallDevirtualiser(module, module.typeResolver()).functions[3].body.instructions

        assertEquals(listOf(Index.FunctionIndex(1u)), body.filterIsInstance<ControlInstruction.Call>().map { it.functionIndex })
        assertEquals(NumericInstruction.I32Const(2), body[1])
    }

    @Test
    fun `an exported table is left alone`() {
        assertUnchanged(fixture(exports = listOf(export(descriptor = tableExportDescriptor()))))
    }

    @Test
    fun `an imported table is left alone`() {
        assertUnchanged(fixture(imports = listOf(import(descriptor = tableImportDescriptor())), tables = emptyList()))
    }

    @Test
    fun `a table written by any instruction is left alone`() {
        val table = Index.TableIndex(0u)
        val writes = listOf(
            TableInstruction.TableSet(table),
            TableInstruction.TableGrow(table),
            TableInstruction.TableFill(table),
            TableInstruction.TableInit(Index.ElementIndex(0u), table),
            TableInstruction.TableCopy(Index.TableIndex(1u), table),
        )
        writes.forEach { write ->
            assertUnchanged(fixture(firstBody = listOf(write)))
        }
    }

    @Test
    fun `a table that is only read is still closed`() {
        val table = Index.TableIndex(0u)
        val reads = listOf(
            TableInstruction.TableGet(table),
            TableInstruction.TableSize(table),
            TableInstruction.TableCopy(table, Index.TableIndex(1u)),
            TableInstruction.ElemDrop(Index.ElementIndex(0u)),
            ReferenceInstruction.RefFunc(Index.FunctionIndex(0u)),
        )
        val module = fixture(firstBody = reads, tables = listOf(closedTable(), closedTable(1)))

        val body = IndirectCallDevirtualiser(module, module.typeResolver()).functions[3].body.instructions

        assertEquals(2, body.count { it is ControlInstruction.Call })
    }

    @Test
    fun `a segment with a computed offset opens its table`() {
        val module = fixture(
            segments = listOf(
                segment(1, 0, 1),
                elementSegment(
                    initExpressions = listOf(Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(2u)))),
                    mode = activeElementSegmentMode(offsetExpr = Expression(VariableInstruction.GlobalGet(Index.GlobalIndex(0u)))),
                ),
            ),
        )

        assertUnchanged(module)
    }

    @Test
    fun `entries written after a segment that may trap are not trusted`() {
        val overflowing = fixture(segments = listOf(segment(1, 0), segment(3, 2, 2), segment(2, 1)))
        val imported = fixture(
            imports = listOf(import(descriptor = tableImportDescriptor())),
            tables = listOf(closedTable(1)),
            segments = listOf(
                elementSegment(
                    initExpressions = listOf(Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(0u)))),
                    mode = activeElementSegmentMode(offsetExpr = Expression(NumericInstruction.I32Const(0))),
                ),
                segment(1, 0, 1, table = 1),
            ),
            caller = listOf(
                NumericInstruction.I32Const(1),
                NumericInstruction.I32Const(2),
                NumericInstruction.I32Const(7),
                ControlInstruction.CallIndirect(Index.TypeIndex(0u), Index.TableIndex(1u)),
            ),
        )

        val body = IndirectCallDevirtualiser(overflowing, overflowing.typeResolver()).functions[3].body.instructions

        assertEquals(listOf(Index.FunctionIndex(0u)), body.filterIsInstance<ControlInstruction.Call>().map { it.functionIndex })
        assertUnchanged(imported)
    }

    @Test
    fun `a table indexed by i64 is left alone`() {
        assertUnchanged(fixture(tables = listOf(table(type = tableType(addressType = i64AddressType(), limits = limits(min = 4u))))))
    }

    @Test
    fun `a site with more than four matching entries is left alone`() {
        assertUnchanged(fixture(tables = listOf(table(type = tableType(limits = limits(min = 8u)))), segments = listOf(segment(0, 0, 1, 0, 1, 0))))
    }

    @Test
    fun `a site whose type matches no entry is left alone`() {
        assertUnchanged(fixture(segments = listOf(segment(1, 0, 1)), caller = listOf(NumericInstruction.I32Const(7), nullarySite)))
    }

    private fun assertUnchanged(
        module: Module,
    ) = assertSame(module, IndirectCallDevirtualiser(module, module.typeResolver()))

    private fun closedTable(
        index: Int = 0,
    ) = table(idx = Index.TableIndex(index.toUInt()), type = tableType(limits = limits(min = 4u)))

    private fun segment(
        offset: Int,
        vararg functions: Int,
        table: Int = 0,
    ): ElementSegment = elementSegment(
        initExpressions = functions.map { function -> Expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(function.toUInt()))) },
        mode = activeElementSegmentMode(tableIndex = Index.TableIndex(table.toUInt()), offsetExpr = Expression(NumericInstruction.I32Const(offset))),
    )

    private fun fixture(
        imports: List<Import> = emptyList(),
        exports: List<Export> = emptyList(),
        tables: List<Table> = listOf(closedTable()),
        segments: List<ElementSegment> = listOf(segment(1, 0, 1, 2)),
        firstBody: List<Instruction> = emptyList(),
        caller: List<Instruction> = listOf(NumericInstruction.I32Const(1), NumericInstruction.I32Const(2), NumericInstruction.I32Const(7), binarySite),
    ): Module = module(
        definedTypes = listOf(binary, nullary, pair),
        imports = imports,
        exports = exports,
        tables = tables,
        elementSegments = segments,
        functions = listOf(
            function(idx = Index.FunctionIndex(0u), typeIndex = Index.TypeIndex(0u), body = Expression(firstBody)),
            function(idx = Index.FunctionIndex(1u), typeIndex = Index.TypeIndex(0u)),
            function(idx = Index.FunctionIndex(2u), typeIndex = Index.TypeIndex(1u)),
            function(idx = Index.FunctionIndex(3u), typeIndex = Index.TypeIndex(1u), body = Expression(caller)),
            function(idx = Index.FunctionIndex(4u), typeIndex = Index.TypeIndex(2u)),
        ),
    )
}
