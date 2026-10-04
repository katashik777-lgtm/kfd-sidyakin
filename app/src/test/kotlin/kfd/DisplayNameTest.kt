package kfd

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DisplayNameTest {
    @Test
    fun preservesName() {
        assertEquals("Анна", displayName("Анна"))
    }

    @Test
    fun handlesNull() {
        assertEquals("Гость", displayName(null))
    }

    @Test
    fun removesWhitespace() {
        assertEquals("Анна", displayName("   Анна      "))
    }

    @Test
    fun handlesEmptyString() {
        assertEquals("Гость", displayName(""))
    }

    @Test
    fun handlesWhiteSpace() {
        assertEquals("Гость", displayName(" "))
    }
}
