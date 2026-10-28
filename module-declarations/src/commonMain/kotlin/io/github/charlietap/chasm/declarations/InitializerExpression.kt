package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.type.HeapType

data class InitializerExpression(
    val instructions: List<InitializerInstruction>,
)

sealed interface InitializerInstruction {

    data class I32Const(
        val value: Int,
    ) : InitializerInstruction

    data class I64Const(
        val value: Long,
    ) : InitializerInstruction

    data class F32Const(
        val bits: Int,
    ) : InitializerInstruction

    data class F64Const(
        val bits: Long,
    ) : InitializerInstruction

    data class V128Const(
        val low: Long,
        val high: Long,
    ) : InitializerInstruction

    data object I32Add : InitializerInstruction

    data object I32Sub : InitializerInstruction

    data object I32Mul : InitializerInstruction

    data object I64Add : InitializerInstruction

    data object I64Sub : InitializerInstruction

    data object I64Mul : InitializerInstruction

    data class GlobalGet(
        val globalIndex: Int,
    ) : InitializerInstruction

    data class RefNull(
        val type: HeapType,
    ) : InitializerInstruction

    data class RefFunc(
        val functionIndex: Int,
    ) : InitializerInstruction

    data class StructNew(
        val typeIndex: Int,
    ) : InitializerInstruction

    data class StructNewDefault(
        val typeIndex: Int,
    ) : InitializerInstruction

    data class ArrayNew(
        val typeIndex: Int,
    ) : InitializerInstruction

    data class ArrayNewDefault(
        val typeIndex: Int,
    ) : InitializerInstruction

    data class ArrayNewFixed(
        val typeIndex: Int,
        val size: Int,
    ) : InitializerInstruction

    data object RefI31 : InitializerInstruction

    data object AnyConvertExtern : InitializerInstruction

    data object ExternConvertAny : InitializerInstruction
}
