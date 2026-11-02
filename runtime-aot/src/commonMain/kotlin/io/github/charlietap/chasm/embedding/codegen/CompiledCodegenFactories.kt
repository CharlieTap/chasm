package io.github.charlietap.chasm.embedding.codegen

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store

@ExperimentalChasmApi
typealias CompiledInstanceFactory = (Store, CompiledModule, List<Import>) -> Instance
