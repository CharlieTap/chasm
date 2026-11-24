package io.github.charlietap.chasm.bindgen

import com.squareup.kotlinpoet.ClassName
import io.github.charlietap.chasm.bindgen.ext.asType
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.ConcreteHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.ext.arrayType
import io.github.charlietap.chasm.type.ext.structType
import java.util.IdentityHashMap

internal class GcTypeResolver(
    private val interfaceClassName: ClassName,
    private val mapsReferences: Boolean,
    private val storagePlanner: StructViewStoragePlanner?,
) {

    private var recursiveGroupBases = IdentityHashMap<RecursiveType, Int>()
    private var registeredStructViews = LinkedHashMap<Int, StructView>()
    private val structViewsInProgress = mutableSetOf<Int>()

    val structViews: List<StructView>
        get() {
            return registeredStructViews.values.toList()
        }

    fun <T> transaction(resolve: () -> T): T {

        val groupBasesBefore = IdentityHashMap(recursiveGroupBases)
        val structViewsBefore = LinkedHashMap(registeredStructViews)

        return try {
            resolve()
        } catch (exception: Exception) {
            recursiveGroupBases = groupBasesBefore
            registeredStructViews = structViewsBefore
            throw exception
        }
    }

    fun signatureType(valueType: ValueType): Type {

        if (!mapsReferences || valueType !is ValueType.Reference) {
            return valueType.asType()
        }

        return referenceType(valueType.referenceType, inSignature = true)
    }

    fun definedType(referenceType: ReferenceType): DefinedType? {
        return (referenceType.heapType as? ConcreteHeapType.Defined)?.definedType
    }

    fun typeIndex(definedType: DefinedType): Int {

        if (definedType.typeIndex >= 0) {
            recursiveGroupBases[definedType.recursiveType] = definedType.typeIndex - definedType.recursiveTypeIndex
            return definedType.typeIndex
        }

        val groupBase = recursiveGroupBases[definedType.recursiveType]
            ?: error("Cannot resolve the type index of a type reached only from inside its own recursive group")

        return groupBase + definedType.recursiveTypeIndex
    }

    private fun referenceType(
        referenceType: ReferenceType,
        inSignature: Boolean,
    ): Type {

        val nullable = referenceType is ReferenceType.RefNull
        val definedType = definedType(referenceType)

        return when {
            definedType?.structType() != null -> StructViewType(structViewClassName(definedType), nullable)
            definedType?.arrayType() != null -> ArrayReferenceType(nullable)
            inSignature && !passesAsAny(referenceType.heapType) -> {
                error("Cannot pass a reference of heap type ${referenceType.heapType} through a generated binding")
            }
            else -> AnyReferenceType(nullable)
        }
    }

    private fun passesAsAny(heapType: HeapType): Boolean {
        return heapType in ANY_PASSABLE_HEAP_TYPES
    }

    private fun structViewClassName(definedType: DefinedType): ClassName {

        val typeIndex = typeIndex(definedType)
        val className = interfaceClassName.nestedClass("Struct$typeIndex")

        if (typeIndex in registeredStructViews || typeIndex in structViewsInProgress) {
            return className
        }

        structViewsInProgress.add(typeIndex)
        try {
            val structType = requireNotNull(definedType.structType())
            val fields = structType.fields.mapIndexed { index, field ->
                StructViewField(
                    name = "f$index",
                    index = index,
                    type = fieldType(field.storageType),
                    mutable = field.mutability == Mutability.Var,
                )
            }
            registeredStructViews[typeIndex] = StructView(className, fields, storagePlanner?.storage(definedType))
        } finally {
            structViewsInProgress.remove(typeIndex)
        }

        return className
    }

    private fun fieldType(storageType: StorageType): Type {
        return when (storageType) {
            is StorageType.Packed -> Scalar.Integer
            is StorageType.Value -> when (val type = storageType.type) {
                is ValueType.Number -> type.asType()
                is ValueType.Reference -> referenceType(type.referenceType, inSignature = false)
                is ValueType.Vector,
                is ValueType.Bottom,
                -> error("Cannot generate a struct view field of type $type")
            }
        }
    }
}

private val ANY_PASSABLE_HEAP_TYPES: Set<HeapType> = setOf(
    AbstractHeapType.Any,
    AbstractHeapType.Eq,
    AbstractHeapType.Struct,
    AbstractHeapType.Array,
    AbstractHeapType.I31,
    AbstractHeapType.None,
    AbstractHeapType.Extern,
    AbstractHeapType.NoExtern,
)
