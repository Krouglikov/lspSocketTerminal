package ask.lspclient;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class ScriptParser {

    static final Pattern HEADER_PATTERN = Pattern.compile("Script:");

    static final Pattern COMMAND_PATTERN = Pattern.compile("Command:");
    static final Pattern PARAM_PATTERN = Pattern.compile("Parameter:");

    private ScriptParser() {
    }

    public static Optional<Script> parseOne(Scanner scanner) {
        Optional<Script> result = Optional.empty(); // if nothing works
        if (scanner.hasNext(HEADER_PATTERN)) {
            scanner.next(HEADER_PATTERN);
            String scriptName = scanner.next();
            Script script = new Script(scriptName);
            // while commands present, parse them
            while (scanner.hasNext(COMMAND_PATTERN)) {
                scanner.next(COMMAND_PATTERN);
                String commandName = scanner.next();
                StringBuilder parameter = new StringBuilder();
                if (scanner.hasNext(PARAM_PATTERN)) {
                    String paramString = scanner.next(PARAM_PATTERN);
                    String nextLine;
                    do {
                        try {
                            nextLine = scanner.nextLine();
                        } catch (Exception e) {
                            nextLine = "";
                        }
                        parameter.append(nextLine);
                    } while (!nextLine.isEmpty());
                }
                script.addCommand(new LspCommand(commandName, parameter.toString()));
            }
            result = Optional.of(script);
        }
        return result;
    }

    public static List<Script> parseAll(Scanner scanner) {
        List<Script> result = new LinkedList<>();
        boolean done = false;
        do {
            Optional<Script> script = parseOne(scanner);
            if (script.isPresent()) {
                result.add(script.get());
            } else {
                done = true;
            }
        } while (!done);
        return result;
    }

    public static List<Script> parseAll(InputStream input) {
        return parseAll(new Scanner(input));
    }

    public static List<Script> parseAll(Path file) {
        try {
            return parseAll(Files.newInputStream(file));
        } catch (IOException e) {
            System.out.println("Error reading file " + file + "\n" + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static List<Script> parseAllFromDirectory(Path directory) {
        List<Script> result = new LinkedList<>();
        if (Files.isDirectory(directory)) {
            try (Stream<Path> files = Files.list(directory)) {
                files.map(ScriptParser::parseAll)
                        .forEach(result::addAll);
            } catch (IOException e) {
                System.out.println("Error parsing scrpits: " + e.getMessage());
            }
        }
        return result;
    }

}
