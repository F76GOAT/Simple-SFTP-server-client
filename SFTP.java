package f76goat.sftp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SFTP {

    private static final Logger logger = LoggerFactory.getLogger(SFTP.class);

    public static void main(String[] args) {
        if (args.length < 1) {
            logger.error("Usage: java -jar FTP.jar <server|client>");
            System.exit(1);
        }

        String choice = args[0].toLowerCase();
        if ("server".equals(choice)) {
            ServerApp.run(args);
        } else if ("client".equals(choice)) {
            ClientApp.run(args);
        } else {
            logger.error("Invalid choice. Please enter 'server' or 'client'.");
            System.exit(1);
        }
    }
}
