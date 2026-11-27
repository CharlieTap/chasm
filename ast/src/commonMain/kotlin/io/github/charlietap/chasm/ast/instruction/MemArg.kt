package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
data class MemArg(val align: UInt, val offset: ULong)
