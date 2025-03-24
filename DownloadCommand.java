package f76goat.sftp.client.commands;

import f76goat.sftp.client.CommandHandler;
import java.io.IOException;
import java.nio.file.Paths;

public class DownloadCommand implements Command {

    private final CommandHandler commandHandler;

    public DownloadCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length == 2) {
            String remoteFilePath = arguments[0];
            String localFilePath = arguments[1];
            commandHandler.downloadFile(remoteFilePath, Paths.get(localFilePath));
            System.out.println("\033[1;32mFile downloaded successfully.\033[0m");
        } else {
            System.out.println("\033[1;31mUsage: download <remoteFilePath> <localFilePath>\033[0m");
        }
    }
}
