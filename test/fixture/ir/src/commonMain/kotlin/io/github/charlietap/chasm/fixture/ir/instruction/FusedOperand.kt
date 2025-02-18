package io.github.charlietap.chasm.fixture.ir.instruction

import io.github.charlietap.chasm.fixture.ir.module.globalIndex
import io.github.charlietap.chasm.fixture.ir.module.localIndex
import io.github.charlietap.chasm.ir.instruction.FusedOperand
import io.github.charlietap.chasm.ir.module.Index

fun fusedOperand(): FusedOperand = i32ConstOperand()

fun i32ConstOperand(
    const: Int = 0,
) = FusedOperand.I32Const(
    const = const,
)

fun i64ConstOperand(
    const: Long = 0L,
): FusedOperand = FusedOperand.I64Const(
    const = const,
)

fun f32ConstOperand(
    const: Float = 0.0f,
): FusedOperand = FusedOperand.F32Const(
    const = const,
)

fun f64ConstOperand(
    const: Double = 0.0,
): FusedOperand = FusedOperand.F64Const(
    const = const,
)

fun localGetOperand(
    index: Index.LocalIndex = localIndex(),
): FusedOperand = FusedOperand.LocalGet(
    index = index,
)

fun globalGetOperand(
    index: Index.GlobalIndex = globalIndex(),
): FusedOperand = FusedOperand.GlobalGet(
    index = index,
)

fun valueStackOperand() = FusedOperand.ValueStack
