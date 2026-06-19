import kotlinx.browser.document

internal actual fun setRootText(value: String) {
    document.getElementById("root")?.textContent = value
}
