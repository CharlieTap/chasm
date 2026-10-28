package io.github.charlietap.chasm.fixture.executor.instantiator

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.declarations.ConstantExpression
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.DataPayload
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.declarations.ElementSegmentDeclaration
import io.github.charlietap.chasm.declarations.ExportDeclarations
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
    exports: ExportDeclarations = exportDeclarations(),
    startFunction: Int? = null,
    elementSegments: List<ElementSegmentDeclaration> = emptyList(),
    dataSegments: List<DataSegmentDeclaration> = emptyList(),
    names: ModuleNames = ModuleNames.NONE,
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
)

fun exportDeclarations(
    names: Array<String> = emptyArray(),
    kinds: ByteArray = ByteArray(0),
    indices: IntArray = IntArray(0),
) = ExportDeclarations(names, kinds, indices)

fun constantExpression(
    vararg instructions: ConstantInstruction,
) = ConstantExpression(instructions.toList())

fun elementSegmentDeclaration(
    type: ReferenceType = ReferenceType.RefNull(AbstractHeapType.Func),
    initializers: List<ConstantExpression> = emptyList(),
    mode: ElementSegmentDeclaration.Mode = ElementSegmentDeclaration.Mode.Passive,
) = ElementSegmentDeclaration(type, initializers, mode)

fun dataSegmentDeclaration(
    bytes: ByteArray = ByteArray(0),
    mode: DataSegmentDeclaration.Mode = DataSegmentDeclaration.Mode.Passive,
) = DataSegmentDeclaration(DataPayload.Inline(bytes), mode)
