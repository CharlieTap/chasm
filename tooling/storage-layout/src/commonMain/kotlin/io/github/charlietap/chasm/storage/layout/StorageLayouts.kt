package io.github.charlietap.chasm.storage.layout

import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_INTS_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_REFERENCES_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.binaryName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.FieldStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.Lane
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.RttKeys
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.analysis.gc.UnsupportedShape
import io.github.charlietap.chasm.compiler.backend.analysis.gc.resolveDefinedType
import io.github.charlietap.chasm.compiler.backend.analysis.gc.subType
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.structType
import io.github.charlietap.chasm.validator.WasmModuleValidator

fun storageLayouts(module: ByteArray): ChasmResult<StorageLayouts, ModuleError> {

    val config = ModuleConfig()
    val decoded = WasmModuleDecoder(config, module).getOrElse { error ->
        return ChasmResult.Error(ModuleError.Malformed(error.toString()))
    }
    val validated = WasmModuleValidator(config, decoded).getOrElse { error ->
        return ChasmResult.Error(ModuleError.Invalid(error.toString()))
    }

    return ChasmResult.Success(storageLayouts(validated.definedTypes))
}

fun storageLayouts(types: List<DefinedType>): StorageLayouts {
    return StorageLayouts(types, StoragePlans(types, STORAGE_FAMILY), RttKeys(types))
}

class StorageLayouts internal constructor(
    private val types: List<DefinedType>,
    private val plans: StoragePlans,
    private val keys: RttKeys,
) {
    val runtimeAbi: Int = COMPILED_RUNTIME_ABI

    fun struct(typeIndex: Int): StructLayout? {

        val type = types.getOrNull(typeIndex) ?: return null
        if (type.structType() == null) {
            return null
        }
        val storage = try {
            plans.struct(typeIndex)
        } catch (_: UnsupportedShape) {
            return null
        }

        return StructLayout(
            typeIndex = typeIndex,
            check = typeCheck(type),
            storageClass = binaryName(storage.className),
            fields = storage.fields.map { field -> fieldLayout(type, field) },
        )
    }

    fun array(typeIndex: Int): ArrayLayout? {

        val type = types.getOrNull(typeIndex) ?: return null
        if (type.arrayType() == null) {
            return null
        }
        val storage = try {
            plans.array(typeIndex)
        } catch (_: UnsupportedShape) {
            return null
        }

        return ArrayLayout(typeIndex, typeCheck(type), binaryName(storage.className))
    }

    private fun typeCheck(type: DefinedType): TypeCheck {

        val ancestry = generateSequence(type) { subtype ->
            subtype.subType().superTypes.singleOrNull()?.let { supertype -> resolveDefinedType(supertype, types, subtype) }
        }

        return TypeCheck(
            rttKey = keys.key(type),
            supertypes = ancestry.count() - 1,
            final = type.subType() is SubType.Final,
        )
    }

    private fun fieldLayout(
        owner: DefinedType,
        field: FieldStorage,
    ): FieldLayout {

        val lanes = field.lanes
        val slots = when {
            lanes.size == 2 -> WideNumberSlots(slot(lanes[0]), slot(lanes[1]))
            lanes[0].kind == LaneKind.INT -> NumberSlot(slot(lanes[0]), packedType(field.type))
            else -> ReferenceSlot(slot(lanes[0]), referenceCheck(owner, field.type))
        }

        return FieldLayout(field.type, slots)
    }

    private fun packedType(type: FieldType): PackedType? {
        return (type.storageType as? StorageType.Packed)?.type
    }

    private fun referenceCheck(
        owner: DefinedType,
        type: FieldType,
    ): TypeCheck? {

        val reference = ((type.storageType as? StorageType.Value)?.type as? ValueType.Reference) ?: return null
        val heapType = reference.referenceType.heapType
        if (heapType is AbstractHeapType) {
            return null
        }

        val referenced = resolveDefinedType(heapType, types, owner)
        if (referenced.structType() == null && referenced.arrayType() == null) {
            return null
        }

        return typeCheck(referenced)
    }

    private fun slot(lane: Lane): Slot {
        return when {
            !lane.spilled -> InlineSlot(lane.fieldName())
            lane.kind == LaneKind.INT -> SpilledSlot(SPILLED_INTS_FIELD, lane.index)
            else -> SpilledSlot(SPILLED_REFERENCES_FIELD, lane.index)
        }
    }
}
