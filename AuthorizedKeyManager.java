package f76goat.sftp.server;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.PublicKey;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.sshd.common.config.keys.AuthorizedKeyEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthorizedKeyManager {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizedKeyManager.class);

    private static final String AUTHORIZED_KEYS_FILE = System.getenv("AUTHORIZED_KEYS_FILE_PATH");
    private static final Map<String, PublicKey> authorizedKeys = new ConcurrentHashMap<>();

    static {
        loadAuthorizedKeys();
    }

    private static void loadAuthorizedKeys() {
        if (AUTHORIZED_KEYS_FILE == null || AUTHORIZED_KEYS_FILE.isEmpty()) {
            logger.error("The authorized keys file path is not set in the environment variables.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(AUTHORIZED_KEYS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }
                try {
                    AuthorizedKeyEntry entry = AuthorizedKeyEntry.parseAuthorizedKeyEntry(line);
                    PublicKey publicKey = entry.resolvePublicKey(null, null, null);
                    String username = entry.getComment();

                    if (username == null || username.isEmpty()) {
                        logger.warn("No username/comment found for key: {}", line);
                        continue;
                    }

                    if (publicKey.getAlgorithm().equals("RSA") || publicKey.getAlgorithm().equals("ED25519")) {
                        authorizedKeys.put(username, publicKey);
                        logger.info("Loaded authorized key for user: {}", username);
                    } else {
                        logger.warn("Invalid key algorithm for user: {}", username);
                    }

                } catch (IOException | GeneralSecurityException e) {
                    logger.error("Failed to parse authorized key entry: {}", line, e);
                }
            }
        } catch (IOException e) {
            logger.error("Error reading authorized keys file", e);
        }
    }

    public static boolean isAuthorized(String username, PublicKey key) {
        return authorizedKeys.containsKey(username) && authorizedKeys.get(username).equals(key);
    }
}
