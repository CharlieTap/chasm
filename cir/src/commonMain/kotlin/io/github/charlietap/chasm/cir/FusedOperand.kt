package io.github.charlietap.chasm.cir

sealed interface FusedOperand {

    @CirTag("i32_const")
    data class I32Const(
        val const: Int,
    ) : FusedOperand

    @CirTag("i64_const")
    data class I64Const(
        val const: Long,
    ) : FusedOperand

    @CirTag("f32_const")
    data class F32Const(
        val const: F32,
    ) : FusedOperand

    @CirTag("f64_const")
    data class F64Const(
        val const: F64,
    ) : FusedOperand

    @CirTag("frame_slot")
    data class FrameSlot(
        val offset: Int,
    ) : FusedOperand
}
