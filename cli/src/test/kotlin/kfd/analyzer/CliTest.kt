package kfd.analyzer

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.file.Path
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class CliTest {
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
