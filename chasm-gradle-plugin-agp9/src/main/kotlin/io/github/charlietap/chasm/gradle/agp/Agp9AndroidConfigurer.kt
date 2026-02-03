package io.github.charlietap.chasm.gradle.agp

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.Variant
import io.github.charlietap.chasm.gradle.AndroidConfigContext
import io.github.charlietap.chasm.gradle.AndroidConfigurer
import io.github.charlietap.chasm.gradle.CodegenTask
import io.github.charlietap.chasm.gradle.Mode
import io.github.charlietap.chasm.gradle.registerCodegenTask

class Agp9AndroidConfigurer : AndroidConfigurer {
    override fun configure(androidComponents: Any, context: AndroidConfigContext) {
        @Suppress("UNCHECKED_CAST")
        val components = androidComponents as AndroidComponentsExtension<*, *, *>

        components.onVariants { variant: Variant ->
            context.extension.modules.configureEach {
                if (context.extension.mode.get() == Mode.PRODUCER) {
                    context.project.logger.error(
                        "Producer mode is only supported for Kotlin Multiplatform projects with WASM targets",
                    )
                    return@configureEach
                }

                val task = registerCodegenTask(context.project, this, variant.name, context.workerClasspath)
                variant.sources.java?.addGeneratedSourceDirectory(task, CodegenTask::outputDirectory)
                variant.sources.kotlin?.addGeneratedSourceDirectory(task, CodegenTask::outputDirectory)
            }
        }
    }
}
