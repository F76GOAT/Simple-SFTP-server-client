package f76goat.sftp.client;

import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.channel.ClientChannelEvent;
import org.apache.sshd.client.session.ClientSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.EnumSet;
import java.util.stream.Collectors;
import org.apache.sshd.sftp.client.SftpClient;
import org.apache.sshd.sftp.client.SftpClientFactory;
import org.apache.sshd.sftp.common.SftpException;

public class CommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommandHandler.class);
    private final ClientSession session;

    public CommandHandler(ClientSession session) {
        this.session = session;
    }

    public List<String> listFiles(String directory) throws IOException {
        logger.info("Attempting to list files in directory: {}", directory);
        List<String> fileNames = new ArrayList<>();

        try (SftpClient sftp = SftpClientFactory.instance().createSftpClient(session)) {
            Iterable<SftpClient.DirEntry> entries = sftp.readDir(directory);
            for (SftpClient.DirEntry entry : entries) {
                fileNames.add(entry.getFilename());
            }
        } catch (SftpException e) {
            logger.error("SFTP error during directory listing for directory {}: {}", directory, e.getMessage());
            throw new IOException("Failed to list files in directory: " + directory, e);
        }

        if (fileNames.isEmpty()) {
            logger.warn("No files found in directory: {}", directory);
            throw new IOException("No files found in directory: " + directory);
        }

        return fileNames;
    }

    public void downloadFile(String remoteFilePath, Path localFilePath) throws IOException {
        logger.info("Downloading: {} → {}", remoteFilePath, localFilePath);

        try (SftpClient sftp = SftpClientFactory.instance().createSftpClient(session); SftpClient.CloseableHandle handle = sftp.open(remoteFilePath); var localFileStream = Files.newOutputStream(localFilePath)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long offset = 0;
            long totalSize = Files.size(localFilePath);

            while ((bytesRead = sftp.read(handle, offset, buffer, 0, buffer.length)) > 0) {
                localFileStream.write(buffer, 0, bytesRead);
                offset += bytesRead;
                logger.info("Downloaded {}/{}", offset, totalSize);
            }

            logger.info("File downloaded successfully.");
        } catch (SftpException e) {
            logger.error("SFTP error during download: {}", e.getMessage());
            throw new IOException("Failed to download file: " + remoteFilePath, e);
        }
    }

    public void uploadFile(Path localFilePath, String remoteFilePath) throws IOException {
        logger.info("Uploading: {} → {}", localFilePath, remoteFilePath);

        try (SftpClient sftp = SftpClientFactory.instance().createSftpClient(session); var localFileStream = Files.newInputStream(localFilePath); SftpClient.CloseableHandle handle = sftp.open(remoteFilePath, SftpClient.OpenMode.Write, SftpClient.OpenMode.Create)) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            long offset = 0;
            long totalSize = Files.size(localFilePath);

            while ((bytesRead = localFileStream.read(buffer)) > 0) {
                sftp.write(handle, offset, buffer, 0, bytesRead);
                offset += bytesRead;
                logger.info("Uploaded {}/{}", offset, totalSize);
            }

            logger.info("File uploaded successfully.");
        } catch (SftpException e) {
            logger.error("SFTP error during upload: {}", e.getMessage());
            throw new IOException("Failed to upload file: " + remoteFilePath, e);
        }
    }

    public void listCommands() {
        System.out.println("\n\033[1;34mAvailable commands:\033[0m");
        System.out.println("  \033[1;36mhelp\033[0m - List available commands");
        System.out.println("  \033[1;36mlist <directory>\033[0m - List files in the remote directory");
        System.out.println("  \033[1;36mdownload <remoteFilePath> <localFilePath>\033[0m - Download a file");
        System.out.println("  \033[1;36mupload <localFilePath> <remoteFilePath>\033[0m - Upload a file");
        System.out.println("  \033[1;36mexit\033[0m - Exit the program\n");
    }

    public List<String> executeCommand(String command) throws IOException {
        return executeCommand(command, 5000);
    }

    public List<String> executeCommand(String command, long timeoutMillis) throws IOException {
        logger.info("Executing command: {}", command);
        List<String> outputLines = new ArrayList<>();

        try (ChannelExec channel = session.createExecChannel(command); ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); ByteArrayOutputStream errorStream = new ByteArrayOutputStream()) {

            channel.setOut(outputStream);
            channel.setErr(errorStream);
            channel.open().verify();
            channel.waitFor(EnumSet.of(ClientChannelEvent.CLOSED), timeoutMillis);

            String output = outputStream.toString();
            String error = errorStream.toString();

            if (!output.isEmpty()) {
                outputLines.addAll(output.lines().collect(Collectors.toList()));
            }
            if (!error.isEmpty()) {
                outputLines.addAll(error.lines().collect(Collectors.toList()));
            }

            logger.info("Exit status: {}", channel.getExitStatus());
        } catch (Exception e) {
            logger.error("Error executing command: {}", e.getMessage());
            throw new IOException("Failed to execute command: " + command, e);
        }

        return outputLines;
    }

    public void shutdown() throws IOException {
        if (session.isOpen()) {
            session.close();
        }
    }

}
