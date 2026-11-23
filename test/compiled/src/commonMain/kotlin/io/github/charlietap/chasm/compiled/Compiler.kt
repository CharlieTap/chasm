package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import kotlin.jvm.JvmInline

typealias Compiler = (ByteArray, ModuleConfig, RuntimeConfig) -> ChasmResult<Module, ScriptFailure>

sealed interface ScriptFailure {

    @JvmInline
    value class Module(val error: ModuleError) : ScriptFailure {

        override fun toString(): String {
            return error.toString()
        }
    }

    @JvmInline
    value class Execution(val error: InstantiationError) : ScriptFailure {

        override fun toString(): String {
            return error.toString()
        }
    }
}
