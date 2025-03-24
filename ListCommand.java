package f76goat.sftp.client.commands;

import f76goat.sftp.client.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class ListCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(ListCommand.class);
    private final CommandHandler commandHandler;

    public ListCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length > 0) {
            String directory = arguments[0];
            System.out.println("Listing directory: " + directory);
            try {
                List<String> filenames = commandHandler.listFiles(directory);
                if (filenames.isEmpty()) {
                    System.out.println("No files found in directory: " + directory);
                } else {
                    System.out.println("Files in directory " + directory + ":");
                    for (String filename : filenames) {
                        System.out.println(filename);
                    }
                }
            } catch (IOException e) {
                logger.error("Failed to list directory: " + directory, e);
                throw e;
            }
        } else {
            System.out.println("\033[1;31mUsage: list <directory>\033[0m");
        }
    }
}
