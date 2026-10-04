package kfd

fun canSendMessage(
    text: String?,
    maxLength: Int = 140
): Boolean = text!!.isNotEmpty() && text.length < maxLength
