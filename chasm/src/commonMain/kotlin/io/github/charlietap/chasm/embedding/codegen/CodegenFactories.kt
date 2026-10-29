package io.github.charlietap.chasm.embedding.codegen

import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.Store

typealias ModuleFactory = (ByteArray) -> Module
typealias SuspendModuleFactory = suspend (ByteArray) -> Module
typealias InstanceFactory = (Store, Module, List<Import>) -> Instance
typealias SuspendInstanceFactory = suspend (Store, Module, List<Import>) -> Instance
