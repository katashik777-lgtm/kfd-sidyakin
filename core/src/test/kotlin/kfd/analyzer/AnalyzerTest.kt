package kfd.analyzer

import java.io.File
import java.nio.file.Path
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.io.TempDir

class AnalyzerTest {
    @TempDir
    lateinit var tmp: Path

    private fun sample(): File {
        val root = tmp.toFile()
        File(root, "src").mkdirs()
        File(root, "src/A.kt").writeText("package sample\nclass A")
        File(root, "src/B.kt").writeText("package sample\nclass B\n")
        File(root, "README.md").writeText("")
        File(root, "notes.txt").writeText("ignored")
        return root
    }

    @Test
    fun extensionReportOnSample() {
        val report = extensionReport(scan(sample()))
        assertEquals(listOf(ExtensionStat("kt", 2, 4), ExtensionStat("md", 1, 0)), report)
    }

    @Test
    fun longestFilesOnSample() {
        val top = longestFiles(scan(sample()))
        assertEquals(listOf("src/A.kt" to 2, "src/B.kt" to 2, "README.md" to 0), top.map { it.path to it.lines })
    }

    @Test
    fun skipsServiceDirectories() {
        val root = sample()
        for (d in listOf(".git", ".gradle", "build")) {
            File(root, d).mkdirs()
            File(root, "$d/X.kt").writeText("a\nb\nc")
        }
        assertEquals(3, scan(root).size)
    }

    @Test
    fun emptyDirectory() {
        val stats = scan(tmp.toFile())
        assertTrue(extensionReport(stats).isEmpty())
        assertTrue(longestFiles(stats).isEmpty())
    }
}
