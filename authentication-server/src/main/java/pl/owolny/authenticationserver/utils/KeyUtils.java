package pl.owolny.authenticationserver.utils;

import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyUtils {

    private KeyUtils() {

    }

    public static PublicKey readPublicKey(String path) throws IOException {
        File file = ResourceUtils.getFile(path);

        try (FileReader keyReader = new FileReader(file)) {

            PEMParser pemParser = new PEMParser(keyReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());

            return converter.getPublicKey(publicKeyInfo);
        }
    }

    public static PrivateKey readPrivateKey(String path) throws IOException {
        File file = ResourceUtils.getFile(path);

        try (FileReader keyReader = new FileReader(file)) {

            PEMParser pemParser = new PEMParser(keyReader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(pemParser.readObject());

            return converter.getPrivateKey(privateKeyInfo);
        }
    }
}
