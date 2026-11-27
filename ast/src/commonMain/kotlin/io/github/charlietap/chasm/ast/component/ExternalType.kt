package io.github.charlietap.chasm.ast.component

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.component.Index.ComponentTypeIndex
import io.github.charlietap.chasm.ast.component.Index.ComponentValueIndex
import io.github.charlietap.chasm.ast.module.Index.TypeIndex as ModuleTypeIndex

@InternalChasmApi
sealed interface ExternalType {

    data class CoreModule(val typeIndex: ModuleTypeIndex) : ExternalType

    data class Function(val typeIndex: ComponentTypeIndex) : ExternalType

    data class Value(val bound: ValueBound) : ExternalType

    data class Type(val bound: TypeBound) : ExternalType

    data class Component(val typeIndex: ComponentTypeIndex) : ExternalType

    data class Instance(val typeIndex: ComponentTypeIndex) : ExternalType
}

@InternalChasmApi
sealed interface ValueBound {

    data class Equals(val valueIndex: ComponentValueIndex) : ValueBound

    data class Type(val type: ValueType) : ValueBound
}

@InternalChasmApi
sealed interface TypeBound {

    data class Equals(val typeIndex: ComponentTypeIndex) : TypeBound

    data object SubResource : TypeBound
}
