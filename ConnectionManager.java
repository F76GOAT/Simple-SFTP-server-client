package f76goat.sftp.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.session.ClientSession;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionManager {

    private static final Logger logger = LoggerFactory.getLogger(ConnectionManager.class);
    private SshClient client;
    private ClientSession session;

    public ConnectionManager(String host, int port, String username, String privateKeyPath) {
        client = SshClient.setUpDefaultClient();
        client.start();

        try {
            session = createSession(host, port, username, privateKeyPath);
            if (session == null) {
                logger.error("Failed to create session.");
                return;
            }

            logger.info("Session established successfully.");

        } catch (IOException ex) {
            logger.error("Error during session creation: ", ex);
        }
    }

    public ClientSession createSession(String host, int port, String username, String privateKeyPath) throws IOException {
        try {
            ClientSession session = client.connect(username, host, port).verify().getSession();
            KeyPair keyPair = loadKeyPair(privateKeyPath);
            session.addPublicKeyIdentity(keyPair);
            session.auth().verify();

            logger.info("Session created for user: {}", username);
            return session;
        } catch (IOException e) {
            logger.error("Failed to create session with host {}:{}", host, port, e);
            throw e;
        }
    }

    private KeyPair loadKeyPair(String privateKeyPath) throws IOException {
        Path path = Paths.get(privateKeyPath);
        logger.info("Loading private key from path: [PRIVATE_KEY_PATH]");

        try (BufferedReader reader = Files.newBufferedReader(path); PEMParser pemParser = new PEMParser(reader)) {
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object object = pemParser.readObject();

            if (object instanceof PEMKeyPair) {
                PEMKeyPair pemKeyPair = (PEMKeyPair) object;
                PrivateKey privateKey = converter.getPrivateKey(pemKeyPair.getPrivateKeyInfo());
                PublicKey publicKey = converter.getPublicKey(pemKeyPair.getPublicKeyInfo());
                return new KeyPair(publicKey, privateKey);
            } else {
                logger.error("Unsupported key format: {}", object.getClass().getName());
                throw new IOException("Unsupported key format: " + object.getClass().getName());
            }
        } catch (Exception e) {
            logger.error("Error loading key pair from file: ", e);
            throw e;
        }
    }

    public ClientSession getSession() {
        return session;
    }

    public void close() throws IOException {
        if (session != null) {
            session.close();
        }
        if (client != null) {
            client.stop();
        }
    }
}
