package kfd

fun canSendMessage(
    text: String?,
    maxLength: Int = 140
): Boolean {
    if (maxLength <= 0 || text.isNullOrEmpty()) return false
    return text.length <= maxLength
}
