package kfd

fun displayName(name: String?): String =
    name?.trim().orEmpty().ifEmpty { "Гость" }