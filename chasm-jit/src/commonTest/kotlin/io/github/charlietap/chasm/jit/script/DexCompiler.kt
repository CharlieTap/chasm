@file:OptIn(ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit.script

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.compiled.Instantiable
import io.github.charlietap.chasm.compiled.script.ScriptModules
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.map
import io.github.charlietap.chasm.jit.instance
import io.github.charlietap.chasm.jit.module

fun compileDex(
    bytes: ByteArray,
    moduleConfig: ModuleConfig,
    storeConfig: StoreConfig,
): ChasmResult<Instantiable, ChasmError> = when (val validated = ScriptModules.validate(bytes, moduleConfig)) {
    is ChasmResult.Error -> validated
    is ChasmResult.Success -> module(bytes, moduleConfig).map { module -> { store, imports -> instance(store, module, imports) } }
}
