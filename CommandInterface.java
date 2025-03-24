package f76goat.sftp.client;

import f76goat.sftp.client.commands.Command;
import java.io.IOException;
import java.util.Scanner;

public class CommandInterface {

    private final CommandHandler commandHandler;
    private final Scanner scanner;

    public CommandInterface(CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        this.scanner = new Scanner(System.in);
    }

    public void start() throws Exception {
        boolean running = true;

        while (running) {
            System.out.print("\033[1;32mSFTP> \033[0m");
            String commandLine = scanner.nextLine().trim();

            if (commandLine.isEmpty()) {
                continue;
            }

            String[] commandParts = commandLine.split("\\s+", 2);
            String command = commandParts[0].toLowerCase();
            String[] arguments = commandParts.length > 1 ? commandParts[1].split("\\s+", 2) : new String[]{};

            try {
                Command cmd = CommandHandlerFactory.getCommand(command, commandHandler);
                cmd.execute(arguments);
                if ("exit".equals(command)) {
                    running = false;
                }
            } catch (IOException e) {
                System.out.println("\033[1;31mError: " + e.getMessage() + "\033[0m");
            }
        }
    }
}
