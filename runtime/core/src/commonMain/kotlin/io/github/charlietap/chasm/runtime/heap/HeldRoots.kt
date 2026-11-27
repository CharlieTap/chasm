package io.github.charlietap.chasm.runtime.heap

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.gc.GcRootMarker

@InternalChasmApi
interface HeldRoots {

    fun visit(marker: GcRootMarker)
}
