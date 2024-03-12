package org.example.chat;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.util.Base64;

public class ClientCryptoManager {

    private KeyPair keyPair;

    public ClientCryptoManager() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048); // Este es el tamaño de la clave
            this.keyPair = keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    // Método para convertir la clave pública a una cadena de texto para su envío
    public String getPublicKeyAsString() {
        return Base64.getEncoder().encodeToString(getPublicKey().getEncoded());
    }
}