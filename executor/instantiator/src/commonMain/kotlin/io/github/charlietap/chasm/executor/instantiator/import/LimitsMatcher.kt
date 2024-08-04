package io.github.charlietap.chasm.executor.instantiator.import

import io.github.charlietap.chasm.ast.type.Limits

internal typealias LimitsMatcher = (Limits, Limits) -> Boolean
