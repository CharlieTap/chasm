package io.github.charlietap.chasm.executor.instantiator.predecoding.instruction

import io.github.charlietap.chasm.ir.instruction.MemArg
import io.github.charlietap.chasm.executor.runtime.instruction.MemArg as RuntimeMemArg

internal typealias MemArgPredecoder = (MemArg) -> RuntimeMemArg

internal inline fun MemArgPredecoder(
    arg: MemArg,
) = RuntimeMemArg(
    align = arg.align.toInt(),
    offset = arg.offset.toInt(),
)
