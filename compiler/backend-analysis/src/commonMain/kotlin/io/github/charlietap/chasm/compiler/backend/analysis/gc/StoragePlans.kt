package io.github.charlietap.chasm.compiler.backend.analysis.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType

@InternalChasmApi
data class FieldStorage(
    val type: FieldType,
    val lanes: List<Lane>,
)

@InternalChasmApi
data class StructStorage(
    val className: String,
    val fields: List<FieldStorage>,
    val inlineLanes: Int,
    val final: Boolean,
) {

    val spilledInts: Int = spilledLanes(LaneKind.INT)

    val spilledReferences: Int = spilledLanes(LaneKind.REFERENCE)

    private fun spilledLanes(kind: LaneKind): Int {
        return fields.sumOf { field -> field.lanes.count { lane -> lane.spilled && lane.kind == kind } }
    }
}

@InternalChasmApi
data class ArrayStorage(
    val className: String,
    val elementShift: Int,
)

@InternalChasmApi
class StoragePlans(
    private val types: List<DefinedType>,
    private val family: StorageFamily,
) {
    private val keys = RttKeys(types)
    private val structsByKey = HashMap<String, StructStorage>()
    private val structsByIndex = arrayOfNulls<StructStorage>(types.size)
    private val arraysByIndex = arrayOfNulls<ArrayStorage>(types.size)
    private val unsupportedByIndex = arrayOfNulls<String>(types.size)
    private val emptyStructClass = family.roots.first { root -> root.intLanes == 1 && root.referenceLanes == 0 }
    private val ranking = compareBy<Placement> { placement -> placement.unpaddedBytes }
        .thenByDescending { placement -> placement.inlineFields }
        .thenByDescending { placement -> placement.inlineComponents }
        .thenBy { placement -> placement.unusedIntLanes }
        .thenBy { placement -> placement.superclasses }
        .thenBy { placement -> placement.order }

    init {
        types.forEachIndexed { index, type ->
            try {
                when (val composite = type.subType().compositeType) {
                    is CompositeType.Struct -> structsByIndex[index] = struct(type)
                    is CompositeType.Array -> arraysByIndex[index] = array(composite)
                    is CompositeType.Function -> Unit
                }
            } catch (unsupported: UnsupportedShape) {
                unsupportedByIndex[index] = unsupported.message.orEmpty()
            }
        }
    }

    fun struct(index: Int): StructStorage {
        requireSupported(index)
        return structsByIndex[index] ?: error("type $index is not a struct type")
    }

    fun array(index: Int): ArrayStorage {
        requireSupported(index)
        return arraysByIndex[index] ?: error("type $index is not an array type")
    }

    fun laneOwner(
        storage: StructStorage,
        lane: Lane,
    ): String {
        return family.storageClass(storage.className).declaringClass(lane).name
    }

    private fun requireSupported(index: Int) {
        val reason = unsupportedByIndex[index]
        if (reason != null) {
            throw UnsupportedShape(reason)
        }
    }

    private fun struct(type: DefinedType): StructStorage {

        val key = keys.key(type)
        structsByKey[key]?.let { storage -> return storage }

        val fields = (type.subType().compositeType as CompositeType.Struct).structType.fields
        val kinds = fields.map(::laneKinds)
        val parent = supertype(type)?.let(::struct)
        val storage = if (parent == null && fields.isEmpty()) {
            StructStorage(emptyStructClass.name, emptyList(), inlineLanes(emptyStructClass), emptyStructClass.final)
        } else {
            place(parent, fields, kinds)
        }
        structsByKey[key] = storage

        return storage
    }

    private fun place(
        parent: StructStorage?,
        fields: List<FieldType>,
        kinds: List<List<LaneKind>>,
    ): StructStorage {

        val inherited = parent?.fields.orEmpty()
        val inheritedLanes = inherited.flatMap { field -> field.lanes }
        val addedFieldKinds = kinds.subList(inherited.size, kinds.size)
        val parentClass = parent?.let { storage -> family.storageClass(storage.className) }

        val placements = ArrayList<Placement>()
        val roots = parentClass?.let(::listOf) ?: family.roots
        for (root in roots) {
            search(root, inheritedLanes, addedFieldKinds, parentClass?.final == true, placements)
        }
        val best = placements.minWith(ranking)

        val placedFields = fields.mapIndexed { index, field ->
            val lanes = if (index < inherited.size) inherited[index].lanes else best.lanes[index - inherited.size]
            FieldStorage(field, lanes)
        }

        return StructStorage(best.storageClass.name, placedFields, inlineLanes(best.storageClass), best.storageClass.final)
    }

    private fun inlineLanes(storageClass: StorageClass): Int {
        return storageClass.intLanes + storageClass.referenceLanes
    }

    private fun search(
        storageClass: StorageClass,
        inheritedLanes: List<Lane>,
        addedFieldKinds: List<List<LaneKind>>,
        spillingInherited: Boolean,
        placements: MutableList<Placement>,
    ) {

        val placement = placement(storageClass, inheritedLanes, addedFieldKinds, spillingInherited, placements.size)

        if (placement != null) {
            placements.add(placement)
        } else {
            for (subclass in storageClass.subclasses) {
                search(subclass, inheritedLanes, addedFieldKinds, spillingInherited, placements)
            }
        }
    }

    private fun placement(
        storageClass: StorageClass,
        inheritedLanes: List<Lane>,
        addedFieldKinds: List<List<LaneKind>>,
        spillingInherited: Boolean,
        order: Int,
    ): Placement? {

        val inlineCounts = IntArray(LaneKind.entries.size)
        val spillCounts = IntArray(LaneKind.entries.size)
        for (lane in inheritedLanes) {
            if (lane.spilled) {
                spillCounts[lane.kind.ordinal]++
            } else {
                inlineCounts[lane.kind.ordinal]++
            }
        }

        var spilling = spillingInherited
        val lanes = addedFieldKinds.map { fieldKinds ->
            fieldKinds.map { kind ->
                val capacity = if (kind == LaneKind.INT) storageClass.intLanes else storageClass.referenceLanes
                if (!spilling && inlineCounts[kind.ordinal] < capacity) {
                    Lane(kind, inlineCounts[kind.ordinal]++, spilled = false)
                } else if (storageClass.final) {
                    spilling = true
                    Lane(kind, spillCounts[kind.ordinal]++, spilled = true)
                } else {
                    return null
                }
            }
        }

        return Placement(
            storageClass = storageClass,
            lanes = lanes,
            unpaddedBytes = unpaddedBytes(storageClass, spillCounts[LaneKind.INT.ordinal], spillCounts[LaneKind.REFERENCE.ordinal]),
            inlineFields = lanes.count { fieldLanes -> fieldLanes.none { lane -> lane.spilled } },
            inlineComponents = lanes.sumOf { fieldLanes -> fieldLanes.count { lane -> !lane.spilled } },
            unusedIntLanes = storageClass.intLanes - inlineCounts[LaneKind.INT.ordinal],
            superclasses = superclasses(storageClass),
            order = order,
        )
    }

    private fun unpaddedBytes(
        storageClass: StorageClass,
        spilledInts: Int,
        spilledReferences: Int,
    ): Int {

        val spillFields = if (storageClass.final) SPILL_ARRAY_FIELD_BYTES else 0
        val lanes = storageClass.intLanes + storageClass.referenceLanes

        return LANE_BYTES * lanes + spillFields + unpaddedArrayBytes(spilledInts) + unpaddedArrayBytes(spilledReferences)
    }

    private fun unpaddedArrayBytes(length: Int): Int {
        return if (length == 0) 0 else ARRAY_BYTES + LANE_BYTES * length
    }

    private fun superclasses(storageClass: StorageClass): Int {
        return generateSequence(storageClass.superclass) { superclass -> superclass.superclass }.count()
    }

    private fun laneKinds(field: FieldType): List<LaneKind> {
        return when (val storage = field.storageType) {
            is StorageType.Packed -> ONE_INT
            is StorageType.Value -> when (val value = storage.type) {
                is ValueType.Number -> if (value.numberType == NumberType.I64 || value.numberType == NumberType.F64) TWO_INTS else ONE_INT
                is ValueType.Reference, is ValueType.Bottom -> ONE_REFERENCE
                is ValueType.Vector -> throw UnsupportedShape("v128 fields cannot be stored in a struct")
            }
        }
    }

    private fun array(composite: CompositeType.Array): ArrayStorage {
        return when (val storage = composite.arrayType.fieldType.storageType) {
            is StorageType.Packed -> if (storage.type == PackedType.I8) I8_ARRAY else I16_ARRAY
            is StorageType.Value -> when (val value = storage.type) {
                is ValueType.Number -> when (value.numberType) {
                    NumberType.I32 -> I32_ARRAY
                    NumberType.I64 -> I64_ARRAY
                    NumberType.F32 -> F32_ARRAY
                    NumberType.F64 -> F64_ARRAY
                }
                is ValueType.Reference, is ValueType.Bottom -> REF_ARRAY
                is ValueType.Vector -> throw UnsupportedShape("v128 elements cannot be stored in an array")
            }
        }
    }

    private fun supertype(type: DefinedType): DefinedType? {

        val supertype = type.subType().superTypes.singleOrNull() ?: return null

        return resolveDefinedType(supertype, types, type)
    }

    private class Placement(
        val storageClass: StorageClass,
        val lanes: List<List<Lane>>,
        val unpaddedBytes: Int,
        val inlineFields: Int,
        val inlineComponents: Int,
        val unusedIntLanes: Int,
        val superclasses: Int,
        val order: Int,
    )

    private companion object {
        const val LANE_BYTES = 4
        const val SPILL_ARRAY_FIELD_BYTES = 8
        const val ARRAY_BYTES = 12

        val ONE_INT = listOf(LaneKind.INT)
        val TWO_INTS = listOf(LaneKind.INT, LaneKind.INT)
        val ONE_REFERENCE = listOf(LaneKind.REFERENCE)

        val I8_ARRAY = ArrayStorage("L${STORAGE_PACKAGE}I8Array;", ArrayElementKind.BYTE.shift)
        val I16_ARRAY = ArrayStorage("L${STORAGE_PACKAGE}I16Array;", ArrayElementKind.CHAR.shift)
        val I32_ARRAY = ArrayStorage("L${STORAGE_PACKAGE}I32Array;", ArrayElementKind.INT.shift)
        val I64_ARRAY = ArrayStorage("L${STORAGE_PACKAGE}I64Array;", ArrayElementKind.LONG.shift)
        val F32_ARRAY = ArrayStorage("L${STORAGE_PACKAGE}F32Array;", ArrayElementKind.FLOAT.shift)
        val F64_ARRAY = ArrayStorage("L${STORAGE_PACKAGE}F64Array;", ArrayElementKind.DOUBLE.shift)
        val REF_ARRAY = ArrayStorage("L${STORAGE_PACKAGE}RefArray;", ArrayElementKind.REFERENCE.shift)
    }
}
