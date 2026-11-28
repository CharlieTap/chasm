package io.github.charlietap.chasm.storage.layout

import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.PackedType

data class TypeCheck(
    val rttKey: String,
    val supertypes: Int,
    val final: Boolean,
)

data class StructLayout(
    val typeIndex: Int,
    val check: TypeCheck,
    val storageClass: String,
    val fields: List<FieldLayout>,
)

data class ArrayLayout(
    val typeIndex: Int,
    val check: TypeCheck,
    val storageClass: String,
)

data class FieldLayout(
    val type: FieldType,
    val slots: FieldSlots,
)

sealed interface FieldSlots

data class NumberSlot(
    val slot: Slot,
    val packed: PackedType?,
) : FieldSlots

data class WideNumberSlots(
    val low: Slot,
    val high: Slot,
) : FieldSlots

data class ReferenceSlot(
    val slot: Slot,
    val check: TypeCheck?,
) : FieldSlots

sealed interface Slot

data class InlineSlot(
    val field: String,
) : Slot

data class SpilledSlot(
    val array: String,
    val index: Int,
) : Slot
