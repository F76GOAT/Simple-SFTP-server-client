package f76goat.sftp.client;

import java.io.IOException;
import java.security.GeneralSecurityException;
import org.bouncycastle.pkcs.PKCSException;
import org.apache.sshd.client.session.ClientSession;

public class ClientStart {

    public static void clientStart() throws IOException, PKCSException, GeneralSecurityException, Exception {
        String host = ClientInterface.getHost();
        int port = ClientInterface.getPort();
        String username = ClientInterface.getUsername();
        String priKeyDir = ClientInterface.getPrivateKeyDir();

        ConnectionManager connectionManager = new ConnectionManager(host, port, username, priKeyDir);
        ClientSession session = null;

        try {
            session = connectionManager.getSession();
            if (session == null) {
                throw new IOException("Failed to establish session.");
            }

            CommandHandler commandHandler = new CommandHandler(session);
            CommandInterface cui = new CommandInterface(commandHandler);
            cui.start();

        } finally {
            if (connectionManager != null) {
                connectionManager.close();
            }
        }
    }
}
