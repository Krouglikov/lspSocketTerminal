package ask.lspclient;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Script {

    @Getter
    private final String name;

    private final List<LspCommand> commands = new LinkedList<>();

    public Script(String name) {
        this.name = name;
    }

    public Script addCommand(LspCommand command) {
        commands.add(command);
        return this;
    }

    public Stream<LspCommand> stream() {
        return commands.stream();
    }

}
