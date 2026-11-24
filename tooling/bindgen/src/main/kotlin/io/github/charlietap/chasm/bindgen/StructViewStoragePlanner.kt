package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.FieldStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.RttKeys
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StoragePlans
import io.github.charlietap.chasm.compiler.backend.analysis.gc.resolveDefinedType
import io.github.charlietap.chasm.compiler.backend.analysis.gc.subType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.structType

internal interface StructViewStoragePlanner {

    fun storage(type: DefinedType): StructViewStorage
}

internal class StoragePlansStructViewStoragePlanner(
    private val keys: RttKeys,
) : StructViewStoragePlanner {

    override fun storage(type: DefinedType): StructViewStorage {

        val plan = StoragePlans(listOf(type), STORAGE_FAMILY).struct(0)
        val descriptor = plan.className.removePrefix("L").removeSuffix(";").replace('/', '.')
        val fields = requireNotNull(type.structType()).fields

        return StructViewStorage(
            storageClass = ClassName(descriptor.substringBeforeLast('.'), descriptor.substringAfterLast('.')),
            check = rttCheck(type),
            fieldLanes = plan.fields.zip(fields, ::fieldLanes),
        )
    }

    private fun fieldLanes(
        storage: FieldStorage,
        field: FieldType,
    ): FieldLanes {

        val storageType = field.storageType
        val valueType = (storageType as? StorageType.Value)?.type
        if (valueType !is ValueType.Reference) {
            return NumberLanes(storage.lanes, (storageType as? StorageType.Packed)?.type)
        }

        val definedType = (valueType.referenceType.heapType as? ConcreteHeapType.Defined)?.definedType
        val lane = storage.lanes.single()

        return if (definedType != null && (definedType.structType() != null || definedType.arrayType() != null)) {
            DefinedReferenceLane(lane, rttCheck(definedType))
        } else {
            AbstractReferenceLane(lane)
        }
    }

    private fun rttCheck(type: DefinedType): RttCheck {

        val supertypes = generateSequence(type) { subtype ->
            subtype.subType().superTypes.singleOrNull()?.let { supertype -> resolveDefinedType(supertype, emptyList(), subtype) }
        }

        return RttCheck(keys.key(type), supertypes.count() - 1, type.subType() is SubType.Final)
    }
}
