package io.github.charlietap.chasm.script.host

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.expect
import io.github.charlietap.chasm.flatMap

typealias HostModuleResolver = (Store) -> ModuleInstance

fun HostModuleResolver(store: Store): ModuleInstance {

    val hostModuleFile = Resource(FILE_HOST_MODULE)

    return module(hostModuleFile.readBytes()).flatMap { module ->
        instance(store, module, emptyList())
    }.expect("Failed to instantiate script host module")
}

private const val FILE_HOST_MODULE = "src/commonTest/resources/script/spectest-host.wasm"
