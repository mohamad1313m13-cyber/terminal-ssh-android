package app.terminalssh.secure.security

/**
 * Minimal container-format detection without decoding private-key bytes into an immutable String.
 * This is intentionally only a format gate; cryptographic parsing still belongs to JSch.
 */
object PrivateKeyFormat {
    fun detect(bytes: ByteArray): String {
        require(containsAscii(bytes, "PRIVATE KEY")) { "not an OpenSSH/PEM private key" }
        return when {
            containsAscii(bytes, "OPENSSH PRIVATE KEY") -> "openssh"
            containsAscii(bytes, "RSA PRIVATE KEY") -> "rsa"
            containsAscii(bytes, "EC PRIVATE KEY") -> "ecdsa"
            else -> "pem"
        }
    }

    internal fun containsAscii(bytes: ByteArray, needle: String): Boolean {
        if (needle.isEmpty()) return true
        if (bytes.size < needle.length) return false
        outer@ for (start in 0..bytes.size - needle.length) {
            for (i in needle.indices) {
                if (bytes[start + i] != needle[i].code.toByte()) continue@outer
            }
            return true
        }
        return false
    }
}
