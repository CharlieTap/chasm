package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.TagType

class ModuleDeclarations(
    val definedTypes: List<DefinedType>,
    val imports: List<Import>,
    val functionTypeIndices: IntArray,
    val tables: List<TableDeclaration>,
    val memories: List<MemoryType>,
    val tags: List<TagType>,
    val globals: List<GlobalDeclaration>,
    val exports: ExportDeclarations,
    val startFunction: Int?,
    val elementSegments: List<ElementSegmentDeclaration>,
    val dataSegments: List<DataSegmentDeclaration>,
    val names: ModuleNames,
) {

    override fun equals(
        other: Any?,
    ): Boolean {
        if (this === other) return true
        if (other !is ModuleDeclarations) return false
        return definedTypes == other.definedTypes &&
            imports == other.imports &&
            functionTypeIndices.contentEquals(other.functionTypeIndices) &&
            tables == other.tables &&
            memories == other.memories &&
            tags == other.tags &&
            globals == other.globals &&
            exports == other.exports &&
            startFunction == other.startFunction &&
            elementSegments == other.elementSegments &&
            dataSegments == other.dataSegments &&
            names == other.names
    }

    override fun hashCode(): Int {
        var result = definedTypes.hashCode()
        result = 31 * result + imports.hashCode()
        result = 31 * result + functionTypeIndices.contentHashCode()
        result = 31 * result + tables.hashCode()
        result = 31 * result + memories.hashCode()
        result = 31 * result + tags.hashCode()
        result = 31 * result + globals.hashCode()
        result = 31 * result + exports.hashCode()
        result = 31 * result + (startFunction ?: -1)
        result = 31 * result + elementSegments.hashCode()
        result = 31 * result + dataSegments.hashCode()
        result = 31 * result + names.hashCode()
        return result
    }
}
