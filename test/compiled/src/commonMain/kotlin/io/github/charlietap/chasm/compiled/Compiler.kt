package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store

typealias Instantiable = (Store, List<Import>) -> ChasmResult<Instance, ChasmError>

typealias Compiler = (ByteArray, ModuleConfig, StoreConfig) -> ChasmResult<Instantiable, ChasmError>
