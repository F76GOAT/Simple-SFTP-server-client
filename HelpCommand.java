package f76goat.sftp.client.commands;

import f76goat.sftp.client.CommandHandler;
import java.io.IOException;

public class HelpCommand implements Command {

    private final CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
    }

    @Override
    public void execute(String[] arguments) throws IOException {
        commandHandler.listCommands();
    }
}
