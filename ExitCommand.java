package f76goat.sftp.client.commands;

import f76goat.sftp.client.CommandHandler;
import java.io.IOException;

public class ExitCommand implements Command {

    private final CommandHandler commandHandler;

    public ExitCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        commandHandler.shutdown();
        System.out.println("\033[1;34mSession closed. Exiting...\033[0m");
    }
}
