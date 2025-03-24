package f76goat.sftp.client.commands;

import java.io.IOException;

public interface Command {

    void execute(String[] arguments) throws IOException;
}
