package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.module.Module

internal typealias Pass = (PassContextt, Module) -> Module
