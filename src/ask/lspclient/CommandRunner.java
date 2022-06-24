package ask.lspclient;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Optional;

public class CommandRunner implements AutoCloseable {

    //region Felds
    private Socket socket;

    private Scripts scripts;

    private InputStream inputStream;

    private InputStreamReader responseReader;
    //endregion

    public static CommandRunner onPort(int port) throws IOException {
        CommandRunner runner = new CommandRunner();
        runner.scripts = new Scripts();
        System.out.println("[Scripts read: " + runner.scripts.count() + "]");
        runner.socket = configureSocket(port);
        System.out.println("[Socket port " + port + " connected: " + runner.socket.isConnected() + "]");
        runner.inputStream = new BufferedInputStream(runner.socket.getInputStream());
        runner.responseReader = new InputStreamReader(runner.inputStream);

        return runner;
    }

    //region Private methods
    private static Socket configureSocket(int port) throws IOException {
        Socket socket = new Socket();
        SocketAddress address = new InetSocketAddress(port);
        //socket.bind(address);
        socket.connect(address);
        //SocketChannel channel = socket.getChannel();
        boolean connected = socket.isConnected();
        return socket;
    }

    private static void sendMessage(Socket socket, String command, String param) throws IOException {
        sendMessage(socket, new LspCommand(command, param));
    }

    private static void sendMessage(Socket socket, LspCommand command) throws IOException {
        write(socket, command.getMessage());
    }

    private static void write(Socket socket, String message) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        writer.append(message);
        System.out.println("[Writing message: " + message.length() + " symbols long]");
        writer.flush();
    }

    private static void awaitResponse(InputStreamReader reader) {
        System.out.println("[Awaiting response...]");
        try {
            //InputStreamReader reader = new InputStreamReader(inputStream);
            char[] buf = new char[200_000];
            String response = null;
            while (!reader.ready()) {
                Thread.sleep(10);
            }
            while (reader.ready()) {
                int read = reader.read(buf);
                response = String.copyValueOf(buf, 0, read);
            }
            System.out.println(response);
        } catch (Exception e) {
            System.out.println("[Response error: " + e.getMessage() + "]");
        }
    }

    public void runCommand(LspCommand command) {
        try {
            System.out.println("[Send command '" + command.getCommand() + "']");
            sendMessage(socket, command);
            awaitResponse(responseReader);
        } catch (IOException e) {
            System.out.println("[Error sending message: " + e.getMessage() + "]");
        }
    }

    public void runScript(String script) {
        Optional<Script> optionalScript = scripts.find(script);
        if (optionalScript.isPresent()) {
            System.out.println("[Running script '" + script + "']");
            optionalScript.get().stream().forEach(this::runCommand);
        } else {
            System.out.println("[Script '" + script + "' not found]");
        }
    }

    @Override
    public void close() throws Exception {
        if (responseReader != null) {
            responseReader.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }
        if (socket != null) {
            socket.close();
        }
    }
    //endregion

}
