package io.github.charlietap.chasm.type.copy

import io.github.charlietap.chasm.ir.type.HeapType

inline fun HeapTypeDeepCopier(
    input: HeapType,
): HeapType = input
