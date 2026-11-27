package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.component.Index.ComponentTypeIndex
import io.github.charlietap.chasm.ast.value.NameValue
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex as ModuleFunctionIndex
import io.github.charlietap.chasm.type.ValueType as CoreValueType

@InternalChasmApi
sealed interface ValueType {

    data object Bool : PrimitiveValueType

    data object S8 : PrimitiveValueType

    data object U8 : PrimitiveValueType

    data object S16 : PrimitiveValueType

    data object U16 : PrimitiveValueType

    data object S32 : PrimitiveValueType

    data object U32 : PrimitiveValueType

    data object S64 : PrimitiveValueType

    data object U64 : PrimitiveValueType

    data object F32 : PrimitiveValueType

    data object F64 : PrimitiveValueType

    data object Char : PrimitiveValueType

    data object String : PrimitiveValueType

    data object ErrorContext : PrimitiveValueType

    data class TypeIndex(val index: ComponentTypeIndex) : ValueType
}

@InternalChasmApi
sealed interface PrimitiveValueType : ValueType, DefinedValueType

@InternalChasmApi
sealed interface DefinedValueType {

    data class Record(val fields: kotlin.collections.List<LabeledValueType>) : DefinedValueType

    data class Variant(val cases: kotlin.collections.List<VariantCase>) : DefinedValueType

    data class List(val element: ValueType) : DefinedValueType

    data class FixedLengthList(val element: ValueType, val length: UInt) : DefinedValueType

    data class Map(val key: KeyType, val value: ValueType) : DefinedValueType

    data class Tuple(val elements: kotlin.collections.List<ValueType>) : DefinedValueType

    data class Flags(val labels: kotlin.collections.List<NameValue>) : DefinedValueType

    data class Enum(val labels: kotlin.collections.List<NameValue>) : DefinedValueType

    data class Option(val value: ValueType) : DefinedValueType

    data class Result(val ok: ValueType?, val error: ValueType?) : DefinedValueType

    data class Own(val resource: ComponentTypeIndex) : DefinedValueType

    data class Borrow(val resource: ComponentTypeIndex) : DefinedValueType

    data class Stream(val element: ValueType?) : DefinedValueType

    data class Future(val value: ValueType?) : DefinedValueType
}

@InternalChasmApi
data class LabeledValueType(
    val label: NameValue,
    val type: ValueType,
)

@InternalChasmApi
data class VariantCase(
    val label: NameValue,
    val type: ValueType?,
)

@InternalChasmApi
sealed interface KeyType {

    data object Bool : KeyType

    data object S8 : KeyType

    data object U8 : KeyType

    data object S16 : KeyType

    data object U16 : KeyType

    data object S32 : KeyType

    data object U32 : KeyType

    data object S64 : KeyType

    data object U64 : KeyType

    data object Char : KeyType

    data object String : KeyType
}

@InternalChasmApi
sealed interface TypeDefinition {

    data class Value(val type: DefinedValueType) : TypeDefinition

    data class Function(val type: FunctionType) : TypeDefinition

    data class Component(val declarations: List<ComponentDeclaration>) : TypeDefinition

    data class Instance(val declarations: List<InstanceDeclaration>) : TypeDefinition

    data class Resource(
        val representation: CoreValueType,
        val destructor: ModuleFunctionIndex?,
    ) : TypeDefinition
}

@InternalChasmApi
data class FunctionType(
    val params: List<LabeledValueType>,
    val result: ValueType?,
    val async: Boolean = false,
)

// Decode keeps the grammar broad; validation rejects private resource types in
// component and instance type declarations.
@InternalChasmApi
sealed interface ComponentDeclaration {

    data class CoreType(val type: CoreTypeDefinition) : ComponentDeclaration

    data class Type(val type: TypeDefinition) : ComponentDeclaration

    data class Alias(val alias: AliasDefinition) : ComponentDeclaration

    data class Export(
        val name: NameAttributes,
        val type: ExternalType,
    ) : ComponentDeclaration

    data class Import(
        val name: NameAttributes,
        val type: ExternalType,
    ) : ComponentDeclaration
}

@InternalChasmApi
sealed interface InstanceDeclaration {

    data class CoreType(val type: CoreTypeDefinition) : InstanceDeclaration

    data class Type(val type: TypeDefinition) : InstanceDeclaration

    data class Alias(val alias: AliasDefinition) : InstanceDeclaration

    data class Export(
        val name: NameAttributes,
        val type: ExternalType,
    ) : InstanceDeclaration
}
