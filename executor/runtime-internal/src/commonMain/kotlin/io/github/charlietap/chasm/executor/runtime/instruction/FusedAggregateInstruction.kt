package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ir.module.Index

sealed interface FusedAggregateInstruction : LinkedInstruction {

    data class StructGet(
        val address: LoadOp,
        val destination: StoreOp,
        val typeIndex: Index.TypeIndex,
        val fieldIndex: Index.FieldIndex,
    ) : AggregateInstruction
}
