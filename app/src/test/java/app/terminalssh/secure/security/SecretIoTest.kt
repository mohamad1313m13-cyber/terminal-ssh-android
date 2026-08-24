package app.terminalssh.secure.security

import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith

class SecretIoTest {
    @Test
    fun readsWithinLimit() {
        val expected = byteArrayOf(1, 2, 3, 4)
        val actual = SecretIo.readBounded(ByteArrayInputStream(expected), 4)
        assertContentEquals(expected, actual)
        actual.fill(0)
    }

    @Test
    fun rejectsOneByteOverLimit() {
        assertFailsWith<IllegalArgumentException> {
            SecretIo.readBounded(ByteArrayInputStream(ByteArray(5)), 4)
        }
    }
}
