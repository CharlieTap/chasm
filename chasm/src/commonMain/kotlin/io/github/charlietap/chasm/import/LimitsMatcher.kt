package io.github.charlietap.chasm.import

import io.github.charlietap.chasm.ast.type.Limits

internal typealias LimitsMatcher = (Limits, Limits) -> Boolean
