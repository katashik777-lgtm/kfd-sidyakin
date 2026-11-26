package kfd.analyzer

import java.io.File
import java.io.PrintStream

fun formatExtensions(report: List<ExtensionStat>): List<String> =
    report.map { "${it.extension}: ${it.files} ${if (it.files == 1) "file" else "files"}, ${it.lines} ${if (it.lines == 1) "line" else "lines"}" }

fun formatLongest(files: List<FileStat>): List<String> =
    files.map { "${it.path} (${it.lines})" }

/** Выполняет программу, возвращает код завершения. */
fun run(args: Array<String>, out: PrintStream, err: PrintStream): Int {
    if (args.size != 1) {
        err.println("Usage: repo-analyzer <directory>")
        return 2
    }
    val root = File(args[0])
    if (!root.isDirectory) {
        err.println("Error: '${args[0]}' does not exist or is not a directory")
        return 1
    }
    val stats = scan(root)
    out.println("Files and lines by extension:")
    formatExtensions(extensionReport(stats)).forEach { out.println("  $it") }
    out.println("Top 3 longest files:")
    formatLongest(longestFiles(stats)).forEach { out.println("  $it") }
    return 0
}

fun main(args: Array<String>) {
    val code = run(args, System.out, System.err)
    if (code != 0) kotlin.system.exitProcess(code)
}
