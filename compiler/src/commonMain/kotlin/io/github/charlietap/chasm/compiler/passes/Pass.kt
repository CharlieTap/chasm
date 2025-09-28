package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.ir.module.Module

internal typealias Pass = (PassContext, Module) -> Module
