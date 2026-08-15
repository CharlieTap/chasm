package io.github.charlietap.chasm.vm

typealias ModuleFactory = (ByteArray) -> Module
typealias SuspendModuleFactory = suspend (ByteArray) -> Module
