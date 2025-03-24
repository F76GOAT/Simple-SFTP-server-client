package f76goat.sftp.client.commands;

import f76goat.sftp.client.CommandHandler;
import java.io.IOException;
import java.nio.file.Paths;

public class UploadCommand implements Command {

    private final CommandHandler commandHandler;

    public UploadCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length == 2) {
            String localFilePath = arguments[0];
            String remoteFilePath = arguments[1];
            commandHandler.uploadFile(Paths.get(localFilePath), remoteFilePath);
            System.out.println("\033[1;32mFile uploaded successfully.\033[0m");
        } else {
            System.out.println("\033[1;31mUsage: upload <localFilePath> <remoteFilePath>\033[0m");
        }
    }
}
