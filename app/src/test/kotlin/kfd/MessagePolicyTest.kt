package kfd

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MessagePolicyTest {
    @Test
    fun acceptsShortMessage() {
        assertTrue(canSendMessage("Привет"))
    }
    @Test
    fun rejectsNullMessage() {
        assertFalse(canSendMessage(null))
    }

    @Test
    fun rejectsEmptyMessage() {
        assertFalse(canSendMessage(""))
    }

    @Test
    fun acceptsMessageWithExactLimitLength() {
        val exactMessage = "a".repeat(140)
        assertTrue(canSendMessage(exactMessage))
    }

    @Test
    fun rejectsMessageExceedingLimit() {
        val longMessage = "a".repeat(141)
        assertFalse(canSendMessage(longMessage))
    }

    @Test
    fun rejectsWhenLimitIsZero() {
        assertFalse(canSendMessage("Привет",0))
    }

    @Test
    fun rejectsWhenLimitIsNegative() {
        assertFalse(canSendMessage(text = "Привет", maxLength = -10))
    }

    @Test
    fun acceptsCustomPositiveLimit() {
        assertTrue(canSendMessage(text = "Тест", maxLength = 10))
        assertFalse(canSendMessage(text = "Длинный текст", maxLength = 5))
    }

}