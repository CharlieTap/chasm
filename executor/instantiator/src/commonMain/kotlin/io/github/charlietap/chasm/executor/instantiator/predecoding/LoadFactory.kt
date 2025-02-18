package io.github.charlietap.chasm.executor.instantiator.predecoding

import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.globalAddress
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.instruction.LoadOp
import io.github.charlietap.chasm.ir.instruction.FusedOperand

internal typealias LoadFactory = (InstantiationContext, FusedOperand) -> LoadOp

internal inline fun LoadFactory(
    context: InstantiationContext,
    operand: FusedOperand,
): LoadOp {
    return context.loadCache.getOrPut(operand) {
        when (operand) {
            is FusedOperand.I32Const -> {
                { operand.const.toLong() }
            }
            is FusedOperand.I64Const -> {
                { operand.const }
            }
            is FusedOperand.F32Const -> {
                { operand.const.toRawBits().toLong() }
            }
            is FusedOperand.F64Const -> {
                { operand.const.toRawBits() }
            }
            is FusedOperand.GlobalGet -> {
                val address = context.instance!!.globalAddress(operand.index).value
                val global = context.store.global(address);
                { global.value }
            }
            is FusedOperand.LocalGet -> {
                { stack -> stack.getLocal(operand.index.idx) }
            }
            is FusedOperand.ValueStack -> {
                { stack -> stack.pop() }
            }
        }
    }
}
