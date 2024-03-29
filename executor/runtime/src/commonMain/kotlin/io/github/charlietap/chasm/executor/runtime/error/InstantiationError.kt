package io.github.charlietap.chasm.executor.runtime.error

import io.github.charlietap.chasm.ast.instruction.Index
import kotlin.jvm.JvmInline

sealed interface InstantiationError : ModuleRuntimeError {

    @JvmInline
    value class FailedToResolveFunctionType(val index: Index.TypeIndex) : InstantiationError

    @JvmInline
    value class ImportLookupFailed(val address: Int) : InstantiationError

    data object MissingImport : InstantiationError

    data class UnexpectedImport(val importModuleName: String, val importEntityName: String) : InstantiationError

    data object DataSegmentMemoryIndexNotZero : InstantiationError
}
