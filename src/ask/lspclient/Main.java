package ask.lspclient;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static final int DEFAULT_PORT = 5008;

    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
            "\\s*[Ss]cript[\\s:\\\\/]\\s*(?<scriptName>\\w*)\\s*");
    private static final String[] COMMANDS = {
            "initialize",
    };

    public static void main(String[] args) throws IOException, InterruptedException {
        int port = getPort(args);
        try (CommandRunner executor = CommandRunner.onPort(port)) {
            while (true) {
                Scanner scanner = new Scanner(System.in);
                System.out.print("Command:>");
                String command = getCommand(scanner);
                Matcher scriptMatcher = SCRIPT_PATTERN.matcher(command);
                if (scriptMatcher.matches()) {
                    String scriptName = scriptMatcher.group("scriptName");
                    executor.runScript(scriptName);
                } else {
                    System.out.print("Param:>");
                    String param = getParam(scanner);
                    executor.runCommand(new LspCommand(command, param));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int getPort(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length < 2) {
            return port;
        }
        int i = 0;
        boolean found = false;
        do {
            if (args[i].equals("--port")) {
                found = true;
            } else {
                i++;
            }
        } while (!found && i < args.length);
        if (found) {
            port = Integer.parseInt(args[i + 1]);
        }
        return port;
    }

    private static String getCommand(Scanner scanner) {
        String command = scanner.nextLine();
        return Arrays.stream(COMMANDS)
                .filter(s -> s.startsWith(command))
                .findFirst()
                .orElse(command);
    }

    private static String getParam(Scanner scanner) {
        StringBuilder paramParts = new StringBuilder();
        String paramPart;
        do {
            paramPart = scanner.nextLine();
            paramParts.append(paramPart);
        } while (!Objects.equals(paramPart, ""));
        return paramParts.toString();
    }


}
