package f76goat.sftp.server;

import org.apache.sshd.scp.server.ScpCommand;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.command.CommandFactory;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.common.util.threads.CloseableExecutorService;
import org.apache.sshd.scp.common.ScpFileOpener;
import org.apache.sshd.scp.common.ScpTransferEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScpCommandHandler implements CommandFactory {

    private static final Logger logger = LoggerFactory.getLogger(ScpCommandHandler.class);

    private final CloseableExecutorService executorService;
    private final ScpFileOpener fileOpener;
    private final ScpTransferEventListener transferEventListener;

    public ScpCommandHandler(CloseableExecutorService executorService, ScpFileOpener fileOpener, ScpTransferEventListener transferEventListener) {
        this.executorService = executorService;
        this.fileOpener = fileOpener;
        this.transferEventListener = transferEventListener;
    }

    @Override
    public Command createCommand(ChannelSession channel, String command) {
        logger.info("Received SCP command: {}", command);

        if (command.startsWith("scp -f ")) {
            String filePath = extractFilePath(command, "scp -f ");
            if (filePath == null) {
                logger.error("Invalid SCP fetch command: Missing file path");
                return null;
            }
            logger.info("SCP fetch command for file path: {}", filePath);

        } else if (command.startsWith("scp -t ")) {
            String filePath = extractFilePath(command, "scp -t ");
            if (filePath == null) {
                logger.error("Invalid SCP transfer command: Missing file path");
                return null;
            }
            logger.info("SCP transfer command for file path: {}", filePath);
        } else {
            logger.error("Unknown SCP command: {}", command);
            return null;
        }

        return new ScpCommand(channel, command, executorService, Integer.MAX_VALUE, Integer.MAX_VALUE, fileOpener, transferEventListener);
    }

    /**
     * Extracts the file path from the SCP command.
     *
     * @param command The SCP command string
     * @param prefix The prefix to be stripped (either "scp -f " or "scp -t ")
     * @return The file path if valid, null otherwise
     */
    private String extractFilePath(String command, String prefix) {
        if (command.length() <= prefix.length()) {
            logger.error("Invalid command, no file path provided.");
            return null;
        }
        return command.substring(prefix.length()).trim();
    }
}
