package kfd.analyzer

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
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

    @Test
    fun errorForMissingPathAndFile() {
        fun exec(path: String): Pair<Int, String> {
            val err = ByteArrayOutputStream()
            val code = run(arrayOf(path), PrintStream(ByteArrayOutputStream()), PrintStream(err))
            return code to err.toString()
        }
        val (c1, m1) = exec(File(tmp.toFile(), "missing").path)
        assertTrue(c1 != 0 && m1.isNotBlank())
        val f = File(tmp.toFile(), "f.kt").apply { writeText("x") }
        assertTrue(exec(f.path).first != 0)
    }

    @Test
    fun cliPrintsBothReports() {
        val out = ByteArrayOutputStream()
        val code = run(arrayOf(sample().path), PrintStream(out), PrintStream(ByteArrayOutputStream()))
        assertEquals(0, code)
        val text = out.toString()
        assertTrue("kt: 2 files, 4 lines" in text && "md: 1 file, 0 lines" in text && "src/A.kt (2)" in text)
    }
}
