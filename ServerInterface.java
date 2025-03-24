package f76goat.sftp.server;

import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerInterface {

    private static final Logger logger = LoggerFactory.getLogger(ServerInterface.class);

    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;
    private static final int DEFAULT_PORT = 2222;
    private static final int MAX_ATTEMPTS = 5;

    public static int getPort() {
        int attempts = 0;

        try (Scanner scanner = new Scanner(System.in)) {
            while (attempts < MAX_ATTEMPTS) {
                System.out.print("Enter a port (default " + DEFAULT_PORT + "): ");
                String userPort = scanner.nextLine().trim();

                if (userPort.isEmpty()) {
                    return DEFAULT_PORT;
                }

                try {
                    int portNum = Integer.parseInt(userPort);
                    if (portNum >= MIN_PORT && portNum <= MAX_PORT) {
                        return portNum;
                    } else {
                        System.out.println("Port must be between " + MIN_PORT + " and " + MAX_PORT);
                    }
                } catch (NumberFormatException e) {
                    attempts++;
                    logger.warn("Invalid port input attempt #{}: {}", attempts, userPort);
                    System.out.println("Invalid input. Please enter a valid port number.");
                }

                if (attempts >= MAX_ATTEMPTS) {
                    System.out.println("Too many invalid attempts. Exiting...");
                    logger.error("Too many invalid port input attempts. Exiting.");
                    System.exit(1);
                }
            }
        }

        return DEFAULT_PORT;
    }
}
