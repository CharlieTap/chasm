package io.github.charlietap.chasm.embedding.shapes

import io.github.charlietap.chasm.executor.runtime.type.ExternalType

data class ImportDefinition(val moduleName: String, val entityName: String, val type: ExternalType)
