package io.github.charlietap.chasm.fixture.executor.instantiator

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.declarations.ConstantExpression
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.declarations.DataSource
import io.github.charlietap.chasm.declarations.ElementSegmentDeclaration
import io.github.charlietap.chasm.declarations.GlobalDeclaration
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.declarations.ModuleNames
import io.github.charlietap.chasm.declarations.TableDeclaration
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.TagType

fun moduleDeclarations(
    definedTypes: List<DefinedType> = emptyList(),
    imports: List<Import> = emptyList(),
    functionTypeIndices: IntArray = IntArray(0),
    tables: List<TableDeclaration> = emptyList(),
    memories: List<MemoryType> = emptyList(),
    tags: List<TagType> = emptyList(),
    globals: List<GlobalDeclaration> = emptyList(),
    exports: List<Export> = emptyList(),
    startFunction: Index.FunctionIndex? = null,
    elementSegments: List<ElementSegmentDeclaration> = emptyList(),
    dataSegments: List<DataSegmentDeclaration> = emptyList(),
    names: ModuleNames = ModuleNames.NONE,
    dataSource: DataSource? = null,
) = ModuleDeclarations(
    definedTypes = definedTypes,
    imports = imports,
    functionTypeIndices = functionTypeIndices,
    tables = tables,
    memories = memories,
    tags = tags,
    globals = globals,
    exports = exports,
    startFunction = startFunction,
    elementSegments = elementSegments,
    dataSegments = dataSegments,
    names = names,
    dataSource = dataSource,
)

fun constantExpression(
    vararg instructions: ConstantInstruction,
) = ConstantExpression(instructions.toList())

fun elementSegmentDeclaration(
    type: ReferenceType = ReferenceType.RefNull(AbstractHeapType.Func),
    initExpressions: List<ConstantExpression> = emptyList(),
    mode: ElementSegmentDeclaration.Mode = ElementSegmentDeclaration.Mode.Passive,
) = ElementSegmentDeclaration(type, initExpressions, mode)

fun dataSegmentDeclaration(
    bytes: ByteArray = ByteArray(0),
    mode: DataSegmentDeclaration.Mode = DataSegmentDeclaration.Mode.Passive,
) = DataSegmentDeclaration(DataPayload.Inline(bytes), mode)
