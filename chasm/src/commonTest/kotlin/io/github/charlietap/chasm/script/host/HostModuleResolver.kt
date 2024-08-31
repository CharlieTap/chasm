package io.github.charlietap.chasm.script.host

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.module
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.shapes.flatMap

typealias HostModuleResolver = (Store) -> Instance

fun HostModuleResolver(store: Store): Instance {

    val hostModuleFile = Resource(FILE_HOST_MODULE)

    return module(hostModuleFile.readBytes()).flatMap { module ->
        instance(store, module, emptyList())
    }.expect("Failed to instantiate script host module")
}

private const val FILE_HOST_MODULE = "src/commonTest/resources/script/spectest-host.wasm"
