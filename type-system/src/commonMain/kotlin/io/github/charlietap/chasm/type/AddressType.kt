package io.github.charlietap.chasm.type

sealed interface AddressType {
    data object I32 : AddressType

    data object I64 : AddressType
}
