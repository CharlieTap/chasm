package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.type.HeapType

data class ConstantExpression(
    val instructions: List<ConstantInstruction>,
)

sealed interface ConstantInstruction {

    data class I32Const(
        val value: Int,
    ) : ConstantInstruction

    data class I64Const(
        val value: Long,
    ) : ConstantInstruction

    data class F32Const(
        val bits: Int,
    ) : ConstantInstruction

    data class F64Const(
        val bits: Long,
    ) : ConstantInstruction

    data class V128Const(
        val low: Long,
        val high: Long,
    ) : ConstantInstruction

    data object I32Add : ConstantInstruction

    data object I32Sub : ConstantInstruction

    data object I32Mul : ConstantInstruction

    data object I64Add : ConstantInstruction

    data object I64Sub : ConstantInstruction

    data object I64Mul : ConstantInstruction

    data class GlobalGet(
        val globalIdx: Index.GlobalIndex,
    ) : ConstantInstruction

    data class RefNull(
        val type: HeapType,
    ) : ConstantInstruction

    data class RefFunc(
        val funcIdx: Index.FunctionIndex,
    ) : ConstantInstruction

    data class StructNew(
        val typeIndex: Index.TypeIndex,
    ) : ConstantInstruction

    data class StructNewDefault(
        val typeIndex: Index.TypeIndex,
    ) : ConstantInstruction

    data class ArrayNew(
        val typeIndex: Index.TypeIndex,
    ) : ConstantInstruction

    data class ArrayNewDefault(
        val typeIndex: Index.TypeIndex,
    ) : ConstantInstruction

    data class ArrayNewFixed(
        val typeIndex: Index.TypeIndex,
        val size: UInt,
    ) : ConstantInstruction

    data object RefI31 : ConstantInstruction

    data object AnyConvertExtern : ConstantInstruction

    data object ExternConvertAny : ConstantInstruction
}
