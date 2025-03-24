package f76goat.sftp.server;

import org.apache.sshd.common.keyprovider.KeyPairProvider;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;

import java.nio.file.Path;

public class Utils {

    public static KeyPairProvider createHostKeyProvider(Path path) {
        return new SimpleGeneratorHostKeyProvider(path);
    }
}
