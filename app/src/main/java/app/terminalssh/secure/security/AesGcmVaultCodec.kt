package app.terminalssh.secure.security

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class AesGcmVaultCodec(
    private val random: SecureRandom = SecureRandom(),
) {
    data class Sealed(val nonce: ByteArray, val ciphertext: ByteArray) {
        init {
            require(nonce.size == NONCE_BYTES) { "invalid GCM nonce length" }
            require(ciphertext.size >= TAG_BYTES) { "ciphertext too short" }
        }
    }

    fun seal(key: SecretKey, plaintext: ByteArray, aad: VaultAad): Sealed {
        val nonce = ByteArray(NONCE_BYTES).also(random::nextBytes)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_BITS, nonce))
        cipher.updateAAD(aad.bytes())
        return Sealed(nonce, cipher.doFinal(plaintext))
    }

    fun open(key: SecretKey, sealed: Sealed, aad: VaultAad): ByteArray {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(TAG_BITS, sealed.nonce))
        cipher.updateAAD(aad.bytes())
        return cipher.doFinal(sealed.ciphertext)
    }

    companion object {
        const val NONCE_BYTES = 12
        const val TAG_BITS = 128
        const val TAG_BYTES = TAG_BITS / 8
    }
}
