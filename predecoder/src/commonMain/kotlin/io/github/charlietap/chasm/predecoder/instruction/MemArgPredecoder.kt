package io.github.charlietap.chasm.predecoder.instruction

import io.github.charlietap.chasm.ir.instruction.MemArg
import io.github.charlietap.chasm.runtime.instruction.MemArg as RuntimeMemArg

internal typealias MemArgPredecoder = (MemArg) -> RuntimeMemArg

internal inline fun MemArgPredecoder(
    arg: MemArg,
) = RuntimeMemArg(
    align = arg.align.toInt(),
    offset = arg.offset.toInt(),
)
