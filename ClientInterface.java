package f76goat.sftp.client;

import java.util.Scanner;

public class ClientInterface {

    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;
    private static final int DEFAULT_PORT = 2222;
    private static final String DEFAULT_USERNAME = "user";
    private static final String DEFAULT_PRIVATE_KEY_DIR = "/home/user/.ssh";
    private static final Scanner scanner = new Scanner(System.in);

    public static String getHost() {
        System.out.print("Enter the host to connect to: ");
        return scanner.nextLine().trim();
    }

    public static int getPort() {
        while (true) {
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
                System.out.println("Invalid input. Please enter a valid port number.");
            }
        }
    }

    public static String getUsername() {
        System.out.print("Enter your username (default '" + DEFAULT_USERNAME + "'): ");
        String username = scanner.nextLine().trim();
        return username.isEmpty() ? DEFAULT_USERNAME : username;
    }

    public static String getPrivateKeyDir() {
        System.out.print("Enter your private key directory (default '" + DEFAULT_PRIVATE_KEY_DIR + "'): ");
        String privateKeyDir = scanner.nextLine().trim();
        return privateKeyDir.isEmpty() ? DEFAULT_PRIVATE_KEY_DIR : privateKeyDir;
    }
}
