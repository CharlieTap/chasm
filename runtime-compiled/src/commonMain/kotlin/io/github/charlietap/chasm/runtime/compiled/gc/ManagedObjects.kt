package io.github.charlietap.chasm.runtime.compiled.gc

internal class ManagedObjects(
    val structs: StructOperations,
    val arrays: ArrayOperations,
    val exceptions: ExceptionOperations,
    val tables: TableOperations,
    val globals: GlobalOperations,
)
