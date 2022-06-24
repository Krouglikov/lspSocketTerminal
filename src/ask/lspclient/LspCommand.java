package ask.lspclient;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LspCommand {
    private String command;
    private String parameters;

    private static String getMessage(String message) {
        long length = message.length();
        return "Content-Length: " + length +
                "\n\n" +
                message;
    }

    public String getMessage() {
        return getMessage("{\"jsonrpc\":\"2.0\"," +
                "\"method\":\"" + command + "\"," +
                "\"params\":" + parameters + "," +
                "\"id\":1}\n");
    }
}
