package f76goat.sftp.client.commands;

import f76goat.sftp.client.CommandHandler;
import java.io.IOException;

public class ListCommand implements Command {

    private final CommandHandler commandHandler;

    public ListCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        if (arguments.length > 0) {
            commandHandler.listFiles(arguments[0]);
        } else {
            System.out.println("\033[1;31mUsage: list <directory>\033[0m");
        }
    }
}
