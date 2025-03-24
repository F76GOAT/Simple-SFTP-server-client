package f76goat.sftp;

import f76goat.sftp.client.ClientStart;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bouncycastle.pkcs.PKCSException;

public class ClientApp {

    public static void run(String[] args) {
        try {
            ClientStart.clientStart();
        } catch (IOException | PKCSException | GeneralSecurityException e) {
            System.err.println("Error starting the client: " + e.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            Logger.getLogger(ClientApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
