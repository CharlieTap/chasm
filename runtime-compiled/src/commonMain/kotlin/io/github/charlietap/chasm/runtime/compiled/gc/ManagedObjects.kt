package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
class ManagedObjects(
    val structs: StructOperations,
    val arrays: ArrayOperations,
    val exceptions: ExceptionOperations,
    val tables: TableOperations,
    val globals: GlobalOperations,
)
