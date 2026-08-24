package app.terminalssh.secure.security

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PrivateKeyFormatTest {
    @Test fun detectsOpenSshWithoutStringDecode() {
        val bytes = "-----BEGIN OPENSSH PRIVATE KEY-----".encodeToByteArray()
        assertEquals("openssh", PrivateKeyFormat.detect(bytes))
        bytes.fill(0)
    }

    @Test fun rejectsNonPrivateKey() {
        assertFailsWith<IllegalArgumentException> {
            PrivateKeyFormat.detect("hello".encodeToByteArray())
        }
    }
}
