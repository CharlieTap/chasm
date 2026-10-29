package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.cir.GlobalIndex
import io.github.charlietap.chasm.cir.TagIndex
import io.github.charlietap.chasm.cir.TypeIndex
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.runtime.ext.defaultBits
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.functionType
import io.github.charlietap.chasm.type.ext.structType

class ModuleIndexSpaces(
    module: Module,
) {

    private val definedTypes = module.definedTypes

    private val functionTypeIndices: List<Int> = module.imports.mapNotNull { import ->
        (import.descriptor as? Import.Descriptor.Function)?.typeIndex?.toInt()
    } + module.functions.map { function -> function.typeIndex.toInt() }

    private val globalTypes: List<ValueType> = module.imports.mapNotNull { import ->
        (import.descriptor as? Import.Descriptor.Global)?.type?.valueType
    } + module.globals.map { global -> global.type.valueType }

    private val tagTypes: List<FunctionType> = module.imports.mapNotNull { import ->
        (import.descriptor as? Import.Descriptor.Tag)?.type?.functionType
    } + module.tags.map { tag -> tag.type.functionType }

    val importedFunctionCount: Int = module.imports.count { import -> import.descriptor is Import.Descriptor.Function }

    val memoryCount: Int = module.imports.count { import -> import.descriptor is Import.Descriptor.Memory } + module.memories.size

    fun isImported(
        index: FunctionIndex,
    ): Boolean = index.index < importedFunctionCount

    fun functionType(
        index: FunctionIndex,
    ): FunctionType = functionType(TypeIndex(functionTypeIndices[index.index]))

    fun functionType(
        index: TypeIndex,
    ): FunctionType =
        checkNotNull(definedTypes[index.index].functionType()) { "type ${index.index} is not a function type" }

    fun globalType(
        index: GlobalIndex,
    ): ValueType = globalTypes[index.index]

    fun tagFieldCount(
        index: TagIndex,
    ): Int = tagTypes[index.index].params.types.size

    fun structFields(
        index: TypeIndex,
    ): List<FieldType> =
        checkNotNull(definedTypes[index.index].structType()) { "type ${index.index} is not a struct type" }.fields

    fun structFieldType(
        index: TypeIndex,
        field: Int,
    ): ValueType = structFields(index)[field].valueType()

    fun structDefaults(
        index: TypeIndex,
    ): Resource.Longs = structDefaultResources[index.index] ?: Resource.Longs(
        structFields(index).let { fields -> LongArray(fields.size) { field -> fields[field].requireDefault() } },
    ).also { defaults -> structDefaultResources[index.index] = defaults }

    private val structDefaultResources = arrayOfNulls<Resource.Longs>(definedTypes.size)

    fun arrayElement(
        index: TypeIndex,
    ): FieldType =
        checkNotNull(definedTypes[index.index].arrayType()) { "type ${index.index} is not an array type" }.fieldType

    fun arrayElementType(
        index: TypeIndex,
    ): ValueType = arrayElement(index).valueType()

    fun arrayDefault(
        index: TypeIndex,
    ): Long = arrayElement(index).requireDefault()

    private fun FieldType.requireDefault(): Long = checkNotNull(defaultBits()) { "field type $this has no default value" }

    private fun FieldType.valueType(): ValueType = when (val storage = storageType) {
        is StorageType.Packed -> ValueType.Number(NumberType.I32)
        is StorageType.Value -> storage.type
    }
}
