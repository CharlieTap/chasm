package io.github.charlietap.chasm.script.host

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.jit.module
import kotlinx.coroutines.runBlocking

typealias HostModuleResolver = (Store) -> Instance

fun HostModuleResolver(store: Store): Instance {

    val hostModuleFile = Resource(FILE_HOST_MODULE)

    return runBlocking { module(hostModuleFile.readBytes(), validate = false) }
        .flatMap { module ->
            runBlocking { instance(store, module, emptyList()) }
        }.expect("Failed to instantiate script host module")
}

private const val FILE_HOST_MODULE = "script/spectest-host.wasm"
