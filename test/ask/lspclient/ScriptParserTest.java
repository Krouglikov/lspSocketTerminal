package ask.lspclient;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ScriptParserTest {


    @Test
    void scriptPatternFitsScanner() {
        String script = "  Script:  azaza   \r\n   Command:";
        Scanner scanner = new Scanner(new ByteArrayInputStream(script.getBytes()));
        assertTrue(scanner.hasNext(ScriptParser.HEADER_PATTERN));
    }


    @Test
    void commandPatternFitsScanner() {
        String command = "  Command: azaza \r\n   Something";
        Scanner scanner = new Scanner(new ByteArrayInputStream(command.getBytes()));
        assertTrue(scanner.hasNext(ScriptParser.COMMAND_PATTERN));
    }

    @Test
    void paramPatternFitsScanner() {
        String parameter = "  Parameter:  { azaza,\n ololo, \n}  \n\n";
        Scanner scanner = new Scanner(new ByteArrayInputStream(parameter.getBytes()));
        assertTrue(scanner.hasNext(ScriptParser.PARAM_PATTERN));
    }

}