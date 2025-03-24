package f76goat.sftp.server;

import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.SshServer;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.shell.ProcessShellCommandFactory;
import org.apache.sshd.scp.server.ScpCommandFactory;
import org.apache.sshd.sftp.server.SftpSubsystemFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;

public class ServerStart {

    private static final Logger logger = LoggerFactory.getLogger(ServerStart.class);

    public static void main(String[] args) {
        SshServer sshd = SshServer.setUpDefaultServer();
        try {
            sshd.setPort(ServerInterface.getPort());
            sshd.setKeyPairProvider(Utils.createHostKeyProvider(Paths.get("hostkey.ser")));
            sshd.setPublickeyAuthenticator((username, key, session) -> {
                logger.debug("Authentication attempt for user: {}", username);
                return AuthorizedKeyManager.isAuthorized(username, key);
            });

            sshd.setFileSystemFactory(new VirtualFileSystemFactory(Paths.get("/srv/sftp/").toAbsolutePath()));
            CommandFactory commandFactory = new ScpCommandFactory.Builder()
                    .withDelegate(new ProcessShellCommandFactory())
                    .build();
            sshd.setCommandFactory(commandFactory);
            sshd.setSubsystemFactories(Collections.singletonList(new SftpSubsystemFactory()));

            sshd.start();
            logger.info("SFTP Server started on port: {}", sshd.getPort());

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.info("Server is shutting down...");
                    Thread.currentThread().interrupt();
                }
            }
        } catch (IOException e) {
            logger.error("Failed to start the SSH server: {}", e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            logger.error("Unexpected error: {}", e.getMessage());
            System.exit(1);
        }
    }
}
