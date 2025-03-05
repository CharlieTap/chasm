package io.github.charlietap.chasm.runtime.error

import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.ir.module.Index
import kotlin.jvm.JvmInline

sealed interface InstantiationError : ModuleTrapError {

    @JvmInline
    value class FailedToResolveFunctionType(val index: Index.TypeIndex) : InstantiationError

    data class MissingImports(
        val imports: List<Import>,
    ) : InstantiationError

    data class UnexpectedImport(
        val importModuleName: String,
        val importEntityName: String,
    ) : InstantiationError

    data object DataSegmentMemoryIndexNotZero : InstantiationError

    data object ClassificationError : InstantiationError

    data object PredecodingError : InstantiationError
}
