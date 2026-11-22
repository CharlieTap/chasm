package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlin.jvm.JvmInline

typealias Instantiable = (Store, List<Import>) -> ChasmResult<Instance, InstantiationError<ReferenceValue.Exception>>

typealias Compiler = (ByteArray, ModuleConfig, StoreConfig) -> ChasmResult<Instantiable, ScriptFailure>

sealed interface ScriptFailure {

    @JvmInline
    value class Module(val error: ModuleError) : ScriptFailure {

        override fun toString(): String {
            return error.toString()
        }
    }

    @JvmInline
    value class Execution(val error: InstantiationError<ReferenceValue.Exception>) : ScriptFailure {

        override fun toString(): String {
            return error.toString()
        }
    }
}
