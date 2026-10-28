package io.github.charlietap.chasm.compiler.frontend.context

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.cir.DataIndex
import io.github.charlietap.chasm.cir.ElementIndex
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.GlobalIndex
import io.github.charlietap.chasm.cir.MemoryIndex
import io.github.charlietap.chasm.cir.TableIndex
import io.github.charlietap.chasm.cir.TagIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.TagType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.type.ext.structType

internal fun CompilerContext.function(
    index: Index.FunctionIndex,
): FunctionIndex = FunctionIndex(index.toInt())

internal fun CompilerContext.functionType(
    index: Index.FunctionIndex,
): FunctionType = functionType(index.toInt())

internal fun CompilerContext.isDefined(
    index: Index.FunctionIndex,
): Boolean = index.toInt() >= importedFunctionCount

internal fun CompilerContext.table(
    index: Index.TableIndex,
): TableIndex = TableIndex(index.toInt())

internal fun CompilerContext.tableType(
    index: Index.TableIndex,
): TableType = tableTypes[index.toInt()]

internal fun CompilerContext.memory(
    index: Index.MemoryIndex,
): MemoryIndex = MemoryIndex(index.toInt())

internal fun CompilerContext.tag(
    index: Index.TagIndex,
): TagIndex = TagIndex(index.toInt())

internal fun CompilerContext.tagType(
    index: Index.TagIndex,
): TagType = tagTypes[index.toInt()]

internal fun CompilerContext.global(
    index: Index.GlobalIndex,
): GlobalIndex = GlobalIndex(index.toInt())

internal fun CompilerContext.globalType(
    index: Index.GlobalIndex,
): GlobalType = globalTypes[index.toInt()]

internal fun CompilerContext.element(
    index: Index.ElementIndex,
): ElementIndex = ElementIndex(index.toInt())

internal fun CompilerContext.data(
    index: Index.DataIndex,
): DataIndex = DataIndex(index.toInt())

internal fun CompilerContext.type(
    index: Index.TypeIndex,
): TypeIndex = TypeIndex(index.toInt())

internal fun CompilerContext.arrayType(
    index: Index.TypeIndex,
): ArrayType =
    module.definedTypes[index.toInt()].asSubType.compositeType.arrayType()
        ?: error("type ${index.idx} is not an array type")

internal fun CompilerContext.structType(
    index: Index.TypeIndex,
): StructType =
    module.definedTypes[index.toInt()].asSubType.compositeType.structType()
        ?: error("type ${index.idx} is not a struct type")
