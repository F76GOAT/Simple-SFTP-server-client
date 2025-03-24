package f76goat.sftp.client;

import f76goat.sftp.client.commands.*;

import java.util.HashMap;
import java.util.Map;

public class CommandHandlerFactory {

    private static final Map<String, Class<? extends Command>> commandMap = new HashMap<>();

    static {
        commandMap.put("download", DownloadCommand.class);
        commandMap.put("upload", UploadCommand.class);
        commandMap.put("exit", ExitCommand.class);
        commandMap.put("list", ListCommand.class);
        commandMap.put("help", HelpCommand.class);
    }

    public static Command getCommand(String command, CommandHandler handler) throws Exception {
        Class<? extends Command> commandClass = commandMap.get(command.toLowerCase());

        if (commandClass == null) {
            System.out.println("Unknown command: " + command);
            commandClass = HelpCommand.class;
        }

        try {
            if (commandClass != null) {
                return commandClass.getConstructor(CommandHandler.class).newInstance(handler);
            }
        } catch (NoSuchMethodException e) {
            return commandClass.getConstructor().newInstance();
        }

        return new HelpCommand(handler);
    }
}
