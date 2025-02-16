package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.MemArg
import io.github.charlietap.chasm.ir.instruction.MemArg as IRMemArg

internal inline fun MemArgFactory(
    arg: MemArg,
) = IRMemArg(
    align = arg.align,
    offset = arg.offset,
)
