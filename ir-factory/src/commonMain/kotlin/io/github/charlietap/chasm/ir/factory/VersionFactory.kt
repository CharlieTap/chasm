package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Version
import io.github.charlietap.chasm.ir.module.Version as IRVersion

internal inline fun VersionFactory(
    version: Version,
): IRVersion {
    return when (version) {
        Version.One -> IRVersion.One
    }
}
