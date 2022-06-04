import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.bouncycastle.util.io.pem.PemObject
import org.bouncycastle.util.io.pem.PemReader
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.security.KeyFactory
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Instant
import java.util.*

fun main(args: Array<String>) {
    val a = getJWT()
    println("encoded token $a")
}
fun getJWT(): String {

    lateinit var privateKeyPem: PemObject

    val file = File("src/main/kotlin/yapem.pem").inputStream()
    val isr = InputStreamReader(file)
    val readerBufferedFile = BufferedReader(isr)

    val reader: PemReader = PemReader(readerBufferedFile)
    privateKeyPem = reader.readPemObject()


    val keyFactory = KeyFactory.getInstance("RSA")
    val privateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(privateKeyPem.getContent()))
    val serviceAccountId = "ajeijp19m599s8aos6vr"
    val keyId = "aje92dpc4t4tu7bbu8un"
    val now = Instant.now()

    //  JWT.
    val encodedToken: String = Jwts.builder()
        .setHeaderParam("kid", keyId)
        .setIssuer(serviceAccountId)
        .setAudience("https://iam.api.cloud.yandex.net/iam/v1/tokens")
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(now.plusSeconds(360)))
        .signWith(
            privateKey,
            SignatureAlgorithm.PS256
        )
        .compact()

    return encodedToken
}