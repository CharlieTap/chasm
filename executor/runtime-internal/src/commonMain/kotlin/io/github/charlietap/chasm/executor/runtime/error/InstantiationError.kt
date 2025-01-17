package io.github.charlietap.chasm.executor.runtime.error

import io.github.charlietap.chasm.ast.module.Index
import kotlin.jvm.JvmInline

sealed interface InstantiationError : ModuleTrapError {

    @JvmInline
    value class FailedToResolveFunctionType(val index: Index.TypeIndex) : InstantiationError

    data object MissingImport : InstantiationError

    data class UnexpectedImport(
        val importModuleName: String,
        val importEntityName: String,
    ) : InstantiationError

    data object DataSegmentMemoryIndexNotZero : InstantiationError

    data object ClassificationError : InstantiationError

    data object PredecodingError : InstantiationError
}
