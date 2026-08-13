package app.terminalssh.secure.model

data class HostProfile(
    val id: String,
    val host: String,
    val port: Int = 22,
    val username: String,
    val auth: AuthMethod,
    val hostKeyPolicy: HostKeyPolicy = HostKeyPolicy.STRICT,
) {
    init {
        require(host.isNotBlank()) { "host must not be blank" }
        require(port in 1..65535) { "port out of range" }
        require(username.isNotBlank()) { "username must not be blank" }
    }
}

sealed interface AuthMethod {
    data class Password(val vaultRef: String) : AuthMethod
    data class PrivateKey(val keyVaultRef: String, val passphraseVaultRef: String? = null) : AuthMethod
}

enum class HostKeyPolicy { STRICT, TRUST_ON_FIRST_USE }
