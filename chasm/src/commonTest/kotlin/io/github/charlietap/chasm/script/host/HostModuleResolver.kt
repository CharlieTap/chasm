package io.github.charlietap.chasm.script.host

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect

typealias HostModuleResolver = suspend (Store) -> Instance

suspend fun HostModuleResolver(store: Store): Instance {

    val hostModuleFile = Resource(FILE_HOST_MODULE)
    val module = module(hostModuleFile.readBytes()).expect("Failed to decode script host module")

    return instance(store, module, emptyList()).expect("Failed to instantiate script host module")
}

private const val FILE_HOST_MODULE = "script/spectest-host.wasm"
