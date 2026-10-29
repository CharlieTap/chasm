package io.github.charlietap.chasm.declarations.construction

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.declarations.ConstantExpression
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.declarations.ElementSegmentDeclaration
import io.github.charlietap.chasm.declarations.GlobalDeclaration
import io.github.charlietap.chasm.declarations.ModuleNames
import io.github.charlietap.chasm.declarations.TableDeclaration
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.SharedStatus
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class DeclarationsBuilderTest {

    @Test
    fun `calls build declarations in the order they were made`() {
        val builder = DeclarationsBuilder()

        builder.strings("run")
        builder.stringLengths("\u0003")
        builder.endStrings()

        builder.group(1)
        builder.group(2)
        builder.numberType(ValueTypes.I32)
        builder.functionComposite(0, 1)
        builder.subType(true, 0)
        builder.groupBody(0, 1)
        builder.structComposite(0)
        builder.subType(true, 0)
        builder.numberType(ValueTypes.I32)
        builder.fieldType(ValueTypes.STORAGE_VALUE, true)
        builder.arrayComposite()
        builder.subType(true, 0)
        builder.groupBody(1, 2)

        builder.functions("\u0000")

        builder.abstractHeapType(HeapTypes.FUNC)
        builder.referenceType(true)
        builder.limits(1, false, 0)
        builder.tableType(false)
        builder.abstractHeapType(HeapTypes.FUNC)
        builder.refNull()
        builder.constantExpression(1)
        builder.table()

        builder.limits(1, true, 2)
        builder.memoryType(false, false)
        builder.memory()

        builder.numberType(ValueTypes.I64)
        builder.globalType(true)
        builder.i64Const(7)
        builder.constantExpression(1)
        builder.global()

        builder.exportKinds("\u0000")
        builder.exportIndices("\u0000")
        builder.exportNames("\u0000")

        builder.abstractHeapType(HeapTypes.FUNC)
        builder.referenceType(false)
        builder.i32Const(2)
        builder.constantExpression(1)
        builder.refFuncConstantExpressions("\u0000")
        builder.elementSegment(ElementModes.ACTIVE, 0, 1)

        builder.i32Const(16)
        builder.i32Const(4)
        builder.op(ConstantOps.I32_ADD)
        builder.constantExpression(3)
        builder.dataSegment(true, 0, 0, 8)

        val declarations = builder.build(null)

        assertEquals(listOf(0, 1, 2), declarations.definedTypes.map { type -> type.typeIndex })
        assertEquals(listOf(0, 0, 1), declarations.definedTypes.map { type -> type.recursiveTypeIndex })
        assertSame(declarations.definedTypes[1].recursiveType, declarations.definedTypes[2].recursiveType)
        assertEquals(2, declarations.definedTypes[1].recursiveType.subTypes.size)
        assertContentEquals(intArrayOf(0), declarations.functionTypeIndices)
        assertEquals(
            listOf(
                TableDeclaration(
                    TableType(AddressType.I32, ReferenceType.RefNull(AbstractHeapType.Func), Limits(1u, null)),
                    ConstantExpression(listOf(ConstantInstruction.RefNull(AbstractHeapType.Func))),
                ),
            ),
            declarations.tables,
        )
        assertEquals(listOf(MemoryType(AddressType.I32, Limits(1u, 2u), SharedStatus.Unshared)), declarations.memories)
        assertEquals(
            listOf(
                GlobalDeclaration(
                    GlobalType(ValueType.Number(NumberType.I64), Mutability.Var),
                    ConstantExpression(listOf(ConstantInstruction.I64Const(7))),
                ),
            ),
            declarations.globals,
        )
        assertEquals(
            listOf(Export(NameValue("run"), Export.Descriptor.Function(Index.FunctionIndex(0u)))),
            declarations.exports,
        )
        assertEquals(
            listOf(
                ElementSegmentDeclaration(
                    ReferenceType.Ref(AbstractHeapType.Func),
                    listOf(ConstantExpression(listOf(ConstantInstruction.RefFunc(Index.FunctionIndex(0u))))),
                    ElementSegmentDeclaration.Mode.Active(
                        Index.TableIndex(0u),
                        ConstantExpression(listOf(ConstantInstruction.I32Const(2))),
                    ),
                ),
            ),
            declarations.elementSegments,
        )
        assertEquals(
            listOf(
                DataSegmentDeclaration(
                    DataPayload.External(0, 8),
                    DataSegmentDeclaration.Mode.Active(
                        Index.MemoryIndex(0u),
                        ConstantExpression(
                            listOf(
                                ConstantInstruction.I32Const(16),
                                ConstantInstruction.I32Const(4),
                                ConstantInstruction.I32Add,
                            ),
                        ),
                    ),
                ),
            ),
            declarations.dataSegments,
        )
        assertSame(ModuleNames.NONE, declarations.names)
    }

    @Test
    fun `a value left on the stack fails the build`() {
        val builder = DeclarationsBuilder()
        builder.i32Const(1)

        assertFailsWith<IllegalStateException> { builder.build(null) }
    }

    @Test
    fun `an int column unpacks signed leb values`() {
        val column = IntColumn()

        column.unpack("\u007FÀ\u0000ÿÿÿÿ\u0007")
        column.unpack("\u0080\u0080\u0080\u0080x")

        assertContentEquals(intArrayOf(-1, 64, Int.MAX_VALUE, Int.MIN_VALUE), column.toArray())
    }
}
