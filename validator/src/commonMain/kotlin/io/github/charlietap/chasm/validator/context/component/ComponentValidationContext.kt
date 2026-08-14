package io.github.charlietap.chasm.validator.context.component

import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ComponentConfig
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.type.component.ComponentIdentityAllocator

internal class ComponentValidationContext(
    val config: ComponentConfig,
    val identities: ComponentIdentityAllocator = ComponentIdentityAllocator(),
) {

    private val frames = mutableListOf(ComponentValidationFrame())
    private var moduleContext: ModuleValidationContext? = null

    var depth: Int = 0
        private set

    val frame: ComponentValidationFrame
        get() = frames[depth]

    init {
        frame.reset(ComponentScopeKind.Component)
    }

    fun push(kind: ComponentScopeKind): ComponentValidationFrame {
        depth += 1
        val child = frames.getOrNull(depth) ?: ComponentValidationFrame().also(frames::add)
        child.reset(kind)
        return child
    }

    fun canPush(): Boolean = depth < MAX_NESTING_DEPTH

    fun pop() {
        check(depth > 0)
        frames[depth].clear()
        depth -= 1
    }

    fun outer(count: UInt): ComponentValidationFrame? {
        val target = depth - count.toInt()
        return frames.getOrNull(target)?.takeIf { target >= 0 }
    }

    fun moduleContext(module: Module): ModuleValidationContext {
        val context = moduleContext
        return if (context == null) {
            ModuleValidationContext(config.moduleConfig, module).also { moduleContext = it }
        } else {
            context.reset(config.moduleConfig, module)
            context
        }
    }

    fun reset() {
        frames.forEach(ComponentValidationFrame::clear)
        depth = 0
        frame.reset(ComponentScopeKind.Component)
        moduleContext?.clearLocalState()
        identities.clear()
    }
}

private const val MAX_NESTING_DEPTH = 100
