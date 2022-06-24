package ask.lspclient;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class Scripts {

    public static final Path SCRIPTS_DIRECTORY = Paths.get("scripts");

    private List<Script> scripts;

    public Scripts() {
        this.scripts = ScriptParser.parseAllFromDirectory(SCRIPTS_DIRECTORY);
    }

    public Optional<Script> find(String name) {
        return scripts.stream().filter(s -> s.getName().equals(name)).findFirst();
    }

    public int count() {
        return scripts.size();
    }

}
