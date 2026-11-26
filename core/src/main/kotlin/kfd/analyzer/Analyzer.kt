package kfd.analyzer

import java.io.File
import java.nio.file.Files

val SOURCE_EXTENSIONS = setOf("kt", "kts", "md")
val SKIPPED_DIRS = setOf(".git", ".gradle", "build")

data class FileStat(val path: String, val extension: String, val lines: Int)
data class ExtensionStat(val extension: String, val files: Int, val lines: Int)

/** Обходит каталог и считает строки в подходящих файлах. */
fun scan(root: File): List<FileStat> {
    require(root.isDirectory) { "Not a directory: ${root.path}" }
    val result = mutableListOf<FileStat>()
    root.walkTopDown()
        .onEnter { dir -> dir == root || (dir.name !in SKIPPED_DIRS && !Files.isSymbolicLink(dir.toPath())) }
        .forEach { file ->
            if (file.isFile && file.extension in SOURCE_EXTENSIONS && !Files.isSymbolicLink(file.toPath())) {
                val lines = file.useLines { lines -> lines.fold(0) { n, _ -> n + 1 } }
                result += FileStat(file.relativeTo(root).invariantSeparatorsPath, file.extension, lines)
            }
        }
    return result
}

fun extensionReport(stats: List<FileStat>): List<ExtensionStat> =
    stats.groupBy { it.extension }
        .map { (ext, list) -> ExtensionStat(ext, list.size, list.sumOf { it.lines }) }
        .sortedBy { it.extension }

fun longestFiles(stats: List<FileStat>, limit: Int = 3): List<FileStat> =
    stats.sortedWith(compareByDescending<FileStat> { it.lines }.thenBy { it.path }).take(limit)
