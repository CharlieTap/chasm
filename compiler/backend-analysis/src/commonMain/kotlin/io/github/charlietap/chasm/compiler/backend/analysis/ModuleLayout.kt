package io.github.charlietap.chasm.compiler.backend.analysis

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.functionType
import java.security.MessageDigest

object ModuleLayout {

    fun hash(
        module: Module,
    ): String {
        val text = buildString {
            module.definedTypes.forEach { type -> append("type ").append(composite(type.asSubType.compositeType)).append('\n') }
            module.imports.forEach { import ->
                append("import ").append(import.moduleName.name).append(' ').append(import.entityName.name).append(' ')
                when (val descriptor = import.descriptor) {
                    is Import.Descriptor.Function -> append("func ").append(function(module, descriptor.typeIndex.toInt()))
                    is Import.Descriptor.Table -> append("table ").append(reference(descriptor.type.referenceType))
                    is Import.Descriptor.Memory -> append("memory")
                    is Import.Descriptor.Global -> append("global ").append(global(descriptor.type))
                    is Import.Descriptor.Tag -> append("tag ").append(function(descriptor.type.functionType))
                }
                append('\n')
            }
            module.functions.forEach { function -> append("func ").append(function(module, function.typeIndex.toInt())).append('\n') }
            module.tables.forEach { table -> append("table ").append(reference(table.type.referenceType)).append('\n') }
            module.tags.forEach { tag -> append("tag ").append(function(tag.type.functionType)).append('\n') }
            module.globals.forEach { global -> append("global ").append(global(global.type)).append('\n') }
            append("memories=").append(module.memories.size)
            append(" elements=").append(module.elementSegments.size).append(" data=").append(module.dataSegments.size)
        }
        return sha256(text.encodeToByteArray())
    }

    private fun sha256(
        bytes: ByteArray,
    ): String =
        MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }

    private fun function(
        module: Module,
        typeIndex: Int,
    ): String {
        val type: FunctionType = module.definedTypes.getOrNull(typeIndex)?.functionType() ?: return "?$typeIndex"
        return function(type)
    }

    private fun function(
        type: FunctionType,
    ): String =
        type.params.types.joinToString(",", "(", ")", transform = ::value) +
            type.results.types.joinToString(",", "(", ")", transform = ::value)

    private fun composite(
        type: CompositeType,
    ): String = when (type) {
        is CompositeType.Function -> "func " + function(type.functionType)
        is CompositeType.Struct -> type.structType.fields.joinToString(",", "struct (", ")", transform = ::field)
        is CompositeType.Array -> "array " + field(type.arrayType.fieldType)
    }

    private fun field(
        type: FieldType,
    ): String {
        val storage = when (val storageType = type.storageType) {
            is StorageType.Value -> value(storageType.type)
            is StorageType.Packed -> when (storageType.type) {
                PackedType.I8 -> "i8"
                PackedType.I16 -> "i16"
            }
        }
        return mutability(type.mutability) + storage
    }

    private fun global(
        type: GlobalType,
    ): String = mutability(type.mutability) + value(type.valueType)

    private fun mutability(
        mutability: Mutability,
    ): String = when (mutability) {
        Mutability.Const -> ""
        Mutability.Var -> "mut "
    }

    private fun value(
        type: ValueType,
    ): String = when (type) {
        is ValueType.Number -> when (type.numberType) {
            NumberType.I32 -> "i32"
            NumberType.I64 -> "i64"
            NumberType.F32 -> "f32"
            NumberType.F64 -> "f64"
        }
        is ValueType.Vector -> "v128"
        is ValueType.Bottom -> "bottom"
        is ValueType.Reference -> reference(type.referenceType)
    }

    private fun reference(
        type: ReferenceType,
    ): String =
        (if (type is ReferenceType.RefNull) "refnull " else "ref ") + heap(type.heapType)

    private fun heap(
        type: HeapType,
    ): String = when (type) {
        AbstractHeapType.Func -> "func"
        AbstractHeapType.NoFunc -> "nofunc"
        AbstractHeapType.Extern -> "extern"
        AbstractHeapType.NoExtern -> "noextern"
        AbstractHeapType.Exception -> "exn"
        AbstractHeapType.NoException -> "noexn"
        AbstractHeapType.Any -> "any"
        AbstractHeapType.Eq -> "eq"
        AbstractHeapType.Struct -> "struct"
        AbstractHeapType.Array -> "array"
        AbstractHeapType.I31 -> "i31"
        AbstractHeapType.None -> "none"
        is AbstractHeapType.Bottom -> "bottom"
        is ConcreteHeapType.TypeIndex -> "t${type.index}"
        is ConcreteHeapType.Defined -> "t${type.definedType.typeIndex}"
        is ConcreteHeapType.RecursiveTypeIndex -> "r${type.index}"
    }
}
