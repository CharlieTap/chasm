package io.github.charlietap.chasm.decoder.error

internal sealed interface ComponentTypeDecodeError : WasmDecodeError {

    data class UnknownPrimitiveValueType(val opcode: UByte) : ComponentTypeDecodeError

    data class UnknownDefinedValueType(val opcode: UByte) : ComponentTypeDecodeError

    data class UnknownTypeDefinition(val opcode: UByte) : ComponentTypeDecodeError

    data class UnknownExternalType(val opcode: UByte) : ComponentTypeDecodeError

    data class UnknownTypeBound(val opcode: UByte) : ComponentTypeDecodeError

    data class UnknownValueBound(val opcode: UByte) : ComponentTypeDecodeError

    data class UnknownCoreModuleDeclaration(val opcode: UByte) : ComponentTypeDecodeError

    data class InvalidCoreType(val opcode: UByte) : ComponentTypeDecodeError

    data class InvalidMapKey(val type: String) : ComponentTypeDecodeError

    data class InvalidResultList(val opcode: UByte) : ComponentTypeDecodeError

    data class InvalidReservedByte(
        val expected: UByte,
        val actual: UByte,
    ) : ComponentTypeDecodeError
}
