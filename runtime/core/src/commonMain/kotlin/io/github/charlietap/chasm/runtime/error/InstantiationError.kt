package io.github.charlietap.chasm.runtime.error

import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.ast.module.Index
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

    data object UnsupportedMemory64Module : InstantiationError

    data object UnsupportedSIMDModule : InstantiationError

    data object UnsupportedThreadsModule : InstantiationError

    @JvmInline
    value class MemoryLimitExceeded(val maximumPages: Int) : InstantiationError

    @JvmInline
    value class TableLimitExceeded(val maximumElements: Int) : InstantiationError

    data class DataUnavailable(
        val reason: String,
    ) : InstantiationError

    data class CompiledCodeRejected(
        val reason: String,
    ) : InstantiationError
}
