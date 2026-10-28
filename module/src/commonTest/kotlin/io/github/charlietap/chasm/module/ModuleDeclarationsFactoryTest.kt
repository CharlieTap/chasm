package io.github.charlietap.chasm.module

import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.instruction.VectorInstruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.FunctionNameSubsection
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.LocalNameSubsection
import io.github.charlietap.chasm.ast.module.Memory
import io.github.charlietap.chasm.ast.module.ModuleNameSubsection
import io.github.charlietap.chasm.ast.module.NameData
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.module.Tag
import io.github.charlietap.chasm.ast.name.IndirectNameAssociation
import io.github.charlietap.chasm.ast.name.NameAssociation
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.declarations.ElementSegmentDeclaration
import io.github.charlietap.chasm.declarations.ExportDeclarations
import io.github.charlietap.chasm.declarations.GlobalDeclaration
import io.github.charlietap.chasm.declarations.InitializerExpression
import io.github.charlietap.chasm.declarations.InitializerInstruction
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.ModuleNames
import io.github.charlietap.chasm.declarations.TableDeclaration
import io.github.charlietap.chasm.fixture.ast.module.module
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.tableType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalUnsignedTypes::class)
class ModuleDeclarationsFactoryTest {

    @Test
    fun `every declaration is carried over in index order`() {
        val types = listOf(definedType(typeIndex = 0), definedType(typeIndex = 1))
        val functionImport = Import(NameValue("env"), NameValue("f"), Import.Descriptor.Function(Index.TypeIndex(1u)))
        val memoryImport = Import(NameValue("env"), NameValue("m"), Import.Descriptor.Memory(memoryType()))
        val table = tableType()
        val memory = memoryType()
        val tag = tagType()
        val global = globalType()
        val bytes = ubyteArrayOf(1u, 2u, 255u)
        val module = module(
            definedTypes = types,
            imports = listOf(functionImport, memoryImport),
            functions = listOf(function(index = 1, typeIndex = 0), function(index = 2, typeIndex = 1)),
            tables = listOf(Table(Index.TableIndex(0u), table, expression(ReferenceInstruction.RefNull(AbstractHeapType.Func)))),
            memories = listOf(Memory(Index.MemoryIndex(1u), memory)),
            tags = listOf(Tag(Index.TagIndex(0u), tag)),
            globals = listOf(Global(Index.GlobalIndex(0u), global, expression(NumericInstruction.I32Const(7)))),
            exports = listOf(
                Export(NameValue("run"), Export.Descriptor.Function(Index.FunctionIndex(2u))),
                Export(NameValue("table"), Export.Descriptor.Table(Index.TableIndex(0u))),
                Export(NameValue("memory"), Export.Descriptor.Memory(Index.MemoryIndex(0u))),
                Export(NameValue("global"), Export.Descriptor.Global(Index.GlobalIndex(0u))),
                Export(NameValue("tag"), Export.Descriptor.Tag(Index.TagIndex(0u))),
            ),
            startFunction = StartFunction(Index.FunctionIndex(1u)),
            elementSegments = listOf(
                ElementSegment(
                    Index.ElementIndex(0u),
                    ReferenceType.Ref(AbstractHeapType.Func),
                    listOf(expression(ReferenceInstruction.RefFunc(Index.FunctionIndex(2u)))),
                    ElementSegment.Mode.Active(Index.TableIndex(0u), expression(NumericInstruction.I32Const(3))),
                ),
                ElementSegment(Index.ElementIndex(1u), ReferenceType.Ref(AbstractHeapType.Func), emptyList(), ElementSegment.Mode.Passive),
                ElementSegment(
                    Index.ElementIndex(2u),
                    ReferenceType.Ref(AbstractHeapType.Func),
                    emptyList(),
                    ElementSegment.Mode.Declarative,
                ),
            ),
            dataSegments = listOf(
                DataSegment(
                    Index.DataIndex(0u),
                    bytes,
                    DataSegment.Mode.Active(Index.MemoryIndex(1u), expression(VariableInstruction.GlobalGet(Index.GlobalIndex(0u)))),
                ),
                DataSegment(Index.DataIndex(1u), ubyteArrayOf(), DataSegment.Mode.Passive),
            ),
        )

        val expected = ModuleDeclarations(
            definedTypes = types,
            imports = listOf(functionImport, memoryImport),
            functionTypeIndices = intArrayOf(0, 1),
            tables = listOf(TableDeclaration(table, initializer(InitializerInstruction.RefNull(AbstractHeapType.Func)))),
            memories = listOf(memory),
            tags = listOf(tag),
            globals = listOf(GlobalDeclaration(global, initializer(InitializerInstruction.I32Const(7)))),
            exports = ExportDeclarations(
                names = arrayOf("run", "table", "memory", "global", "tag"),
                kinds = byteArrayOf(
                    ExportDeclarations.FUNCTION,
                    ExportDeclarations.TABLE,
                    ExportDeclarations.MEMORY,
                    ExportDeclarations.GLOBAL,
                    ExportDeclarations.TAG,
                ),
                indices = intArrayOf(2, 0, 0, 0, 0),
            ),
            startFunction = 1,
            elementSegments = listOf(
                ElementSegmentDeclaration(
                    ReferenceType.Ref(AbstractHeapType.Func),
                    listOf(initializer(InitializerInstruction.RefFunc(2))),
                    ElementSegmentDeclaration.Mode.Active(0, initializer(InitializerInstruction.I32Const(3))),
                ),
                ElementSegmentDeclaration(ReferenceType.Ref(AbstractHeapType.Func), emptyList(), ElementSegmentDeclaration.Mode.Passive),
                ElementSegmentDeclaration(
                    ReferenceType.Ref(AbstractHeapType.Func),
                    emptyList(),
                    ElementSegmentDeclaration.Mode.Declarative,
                ),
            ),
            dataSegments = listOf(
                DataSegmentDeclaration(
                    DataPayload.Inline(byteArrayOf(1, 2, -1)),
                    DataSegmentDeclaration.Mode.Active(1, initializer(InitializerInstruction.GlobalGet(0))),
                ),
                DataSegmentDeclaration(DataPayload.Inline(byteArrayOf()), DataSegmentDeclaration.Mode.Passive),
            ),
            names = ModuleNames.NONE,
        )

        val actual = ModuleDeclarationsFactory(module)

        assertEquals(expected, actual)
        assertSame(types, actual.definedTypes)
    }

    @Test
    fun `every constant instruction becomes its initializer instruction`() {
        val instructions = listOf(
            NumericInstruction.I32Const(-1),
            NumericInstruction.I64Const(Long.MIN_VALUE),
            NumericInstruction.F32Const(-0.0f, (-0.0f).toRawBits()),
            NumericInstruction.F64Const(Double.NaN, 0x7FF4000000000001),
            NumericInstruction.I32Add,
            NumericInstruction.I32Sub,
            NumericInstruction.I32Mul,
            NumericInstruction.I64Add,
            NumericInstruction.I64Sub,
            NumericInstruction.I64Mul,
            VectorInstruction.V128Const(ByteArray(16) { index -> (index + 1).toByte() }),
            VariableInstruction.GlobalGet(Index.GlobalIndex(4u)),
            ReferenceInstruction.RefNull(AbstractHeapType.Extern),
            ReferenceInstruction.RefFunc(Index.FunctionIndex(5u)),
            AggregateInstruction.StructNew(Index.TypeIndex(6u)),
            AggregateInstruction.StructNewDefault(Index.TypeIndex(7u)),
            AggregateInstruction.ArrayNew(Index.TypeIndex(8u)),
            AggregateInstruction.ArrayNewDefault(Index.TypeIndex(9u)),
            AggregateInstruction.ArrayNewFixed(Index.TypeIndex(10u), 11u),
            AggregateInstruction.RefI31,
            AggregateInstruction.AnyConvertExtern,
            AggregateInstruction.ExternConvertAny,
        )
        val expected = initializer(
            InitializerInstruction.I32Const(-1),
            InitializerInstruction.I64Const(Long.MIN_VALUE),
            InitializerInstruction.F32Const((-0.0f).toRawBits()),
            InitializerInstruction.F64Const(0x7FF4000000000001),
            InitializerInstruction.I32Add,
            InitializerInstruction.I32Sub,
            InitializerInstruction.I32Mul,
            InitializerInstruction.I64Add,
            InitializerInstruction.I64Sub,
            InitializerInstruction.I64Mul,
            InitializerInstruction.V128Const(low = 0x0807060504030201, high = 0x100F0E0D0C0B0A09),
            InitializerInstruction.GlobalGet(4),
            InitializerInstruction.RefNull(AbstractHeapType.Extern),
            InitializerInstruction.RefFunc(5),
            InitializerInstruction.StructNew(6),
            InitializerInstruction.StructNewDefault(7),
            InitializerInstruction.ArrayNew(8),
            InitializerInstruction.ArrayNewDefault(9),
            InitializerInstruction.ArrayNewFixed(10, 11),
            InitializerInstruction.RefI31,
            InitializerInstruction.AnyConvertExtern,
            InitializerInstruction.ExternConvertAny,
        )
        val module = module(globals = listOf(Global(Index.GlobalIndex(0u), globalType(), Expression(instructions))))

        val actual = ModuleDeclarationsFactory(module).globals.single().initializer

        assertEquals(expected, actual)
    }

    @Test
    fun `instructions no constant expression may hold are left out`() {
        val expression = Expression(listOf(NumericInstruction.I32Const(1), ControlInstruction.Nop, NumericInstruction.I32Const(2)))
        val module = module(globals = listOf(Global(Index.GlobalIndex(0u), globalType(), expression)))

        val actual = ModuleDeclarationsFactory(module).globals.single().initializer

        assertEquals(initializer(InitializerInstruction.I32Const(1), InitializerInstruction.I32Const(2)), actual)
    }

    @Test
    fun `names come from the first subsection of each kind, the first name of an index winning`() {
        val module = module(
            functions = listOf(function(index = 0, typeIndex = 0), function(index = 1, typeIndex = 0)),
            exports = listOf(Export(NameValue("run"), Export.Descriptor.Function(Index.FunctionIndex(1u)))),
            customs = listOf(
                NameData(
                    listOf(
                        ModuleNameSubsection(NameValue("guest")),
                        FunctionNameSubsection(
                            listOf(
                                NameAssociation(1u, NameValue("run")),
                                NameAssociation(0u, NameValue("helper")),
                                NameAssociation(1u, NameValue("ignored")),
                            ),
                        ),
                        FunctionNameSubsection(listOf(NameAssociation(0u, NameValue("ignored")))),
                        LocalNameSubsection(
                            listOf(
                                IndirectNameAssociation(0u, listOf(NameAssociation(0u, NameValue("unexported")))),
                                IndirectNameAssociation(1u, listOf(NameAssociation(1u, NameValue("b")), NameAssociation(0u, NameValue("a")))),
                            ),
                        ),
                    ),
                ),
                NameData(listOf(ModuleNameSubsection(NameValue("ignored")))),
            ),
        )
        val expected = ModuleNames(
            moduleName = "guest",
            functionNameIndices = intArrayOf(0, 1),
            functionNames = arrayOf("helper", "run"),
            exportedLocalNames = mapOf(1 to listOf("b", "a")),
            trapNameIndices = intArrayOf(0, 1),
            trapNames = arrayOf("ignored", "ignored"),
        )

        val actual = ModuleDeclarationsFactory(module).names

        assertEquals(expected, actual)
        assertEquals("run", actual.functionName(1))
        assertEquals("ignored", actual.trapFunctionName(1))
        assertEquals(null, actual.functionName(2))
        assertEquals(null, actual.trapFunctionName(2))
    }

    private fun function(
        index: Int,
        typeIndex: Int,
    ) = Function(
        idx = Index.FunctionIndex(index.toUInt()),
        typeIndex = Index.TypeIndex(typeIndex.toUInt()),
        locals = emptyList(),
        body = Expression.EMPTY,
    )

    private fun expression(
        vararg instructions: Instruction,
    ) = Expression(instructions.toList())

    private fun initializer(
        vararg instructions: InitializerInstruction,
    ) = InitializerExpression(instructions.toList())
}
