package io.github.charlietap.chasm.vm

typealias InstanceFactory = (Store, Module, List<Import>) -> Instance
typealias SuspendInstanceFactory = suspend (Store, Module, List<Import>) -> Instance
